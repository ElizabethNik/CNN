public class Main {
    public static void main(String[] args) {
        CNNAlgorithm algorithm = new CNNAlgorithm("src/resources/originalO",
                "src/resources/originalP",
                "src/resources/originalX");
        algorithm.perform("src/resources/testO_1");
    }
}
