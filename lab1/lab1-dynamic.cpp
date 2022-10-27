//
// Created by Mircea on 26.10.2022.
//
#include <thread>
#include <iostream>
#include <sstream>
#include "../utils/Utils.h"

using namespace std;

void solveWithFilterSizeThree(double** imageMatrix, double** filterMatrix, double** filteredMatrix, int start, int end, int M, int N) {
    for (int i = start; i < end; i++) {
        filteredMatrix[i] = new double[N];
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

void solveWithFilterSizeFive(double** imageMatrix, double** filterMatrix, double** filteredMatrix, int start, int end, int M, int N) {
    for (int i = start; i < end; i++) {
        filteredMatrix[i] = new double[N];
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

void solve(double** imageMatrix, double** filterMatrix, double** filteredMatrix, int start, int end, int M, int N, int m, int n) {
    if ((m == n && n == 3)) {
        solveWithFilterSizeThree(imageMatrix, filterMatrix, filteredMatrix, start, end, M, N);
    } else {
        solveWithFilterSizeFive(imageMatrix, filterMatrix, filteredMatrix, start, end, M, N);
    }
}

void solveMultipleThreads(int threadsNumber, double** imageMatrix, double** filterMatrix, double** filteredMatrix, int M, int N, int m, int n) {
    auto *threads = new thread[threadsNumber];

    int start = 0;
    int chunk_size = M / threadsNumber;
    int end = chunk_size;
    int r = M % threadsNumber;

    for (int i = 0; i < threadsNumber; i++) {
        if (r > 0) {
            end++;
            r--;
        }
        threads[i] = thread(solve, imageMatrix, filterMatrix, filteredMatrix, start, end, M, N, m, n);
        start = end;
        end += chunk_size;
    }

    for (int i = 0; i < threadsNumber; i++) {
        threads[i].join();
    }
}

int main(int argc, char** argv) {
    int threadsNumber = stoi(argv[1]);

    double** imageMatrix, ** filterMatrix, ** filteredMatrix;
    int M, N, m, n;
    Utils::readMatrixFromFile(imageMatrix, M, N, R"(D:\Proiecte\C++\PPD\lab1\resources\input\imageMatrix.in)");
    Utils::readMatrixFromFile(filterMatrix, m, n, R"(D:\Proiecte\C++\PPD\lab1\resources\input\filterMatrix.in)");
    filteredMatrix = new double* [M];
    for (int i = 0; i < M; i++) {
        filteredMatrix[i] = new double [N];
    }

    if (threadsNumber == 0) {
        auto start = chrono::steady_clock::now();
        solve(imageMatrix, filterMatrix, filteredMatrix, 0, M, M, N, m, n);
        auto finish = chrono::steady_clock::now();
        stringstream path;
        path << R"(D:\Proiecte\C++\PPD\lab1\resources\output\filteredMatrix-M)" << M << "-N" << N << "-m" << m << "-n"
             << n << "-th" << threadsNumber << "-dynamic";
        Utils::writeMatrixToFile(M, N, filteredMatrix, path.str());
        cout << chrono::duration<double, milli> ((finish-start)).count() << endl;
    }
    else {
        auto start = chrono::steady_clock::now();
        solveMultipleThreads(threadsNumber, imageMatrix, filterMatrix, filteredMatrix, M, N, m, n);
        auto finish = chrono::steady_clock::now();
        stringstream path;
        path << R"(D:\Proiecte\C++\PPD\lab1\resources\output\filteredMatrix-M)" << M << "-N" << N << "-m" << m << "-n"
             << n << "-th" << threadsNumber << "-dynamic";
        Utils::writeMatrixToFile(M, N, filteredMatrix, path.str());
        cout << chrono::duration<double, milli> ((finish-start)).count() << endl;
    }

    return 0;
}
