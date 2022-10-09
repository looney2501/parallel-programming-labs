import java.io.IOException;
import java.nio.file.Path;

public class Solver {
    private final int M;
    private final int N;
    private final int m;
    private final int n;
    private final int testNumber;
    private double[][] imageMatrix;
    private double[][] filterMatrix;

    public Solver(int M, int N, int m, int n, int testNumber) {
        this.M = M;
        this.N = N;
        this.m = m;
        this.n = n;
        this.testNumber = testNumber;
    }

    public void setImageMatrix(double[][] imageMatrix) {
        this.imageMatrix = imageMatrix;
    }

    public void setFilterMatrix(double[][] filterMatrix) {
        this.filterMatrix = filterMatrix;
    }

    public void generateInputData() throws IOException {
        System.out.println("Generating matrix of pixels...");
        this.imageMatrix = Utils.generateRandomMatrix(M, N);
        Utils.writeInputMatrixToFile(imageMatrix, Path.of(String.format("lab1\\resources\\test%d\\imageMatrix.in", testNumber)));
        System.out.println("Done!");

        System.out.println("Generating matrix of filters...");
        this.filterMatrix = Utils.generateRandomMatrix(m, n);
        Utils.writeInputMatrixToFile(filterMatrix, Path.of(String.format("lab1\\resources\\test%d\\filterMatrix.in", testNumber)));
        System.out.println("Done!");
    }

    private void writeResultToFile(double [][] matrix, String type) throws IOException {
        System.out.println("Writing filtered matrix to file...");
        Utils.writeMatrixToFile(matrix, Path.of(String.format("lab1\\resources\\test%d\\filteredMatrix%s.out", testNumber, type)));
        System.out.println("Done!");
    }

    private double[][] solveWithFilterSizeThree() {
        double[][] filteredMatrix = new double[M][N];
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                filteredMatrix[i][j] = 0.0;
                for (int k = -1; k < 2; k++) {
                    for (int l = -1; l < 2; l++) {
                        double initialImagePixel;
                        if (i - k == -1 && j - l == -1 || i - k == M && j - l == N || i - k == -1 && j - l == N || i - k == M && j - l == -1) {
                            initialImagePixel = imageMatrix[i][j];
                        }
                        else if (i - k == -1 || i - k == M) initialImagePixel = imageMatrix[i][j-l];
                        else if (j - l == -1 || j - l == N) initialImagePixel = imageMatrix[i-k][j];
                        else initialImagePixel = imageMatrix[i-k][j-l];

                        filteredMatrix[i][j] += filterMatrix[1-k][1-l] * initialImagePixel;
                    }
                }
            }
        }
        return filteredMatrix;
    }

    private double[][] solveWithFilterSizeFive() {
        double[][] filteredMatrix = new double[M][N];
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                filteredMatrix[i][j] = 0;
                for (int k = -2; k < 3; k++) {
                    for (int l = -2; l < 3; l++) {
                        double initialImagePixel = 0;
                        if (i - k <= -1 && j - l <= -1 || i - k >= M && j - l >= N || i - k <= -1 && j - l >= N || i - k >= M && j - l <= -1) {
                            initialImagePixel = imageMatrix[i][j];
                        }
                        else if (i - k <= -1 || i - k >= M) initialImagePixel = imageMatrix[i][j-l];
                        else if (j - l <= -1 || j - l >= N) initialImagePixel = imageMatrix[i-k][j];
                        else initialImagePixel = imageMatrix[i-k][j-l];

                        filteredMatrix[i][j] += filterMatrix[2-k][2-l] * initialImagePixel;
                    }
                }
            }
        }
        return filteredMatrix;
    }

    public void sequentialSolve() throws IOException {
        double [][] filteredMatrix = (n == m && n == 3) ? solveWithFilterSizeThree() : solveWithFilterSizeFive();

        writeResultToFile(filteredMatrix, "Sequential");
    }
}
