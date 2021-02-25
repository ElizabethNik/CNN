import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ImageUtil {
    private final int[][] image;
    private final int[][] filter1;
    private final int[][] filter2;
    private final int[][] filter3;

    ImageUtil(String csvFile) {
        image = new int[9][9];
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
                }
                row++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Float> getImageList() {

        float[][] f1_convulated = CNNAlgorithm.convulate(image, filter1);
        float[][] f2_convulated = CNNAlgorithm.convulate(image, filter2);
        float[][] f3_convulated = CNNAlgorithm.convulate(image, filter3);

        CNNAlgorithm.relu(f1_convulated);
        CNNAlgorithm.relu(f2_convulated);
        CNNAlgorithm.relu(f3_convulated);

        float[][] f1_pooled = CNNAlgorithm.fullPool(f1_convulated);
        float[][] f2_pooled = CNNAlgorithm.fullPool(f2_convulated);
        float[][] f3_pooled = CNNAlgorithm.fullPool(f3_convulated);

        return CNNAlgorithm.generateImageList(f1_pooled, f2_pooled, f3_pooled);
    }
}
