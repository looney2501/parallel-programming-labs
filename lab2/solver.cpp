//
// Created by Mircea on 31.10.2022.
//

#include "barrier.cpp"
#include <thread>
#include <vector>

using namespace std;

class Solver {
public:
    static void solveWithMultipleThreads(double **imageMatrix, double **filterMatrix, int M, int N, int m, int n,
                                         int threadsNumber) {
        my_barrier barrier(threadsNumber);

        thread *threads = new thread[threadsNumber];

        int start = 0;
        int chunk_size = M / threadsNumber;
        int end = chunk_size;
        int r = M % threadsNumber;

        for (int i = 0; i < threadsNumber; i++) {
            if (r > 0) {
                end++;
                r--;
            }
            threads[i] = thread(threadRunner, imageMatrix, filterMatrix, M, N, m, n, start, end, ref(barrier));
            start = end;
            end += chunk_size;
        }

        for (int i = 0; i < threadsNumber; i++) {
            threads[i].join();
        }
    }

private:
    static void
    threadRunner(double **imageMatrix, double **filterMatrix, int M, int N, int m, int n, int start, int end,
                 my_barrier &barrier) {
        double **frontierMatrix;
        int topFrontierLength;
        createFrontierMatrix(imageMatrix, frontierMatrix, M, N, m, n, start, end, topFrontierLength);
        barrier.wait();
        solve(imageMatrix, filterMatrix, frontierMatrix, M, N, m, n, start, end, topFrontierLength);
    }

    static void
    createFrontierMatrix(double **imageMatrix, double **&frontierMatrix, int M, int N, int m, int n, int start, int end,
                         int &topFrontierLength) {
        if ((m == n && n == 3)) {
            createFrontierMatrixSizeThree(imageMatrix, frontierMatrix, M, N, start, end, topFrontierLength);
        } else {
            createFrontierMatrixSizeFive(imageMatrix, frontierMatrix, M, N, start, end, topFrontierLength);
        }
    }

    static void
    createFrontierMatrixSizeThree(double **imageMatrix, double **&frontierMatrix, int M, int N, int start, int end,
                                  int &topFrontierLength) {
        int linesNumber = end - start;
        //frontiera ocupa toata matricea
        if (start == 0 && end == M) {
            topFrontierLength = 0;
            frontierMatrix = new double *[linesNumber];
            for (int i = start; i < end; i++) {
                frontierMatrix[i - start] = new double[N];
                for (int j = 0; j < N; j++) {
                    frontierMatrix[i - start][j] = imageMatrix[i][j];
                }
            }
            //frontiera este intr-o margine, dar nu ocupa toata marginea
        } else if ((start == 0 || end == M) && linesNumber != M) {
            frontierMatrix = new double *[linesNumber + 1];
            if (start == 0) {
                topFrontierLength = 0;
                for (int i = start; i < end + 1; i++) {
                    frontierMatrix[i - start] = new double[N];
                    for (int j = 0; j < N; j++) {
                        frontierMatrix[i - start][j] = imageMatrix[i][j];
                    }
                }
            }
            if (end == M) {
                topFrontierLength = 1;
                for (int i = start - 1; i < end; i++) {
                    frontierMatrix[i - start + 1] = new double[N];
                    for (int j = 0; j < N; j++) {
                        frontierMatrix[i - start + 1][j] = imageMatrix[i][j];
                    }
                }
            }
            //frontiera nu este in margine
        } else {
            topFrontierLength = 1;
            frontierMatrix = new double *[linesNumber + 2];
            for (int i = start - 1; i < end + 1; i++) {
                frontierMatrix[i - start + 1] = new double[N];
                for (int j = 0; j < N; j++) {
                    frontierMatrix[i - start + 1][j] = imageMatrix[i][j];
                }
            }
        }
    }

    static void
    createFrontierMatrixSizeFive(double **imageMatrix, double **&frontierMatrix, int M, int N, int start, int end,
                                 int &topFrontierLength) {
        int linesNumber = end - start;
        //frontiera ocupa toata marginea
        if (start == 0 && end == M) {
            topFrontierLength = 0;
            frontierMatrix = new double *[linesNumber];
            for (int i = start; i < end; i++) {
                frontierMatrix[i - start] = new double[N];
                for (int j = 0; j < N; j++) {
                    frontierMatrix[i - start][j] = imageMatrix[i][j];
                }
            }
            //frontiera este intr-o margine
        } else if (start == 0 || end == M) {
            //daca frontiera mai are un singur vecin pana la margine
            if (linesNumber == M - 1) {
                if (start == 0) {
                    topFrontierLength = 0;
                    frontierMatrix = new double *[linesNumber + 1];
                    for (int i = start; i < end + 1; i++) {
                        frontierMatrix[i - start] = new double[N];
                        for (int j = 0; j < N; j++) {
                            frontierMatrix[i - start][j] = imageMatrix[i][j];
                        }
                    }
                }
                if (end == M) {
                    topFrontierLength = 1;
                    frontierMatrix = new double *[linesNumber + 1];
                    for (int i = start - 1; i < end; i++) {
                        frontierMatrix[i - start + 1] = new double[N];
                        for (int j = 0; j < N; j++) {
                            frontierMatrix[i - start + 1][j] = imageMatrix[i][j];
                        }
                    }
                }
                //daca frontiera mai are cel putin 2 vecini pana la margine
            } else {
                if (start == 0) {
                    topFrontierLength = 0;
                    frontierMatrix = new double *[linesNumber + 2];
                    for (int i = start; i < end + 2; i++) {
                        frontierMatrix[i - start] = new double[N];
                        for (int j = 0; j < N; j++) {
                            frontierMatrix[i - start][j] = imageMatrix[i][j];
                        }
                    }
                }
                if (end == M) {
                    topFrontierLength = 2;
                    frontierMatrix = new double *[linesNumber + 2];
                    for (int i = start - 2; i < end; i++) {
                        frontierMatrix[i - start + 2] = new double[N];
                        for (int j = 0; j < N; j++) {
                            frontierMatrix[i - start + 2][j] = imageMatrix[i][j];
                        }
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
            if (end == M - 1) {
                bottomFrontierLength = 1;
                //daca sunt cel putin 2 vecini
            } else {
                bottomFrontierLength = 2;
            }

            frontierMatrix = new double *[linesNumber + topFrontierLength + bottomFrontierLength];
            for (int i = start - topFrontierLength; i < end + bottomFrontierLength; i++) {
                frontierMatrix[i - start + topFrontierLength] = new double[N];
                for (int j = 0; j < N; j++) {
                    frontierMatrix[i - start + topFrontierLength][j] = imageMatrix[i][j];
                }
            }
        }
    }

    static void
    solve(double **imageMatrix, double **filterMatrix, double **frontierMatrix, int M, int N, int m, int n, int start,
          int end, int topFrontierLength) {
        if ((m == n && n == 3)) {
            solveWithFilterSizeThree(imageMatrix, filterMatrix, frontierMatrix, M, N, start, end, topFrontierLength);
        } else {
            solveWithFilterSizeFive(imageMatrix, filterMatrix, frontierMatrix, M, N, start, end, topFrontierLength);
        }
    }

    static void
    solveWithFilterSizeThree(double **imageMatrix, double **filterMatrix, double **frontierMatrix, int M, int N,
                             int start, int end, int topFrontierLength) {
        for (int i = start; i < end; i++) {
            for (int j = 0; j < N; j++) {
                imageMatrix[i][j] = 0.0;
                for (int k = -1; k < 2; k++) {
                    for (int l = -1; l < 2; l++) {
                        double initialImagePixel;
                        //colturi
                        if (i - k == -1 && j - l == -1 || i - k == M && j - l == N || i - k == -1 && j - l == N ||
                            i - k == M && j - l == -1) {
                            initialImagePixel = frontierMatrix[i - start + topFrontierLength][j];
                        }
                            //marginile sus-jos
                        else if (i - k == -1 || i - k == M)
                            initialImagePixel = frontierMatrix[i - start + topFrontierLength][j - l];
                            //marginile stanga-dreapta
                        else if (j - l == -1 || j - l == N)
                            initialImagePixel = frontierMatrix[i - k - start + topFrontierLength][j];
                            //orice alt caz
                        else initialImagePixel = frontierMatrix[i - k - start + topFrontierLength][j - l];

                        imageMatrix[i][j] += filterMatrix[1 - k][1 - l] * initialImagePixel;
                    }
                }
            }
        }
    }

    static void
    solveWithFilterSizeFive(double **imageMatrix, double **filterMatrix, double **frontierMatrix, int M, int N,
                            int start, int end, int topFrontierLength) {
        for (int i = start; i < end; i++) {
            for (int j = 0; j < N; j++) {
                imageMatrix[i][j] = 0.0;
                for (int k = -2; k < 3; k++) {
                    for (int l = -2; l < 3; l++) {
                        double initialImagePixel;
                        //colturi
                        if (i - k <= -1 && j - l <= -1 || i - k >= M && j - l >= N || i - k <= -1 && j - l >= N ||
                            i - k >= M && j - l <= -1) {
                            initialImagePixel = frontierMatrix[i - start + topFrontierLength][j];
                        } else if (i - k <= -1 || i - k >= M)
                            initialImagePixel = frontierMatrix[i - start + topFrontierLength][j - l];
                        else if (j - l <= -1 || j - l >= N)
                            initialImagePixel = frontierMatrix[i - k - start + topFrontierLength][j];
                        else initialImagePixel = frontierMatrix[i - k - start + topFrontierLength][j - l];

                        imageMatrix[i][j] += filterMatrix[2 - k][2 - l] * initialImagePixel;
                    }
                }
            }
        }
    }
};
