import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        int threadsNumber = Integer.parseInt(args[0]);

        double[][] imageMatrix = Utils.readMatrixFromFile(Path.of("D:\\Proiecte\\Java\\PPD\\lab2\\resources\\input\\imageMatrix.in"));
        double[][] filterMatrix = Utils.readMatrixFromFile(Path.of("D:\\Proiecte\\Java\\PPD\\lab2\\resources\\input\\filterMatrix.in"));
        int M = imageMatrix.length;
        int N = imageMatrix[0].length;
        int m = filterMatrix.length;
        int n = filterMatrix[0].length;

        long startTime = System.nanoTime();
        Solver.solveWithMultipleThreads(imageMatrix, filterMatrix, threadsNumber);
        long endTime = System.nanoTime();

        Utils.writeMatrixToFile(imageMatrix, Path.of("D:\\Proiecte\\Java\\PPD\\lab2\\resources\\output\\" + String.format("filteredMatrix-M%d-N%d-m%d-n%d-th%d", M, N, m, n, threadsNumber)));

        System.out.println(endTime - startTime);
    }
}
