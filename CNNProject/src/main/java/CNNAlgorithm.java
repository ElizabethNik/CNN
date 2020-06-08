import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class CNNAlgorithm {

    public static void main(String[] args) {
        int[][] face1 = new int[10][10];
        int[][] face1_f1 = new int[5][5];
        int[][] face1_f2 = new int[5][5];
        int[][] face1_f3 = new int[5][10];

        String face1CsvFile = "src/face1";
        populateFaceAndFilters(face1CsvFile, face1, face1_f1, face1_f2, face1_f3);
        getFaceList(face1, face1_f1, face1_f2, face1_f3);

        int[][] face2 = new int[10][10];
        int[][] face2_f1 = new int[5][5];
        int[][] face2_f2 = new int[5][5];
        int[][] face2_f3 = new int[5][10];

        String face2CsvFile = "src/face2";
        populateFaceAndFilters(face2CsvFile, face2, face2_f1, face2_f2, face2_f3);
        getFaceList(face2, face2_f1, face2_f2, face2_f3);

        ArrayList<Float> face1List = getFaceList(face1, face1_f1, face1_f2, face1_f3);


        //egati
        int egati = 1;

        ArrayList<Float> face2List = new ArrayList<Float>();
        face2List.addAll(Arrays.asList(0.9f, 0.1f, 0.04f, 0.0f, 0.0f, 0.0f, 0.0f, 0.3f, 0.5f, 0.1f, 0.03f, 0.12f));

        ArrayList<Float> face3List = new ArrayList<Float>();
        face3List.addAll(Arrays.asList(0.9f, 0.7f, 0.54f, 0.76f, 0.8f, 0.4f, 0.7f, 0.6f, 0.9f, 0.56f, 0.83f, 0.77f));

        System.out.println("Comparison between face1 and face2: " + getConformityLevel(face1List, face2List));
        System.out.println("Comparison between face1 and face3: " + getConformityLevel(face1List, face3List));
    }

    private static ArrayList<Float> getFaceList(int[][] face, int[][] filter1, int[][] filter2, int[][] filter3) {
        float[][] face1_f1_convulated = convulate(face, filter1);
        float[][] face1_f2_convulated = convulate(face, filter2);
        float[][] face1_f3_convulated = convulate(face, filter3);

        relu(face1_f1_convulated);
        relu(face1_f2_convulated);
        relu(face1_f3_convulated);

        float[][] face1_f1_pooled = fullPool(face1_f1_convulated);
        float[][] face1_f2_pooled = fullPool(face1_f2_convulated);
        float[][] face1_f3_pooled = fullPool(face1_f3_convulated);

        return generateFaceList(face1_f1_pooled, face1_f2_pooled, face1_f3_pooled);
    }

    private static void populateFaceAndFilters(
            String csvFile,
            int[][] face,
            int[][] filter1,
            int[][] filter2,
            int[][] filter3) {
        try {
            CSVReader reader = new CSVReader(new FileReader(csvFile));
            String[] line;
            int j = 0;
            while ((line = reader.readNext()) != null) {
                for(int i = 0; i < line.length; i++) {
                    face[j][i] = Integer.parseInt(line[i]);
                    if(i <= 4 && j <= 4) {
                        filter1[j][i] = Integer.parseInt(line[i]);
                    }else if(i >= 5 && j <= 4) {
                        filter2[j][i-5] = Integer.parseInt(line[i]);
                    }else {
                        filter3[j-5][i] = Integer.parseInt(line[i]);
                    }
                }
                j++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void populateFace(
            String csvFile,
            int[][] face) {
        try {
            CSVReader reader = new CSVReader(new FileReader(csvFile));
            String[] line;
            int j = 0;
            while ((line = reader.readNext()) != null) {
                for(int i = 0; i < line.length; i++) {
                    face[j][i] = Integer.parseInt(line[i]);
                }
                j++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static float[][] convulate(
            int[][] face,
            int[][] filter) {
        float[][] avg = new float[face.length][face[0].length];

        for(int faceRow = 0; faceRow < face.length; faceRow++) {
            for (int faceColumn = 0; faceColumn < face[0].length; faceColumn++) {
                float sum = 0f;
                int count = 0;

                for (int localRow = faceRow; localRow < faceRow + filter.length && localRow < face.length; localRow++) {
                    for(int localColumn = faceColumn; localColumn < faceColumn + filter[0].length && localColumn < face[0].length; localColumn++) {
                        sum += face[localRow][localColumn] * filter[localRow-faceRow][localColumn-faceColumn];
                        count++;
                    }
                }
                avg[faceRow][faceColumn] = sum/count;
            }
        }
        return avg;
    }

    private static void relu(float[][] array) {
        for(float[] arr:array) {
            for(int i = 0; i < arr.length; i++) {
                arr[i] = arr[i] < 0 ? 0:arr[i];
            }
        }
    }

    private static float[][] fullPool(float[][] array) {
        float[][] pooledArray = array;
        while(pooledArray.length > 2) {
            pooledArray = pool(pooledArray);
        }
        return pooledArray;
    }

    private static float[][] pool(float[][] array) {
        int newSize = (int) Math.ceil(array.length / 2.0);
        float[][] pooledArray = new float[newSize][newSize];

        for(int i = 0, m = 0; i < array.length; i+=2, m++) {
            for(int j = 0, n = 0; j < array[0].length; j+=2, n++) {
                ArrayList<Float> elements = new ArrayList<Float>();
                if(j == array[0].length - 1) {
                    if(i == array.length - 1) {
                        elements.addAll(Arrays.asList(array[i][j]));
                        pooledArray[m][n] = Collections.max(elements);
                        break;
                    }
                    elements.addAll(Arrays.asList(array[i][j], array[i+1][j]));
                    pooledArray[m][n] = Collections.max(elements);
                    continue;
                }
                if(i == array.length - 1) {
                    elements.addAll(Arrays.asList(array[i][j], array[i][j+1]));
                    pooledArray[m][n] = Collections.max(elements);
                    continue;
                }
                elements.addAll(Arrays.asList(array[i][j], array[i+1][j], array[i][j+1], array[i+1][j+1]));
                pooledArray[m][n] = Collections.max(elements);
            }
        }
        return pooledArray;
    }

    private static ArrayList<Float> generateFaceList(float[][] ...arrays) {
        ArrayList<Float> faceList = new ArrayList();
        for(float[][] arr : arrays) {
            faceList.addAll(Arrays.asList(arr[0][0], arr[0][1], arr[1][0], arr[1][1]));
        }
        return faceList;
    }

    private static float getConformityLevel(
            ArrayList<Float> original,
            ArrayList<Float> face
    ) {
        float originalSum = 0;
        float faceSum = 0;
        for(int i = 0; i < original.size(); i++) {
            if(original.get(i) == 1) {
                originalSum++;
                faceSum += face.get(i);
            }
        }
        return faceSum/originalSum;
    }

}
