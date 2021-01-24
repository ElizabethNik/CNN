import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class CNNAlgorithm {
    ArrayList<Float> firstImageList;
    ArrayList<Float> secondImageList;
    ArrayList<Float> thirdImageList;

    CNNAlgorithm(String firstImageCSVFile, String secondImageCSVFile, String thirdImageCSVFile) {
        ImageUtil firstImage = new ImageUtil(firstImageCSVFile);
        firstImageList = firstImage.getImageList();

        ImageUtil secondImage = new ImageUtil(secondImageCSVFile);
        secondImageList = secondImage.getImageList();

        ImageUtil thirdImage = new ImageUtil(thirdImageCSVFile);
        thirdImageList = thirdImage.getImageList();
    }

    public void perform(String testImageCSVFile) {
        ImageUtil testImage = new ImageUtil(testImageCSVFile);
        ArrayList<Float> testImageList = testImage.getImageList();

        System.out.println("----- FIRST IMAGE LIST -----");
        for (float value : firstImageList)
            System.out.println(String.format("%.2f", value));

        System.out.println("----- SECOND IMAGE LIST -----");
        for (float value : secondImageList)
            System.out.println(String.format("%.2f", value));

        System.out.println("----- THIRD IMAGE LIST -----");
        for (float value : thirdImageList)
            System.out.println(String.format("%.2f", value));

        System.out.println("----- TEST IMAGE LIST -----");
        for (float value : testImageList)
            System.out.println(String.format("%.2f", value));

        float similarityWithFIRST = getConformityLevel(firstImageList, testImageList);
        float similarityWithSECOND = getConformityLevel(secondImageList, testImageList);
        float similarityWithTHIRD = getConformityLevel(thirdImageList, testImageList);

        System.out.println("\nComparison with FIRST image: " + String.format("%.2f", similarityWithFIRST));
        System.out.println("Comparison with SECOND image: " + String.format("%.2f", similarityWithSECOND));
        System.out.println("Comparison with THIRD image: " + String.format("%.2f", similarityWithTHIRD));

        String recognizedImage = "";
        if (similarityWithFIRST > similarityWithSECOND) {
            if (similarityWithFIRST > similarityWithTHIRD) {
                recognizedImage = "FIRST";
            } else if (similarityWithFIRST < similarityWithTHIRD) {
                recognizedImage = "THIRD";
            } else {
                recognizedImage = "FIRST and THIRD";
            }
        } else if (similarityWithFIRST < similarityWithSECOND) {
            if (similarityWithSECOND > similarityWithTHIRD) {
                recognizedImage = "SECOND";
            } else if (similarityWithSECOND < similarityWithTHIRD) {
                recognizedImage = "THIRD";
            } else {
                recognizedImage = "SECOND and THIRD";
            }
        } else {
            if (similarityWithSECOND > similarityWithTHIRD) {
                recognizedImage = "FIRST and SECOND";
            } else if (similarityWithSECOND < similarityWithTHIRD) {
                recognizedImage = "THIRD";
            } else {
                recognizedImage = "ALL";
            }
        }

        System.out.println("\nRecognized as class: " + recognizedImage);
    }

    public static float[][] convulateNew(float[][] image, int[][] filter) {
        int imageRows = image.length;
        int filterRows = filter.length;
        int imageCols = image[0].length;
        int filterCols = filter[0].length;
        int resultRows = imageRows - filterRows + 1;
        int resultCols = imageCols - filterCols + 1;

        float[][] average = new float[resultRows][resultCols];

        // Loop through every pixel in the Image, while filter's end is not equal to the Image's end
        for (int imageRow = 0; imageRow < resultRows; imageRow++) {
            for (int imageCol = 0; imageCol < resultCols; imageCol++) {
                float sum = 0;
                float pixelCount = 0;

                // Loop through every pixel in the shifted filter zone
                // Multiply the value with the original filter pixel and calculate avarage
                for (int currentRow = imageRow; currentRow < imageRow + filterRows; currentRow++) {
                    for (int currentCol = imageCol; currentCol < imageCol + filterCols; currentCol++) {
                        float imagePixelValue = image[currentRow][currentCol];
                        int filterPixelValue = filter[currentRow - imageRow][currentCol - imageCol];
                        sum += imagePixelValue * filterPixelValue;
                        pixelCount++;
                    }
                }
                average[imageRow][imageCol] = sum / pixelCount;
            }
        }
        return average;
    }

    public static void relu(float[][] array) {
        for (float[] arr : array) {
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] < 0)
                    arr[i] = 0;
            }
        }
    }

    public static float[][] fullPool(float[][] array) {
        float[][] pooledArray = array;
        while (pooledArray.length > 2) {
            pooledArray = pool(pooledArray);
        }
        return pooledArray;
    }

    public static float[][] pool(float[][] array) {
        if (array.length == 2)
            return array;

        int newSize = (int) Math.ceil(array.length / 2.0);
        float[][] pooledArray = new float[newSize][newSize];

        for (int row = 0; row < array.length; row += 2) {
            for (int col = 0; col < array[0].length; col += 2) {
                ArrayList<Float> elements = new ArrayList<Float>();
                elements.add(array[row][col]);

                if ((col + 1) < array[0].length)
                    elements.add(array[row][col + 1]);

                if ((row + 1) < array.length) {
                    elements.add(array[row + 1][col]);
                    if ((col + 1) < array[0].length)
                        elements.add(array[row + 1][col + 1]);
                }

                pooledArray[row / 2][col / 2] = Collections.max(elements);
            }
        }
        return pooledArray;
    }

    public static ArrayList<Float> generateImageList(float[][]... arrays) {
        ArrayList<Float> imageList = new ArrayList();
        for (float[][] arr : arrays) {
            imageList.addAll(Arrays.asList(arr[0][0], arr[0][1], arr[1][0], arr[1][1]));
        }
        return imageList;
    }

    public static float getConformityLevel(ArrayList<Float> testImageList, ArrayList<Float> originalImageList) {
        float testImageSum = 0;
        float originalImageSum = 0;
        for (int i = 0; i < testImageList.size(); i++) {
            if (testImageList.get(i) == 1) {
                testImageSum++;
                originalImageSum += originalImageList.get(i);
            }
        }
        return originalImageSum / testImageSum;
    }
}