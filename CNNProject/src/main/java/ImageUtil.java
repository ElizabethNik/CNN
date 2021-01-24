import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ImageUtil {
    private float[][] image;
    private int[][] filter1;
    private int[][] filter2;
    private int[][] filter3;

    ImageUtil(String csvFile) {
        image = new float[9][9];
        filter1 = new int[3][3];
        filter2 = new int[3][3];
        filter3 = new int[3][3];

        populateImageAndFilters(csvFile);
    }

    private void populateImageAndFilters(String csvFile) {
        try {
            CSVReader reader = new CSVReader(new FileReader(csvFile));
            String[] line;
            int row = 0;
            while ((line = reader.readNext()) != null) {
                for (int col = 0; col < line.length; col++) {
                    image[row][col] = Integer.parseInt(line[col]);
                    if (row >= 1 && row <= 3 && col >= 1 && col <= 3) {
                        filter1[row - 1][col - 1] = Integer.parseInt(line[col]);
                    }
                    if (row >= 3 && row <= 5 && col >= 3 && col <= 5) {
                        filter2[row - 3][col - 3] = Integer.parseInt(line[col]);
                    }
                    if (row >= 5 && row <= 7 && col >= 1 && col <= 3) {
                        filter3[row - 5][col - 1] = Integer.parseInt(line[col]);
                    }
//                    if (row >= 5 && row <= 7 && col >= 1 && col <= 3) {
//                        filter4[row - 5][col - 1] = Integer.parseInt(line[row]);
//                    }
//                    if (row >= 5 && row <= 7 && col >= 5 && col <= 7) {
//                        filter4[row - 5][col - 5] = Integer.parseInt(line[row]);
//                    }
                }
                row++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Float> getImageList() {
//        float[][] result1 = Image;
//        float[][] result2 = Image;
//        float[][] result3 = Image;
//        float[][] result4 = Image;
//        float[][] result5 = Image;
//
//        while (result1.length > 2) {
//            result1 = CNNAlgorithm.convulateNew(result1, filter1);
//            result2 = CNNAlgorithm.convulateNew(result2, filter2);
//            result3 = CNNAlgorithm.convulateNew(result3, filter3);
//            result4 = CNNAlgorithm.convulateNew(result4, filter4);
//            result5 = CNNAlgorithm.convulateNew(result5, filter5);
//
//            CNNAlgorithm.relu(result1);
//            CNNAlgorithm.relu(result2);
//            CNNAlgorithm.relu(result3);
//            CNNAlgorithm.relu(result4);
//            CNNAlgorithm.relu(result5);
//
//            result1 = CNNAlgorithm.pool(result1);
//            result2 = CNNAlgorithm.pool(result2);
//            result3 = CNNAlgorithm.pool(result3);
//            result4 = CNNAlgorithm.pool(result4);
//            result5 = CNNAlgorithm.pool(result5);
//        }

        float[][] image1_f2_convulated = CNNAlgorithm.convulateNew(image, filter1);

        float[][] image1_f3_convulated = CNNAlgorithm.convulateNew(image, filter2);
        float[][] image1_f4_convulated = CNNAlgorithm.convulateNew(image, filter3);

        CNNAlgorithm.relu(image1_f2_convulated);
        CNNAlgorithm.relu(image1_f3_convulated);
        CNNAlgorithm.relu(image1_f4_convulated);

        float[][] image1_f2_pooled = CNNAlgorithm.fullPool(image1_f2_convulated);
        float[][] image1_f3_pooled = CNNAlgorithm.fullPool(image1_f3_convulated);
        float[][] image1_f4_pooled = CNNAlgorithm.fullPool(image1_f4_convulated);

        return CNNAlgorithm.generateImageList(image1_f2_pooled, image1_f3_pooled, image1_f4_pooled);
    }
}
