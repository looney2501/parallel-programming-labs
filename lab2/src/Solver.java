import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Solver {

    private static class MyThread extends Thread {
        private final double[][] imageMatrix;
        private final double[][] filterMatrix;
        private double[][] frontierMatrix;
        private int topFrontierLength;
        private final int start;
        private final int end;
        private final int M;
        private final int N;
        private final int m;
        private final int n;
        private final CyclicBarrier barrier;

        public MyThread(double[][] imageMatrix, double[][] filterMatrix, int start, int end, CyclicBarrier barrier) {
            this.imageMatrix = imageMatrix;
            this.filterMatrix = filterMatrix;
            this.start = start;
            this.end = end;
            this.M = imageMatrix.length;
            this.N = imageMatrix[0].length;
            this.m = filterMatrix.length;
            this.n = filterMatrix[0].length;
            this.barrier = barrier;
        }

        @Override
        public void run() {
            createFrontierMatrix();
            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
            solve();
        }

        private void createFrontierMatrix() {
            if ((m == n && n == 3)) {
                createFrontierMatrixSizeThree();
            } else {
                createFrontierMatrixSizeFive();
            }
        }

        private void createFrontierMatrixSizeThree() {
            int linesNumber = end - start;
            //frontiera ocupa toata matricea
            if (start == 0 && end == M) {
                topFrontierLength = 0;
                frontierMatrix = new double[linesNumber][N];
                for (int i = start; i < end; i++) {
                    frontierMatrix[i-start] = imageMatrix[i].clone();
                }
            //frontiera este intr-o margine, dar nu ocupa toata marginea
            } else if ((start == 0 || end == M) && linesNumber != M) {
                frontierMatrix = new double[linesNumber+1][N];
                if (start == 0) {
                    topFrontierLength = 0;
                    for (int i = start; i < end + 1; i++) {
                        frontierMatrix[i-start] = imageMatrix[i].clone();
                    }
                }
                if (end == M) {
                    topFrontierLength = 1;
                    for (int i = start - 1; i < end; i++) {
                        frontierMatrix[i-start+1] = imageMatrix[i].clone();
                    }
                }
            //frontiera nu este in margine
            } else {
                topFrontierLength = 1;
                frontierMatrix = new double[linesNumber+2][N];
                for (int i = start - 1; i < end + 1; i++) {
                    frontierMatrix[i-start+1] = imageMatrix[i].clone();
                }
            }
        }

        private void createFrontierMatrixSizeFive() {
            int linesNumber = end - start;
            //frontiera ocupa toata marginea
            if (start == 0 && end == M) {
                topFrontierLength = 0;
                frontierMatrix = new double[linesNumber][N];
                for (int i = start; i < end; i++) {
                    frontierMatrix[i-start] = imageMatrix[i].clone();
                }
            //frontiera este intr-o margine
            } else if (start == 0 || end == M) {
                //daca frontiera mai are un singur vecin pana la margine
                if (linesNumber == M - 1) {
                    if (start == 0) {
                        topFrontierLength = 0;
                        frontierMatrix = new double[linesNumber+1][N];
                        for (int i = start; i < end + 1; i++) {
                            frontierMatrix[i-start] = imageMatrix[i].clone();
                        }
                    }
                    if (end == M) {
                        topFrontierLength = 1;
                        frontierMatrix = new double[linesNumber+1][N];
                        for (int i = start - 1; i < end; i++) {
                            frontierMatrix[i-start+1] = imageMatrix[i].clone();
                        }
                    }
                //daca frontiera mai are cel putin 2 vecini pana la margine
                } else {
                    if (start == 0) {
                        topFrontierLength = 0;
                        frontierMatrix = new double[linesNumber+2][N];
                        for (int i = start; i < end + 2; i++) {
                            frontierMatrix[i-start] = imageMatrix[i].clone();
                        }
                    }
                    if (end == M) {
                        topFrontierLength = 2;
                        frontierMatrix = new double[linesNumber+2][N];
                        for (int i = start - 2; i < end; i++) {
                            frontierMatrix[i-start+2] = imageMatrix[i].clone();
                        }
                    }
                }
            } else {
                //daca in sus e maxim un vecin
                if (start == 1) {
                    topFrontierLength = 1;
                //daca sunt cel putin 2 vecini
                } else {
                    topFrontierLength = 2;
                }

                int bottomFrontierLength;
                //daca in jos e maxim un vecin
                if (end == M-1) {
                    bottomFrontierLength = 1;
                //daca sunt cel putin 2 vecini
                } else {
                    bottomFrontierLength = 2;
                }

                frontierMatrix = new double[linesNumber+topFrontierLength+bottomFrontierLength][N];
                for (int i = start - topFrontierLength; i < end + bottomFrontierLength; i++) {
                    frontierMatrix[i-start+topFrontierLength] = imageMatrix[i].clone();
                }
            }
        }

        private void solveWithFilterSizeThree() {
            for (int i = start; i < end; i++) {
                for (int j = 0; j < N; j++) {
                    imageMatrix[i][j] = 0.0;
                    for (int k = -1; k < 2; k++) {
                        for (int l = -1; l < 2; l++) {
                            double initialImagePixel;
                            //colturi
                            if (i - k == -1 && j - l == -1 || i - k == M && j - l == N || i - k == -1 && j - l == N || i - k == M && j - l == -1) {
                                initialImagePixel = frontierMatrix[i-start+topFrontierLength][j];
                            }
                            //marginile sus-jos
                            else if (i - k == -1 || i - k == M) initialImagePixel = frontierMatrix[i-start+topFrontierLength][j-l];
                            //marginile stanga-dreapta
                            else if (j - l == -1 || j - l == N) initialImagePixel = frontierMatrix[i-k-start+topFrontierLength][j];
                            //orice alt caz
                            else initialImagePixel = frontierMatrix[i-k-start+topFrontierLength][j-l];

                            imageMatrix[i][j] += filterMatrix[1-k][1-l] * initialImagePixel;
                        }
                    }
                }
            }
        }

        private void solveWithFilterSizeFive() {
            for (int i = start; i < end; i++) {
                for (int j = 0; j < N; j++) {
                    imageMatrix[i][j] = 0.0;
                    for (int k = -2; k < 3; k++) {
                        for (int l = -2; l < 3; l++) {
                            double initialImagePixel;
                            //colturi
                            if (i - k <= -1 && j - l <= -1 || i - k >= M && j - l >= N || i - k <= -1 && j - l >= N || i - k >= M && j - l <= -1) {
                                initialImagePixel = frontierMatrix[i-start+topFrontierLength][j];
                            }
                            else if (i - k <= -1 || i - k >= M) initialImagePixel = frontierMatrix[i-start+topFrontierLength][j-l];
                            else if (j - l <= -1 || j - l >= N) initialImagePixel = frontierMatrix[i-k-start+topFrontierLength][j];
                            else initialImagePixel = frontierMatrix[i-k-start+topFrontierLength][j-l];

                            imageMatrix[i][j] += filterMatrix[2-k][2-l] * initialImagePixel;
                        }
                    }
                }
            }
        }

        private void solve() {
            if ((m == n && n == 3)) {
                solveWithFilterSizeThree();
            } else {
                solveWithFilterSizeFive();
            }
        }
    }

    public static void solveWithMultipleThreads(double[][] imageMatrix, double[][] filterMatrix, int threadsNumber) {
        CyclicBarrier barrier = new CyclicBarrier(threadsNumber);
        MyThread[] threads = new MyThread[threadsNumber];

        int M = imageMatrix.length;
        int start = 0;
        int chunk_size = M / threadsNumber;
        int end = chunk_size;
        int r = M % threadsNumber;

        for (int i = 0; i < threadsNumber; i++) {
            if (r > 0) {
                end++;
                r--;
            }
            threads[i] = new MyThread(imageMatrix, filterMatrix, start, end, barrier);
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
}
