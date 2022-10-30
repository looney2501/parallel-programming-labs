import java.io.IOException;
import java.nio.file.Path;

public final class InputDataGenerator {
    private static void lab1Generator(int M, int N, int m, int n) {
        System.out.println("Generating matrix of pixels...");
        double[][] imageMatrix = Utils.generateRandomMatrix(M, N);
        try {
            Utils.writeInputMatrixToFile(imageMatrix, Path.of("lab1\\resources\\input\\imageMatrix.in"));
            Utils.writeInputMatrixToFile(imageMatrix, Path.of("lab2\\resources\\input\\imageMatrix.in"));
            System.out.println("Done!");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        System.out.println("Generating matrix of filters...");
        double[][] filterMatrix = Utils.generateRandomMatrix(m, n);
        try {
            Utils.writeInputMatrixToFile(filterMatrix, Path.of("lab1\\resources\\input\\filterMatrix.in"));
            Utils.writeInputMatrixToFile(filterMatrix, Path.of("lab2\\resources\\input\\filterMatrix.in"));
            System.out.println("Done!");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        lab1Generator(100, 100, 5, 5);
    }
}
