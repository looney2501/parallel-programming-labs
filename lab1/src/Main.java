import java.io.IOException;
import java.nio.file.Path;

public class Main {
    private static void solveWithFilterSizeThree(double[][] imageMatrix, double[][] filterMatrix, double[][] filteredMatrix, int start, int end, int M, int N) {
        for (int i = start; i < end; i++) {
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
    }

    private static void solveWithFilterSizeFive(double[][] imageMatrix, double[][] filterMatrix, double[][] filteredMatrix, int start, int end, int M, int N) {
        for (int i = start; i < end; i++) {
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
    }

    private static void solve(double[][] imageMatrix, double[][] filterMatrix, double[][] filteredMatrix, int start, int end, int M, int N, int m, int n) {
        if ((m == n && n == 3)) {
            solveWithFilterSizeThree(imageMatrix, filterMatrix, filteredMatrix, start, end, M, N);
        } else {
            solveWithFilterSizeFive(imageMatrix, filterMatrix, filteredMatrix, start, end, M, N);
        }
    }

    private static class MyThread extends Thread {
        private final double[][] imageMatrix;
        private final double[][] filterMatrix;
        private final double[][] filteredMatrix;
        private final int start;
        private final int end;
        private final int M;
        private final int N;
        private final int m;
        private final int n;

        public MyThread(double[][] imageMatrix, double[][] filterMatrix, double[][] filteredMatrix, int start, int end, int M, int N, int m, int n) {
            this.imageMatrix = imageMatrix;
            this.filterMatrix = filterMatrix;
            this.filteredMatrix = filteredMatrix;
            this.start = start;
            this.end = end;
            this.M = M;
            this.N = N;
            this.m = m;
            this.n = n;
        }

        private void solveWithFilterSizeThree(double[][] imageMatrix, double[][] filterMatrix, double[][] filteredMatrix, int start, int end, int M, int N) {
            for (int i = start; i < end; i++) {
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
        }

        private void solveWithFilterSizeFive(double[][] imageMatrix, double[][] filterMatrix, double[][] filteredMatrix, int start, int end, int M, int N) {
            for (int i = start; i < end; i++) {
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
        }

        private void solve(double[][] imageMatrix, double[][] filterMatrix, double[][] filteredMatrix, int start, int end, int M, int N, int m, int n) {
            if ((m == n && n == 3)) {
                solveWithFilterSizeThree(imageMatrix, filterMatrix, filteredMatrix, start, end, M, N);
            } else {
                solveWithFilterSizeFive(imageMatrix, filterMatrix, filteredMatrix, start, end, M, N);
            }
        }

        @Override
        public void run() {
            solve(imageMatrix, filterMatrix, filteredMatrix, start, end, M, N, m, n);
        }
    }

    private static void solveMultipleThreads(int threadsNumber, double[][] imageMatrix, double[][] filterMatrix, double[][] filteredMatrix, int M, int N, int m, int n) {
        MyThread[] threads = new MyThread[threadsNumber];

        int start = 0;
        int chunk_size = M / threadsNumber;
        int end = chunk_size;
        int r = M % threadsNumber;

        for (int i = 0; i < threadsNumber; i++) {
            if (r > 0) {
                end++;
                r--;
            }
            threads[i] = new MyThread(imageMatrix, filterMatrix, filteredMatrix, start, end, M, N, m, n);
            threads[i].start();
            start = end;
            end += chunk_size;
        }

        for (int i = 0; i < threadsNumber; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        int threadsNumber = Integer.parseInt(args[0]);

        double[][] imageMatrix = Utils.readMatrixFromFile(Path.of("D:\\Proiecte\\Java\\PPD\\lab1\\resources\\input\\imageMatrix.in"));
        double[][] filterMatrix = Utils.readMatrixFromFile(Path.of("D:\\Proiecte\\Java\\PPD\\lab1\\resources\\input\\filterMatrix.in"));
        int M = imageMatrix.length;
        int N = imageMatrix[0].length;
        int m = filterMatrix.length;
        int n = filterMatrix[0].length;
        double[][] filteredMatrix = new double[M][N];

        long startTime, endTime;

        if (threadsNumber == 0) {
            startTime = System.nanoTime();
            solve(imageMatrix, filterMatrix, filteredMatrix, 0, M, M, N, m, n);
            endTime = System.nanoTime();
        }
        else {
            startTime = System.nanoTime();
            solveMultipleThreads(threadsNumber, imageMatrix, filterMatrix, filteredMatrix, M, N, m, n);
            endTime = System.nanoTime();
        }

        Utils.writeMatrixToFile(filteredMatrix, Path.of("D:\\Proiecte\\Java\\PPD\\lab1\\resources\\output\\" + String.format("filteredMatrix-M%d-N%d-m%d-n%d-th%d", M, N, m, n, threadsNumber)));

        System.out.println((double)(endTime - startTime)/1E6);
    }
}
