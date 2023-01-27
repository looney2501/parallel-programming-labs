//
// Created by Mircea on 26.10.2022.
//
#include <thread>
#include <iostream>
#include <fstream>
#include <sstream>
#include "../utils/utils-lab1.cpp"

#define M 10000
#define N 10
#define m 5
#define n 5

using namespace std;

double imageMatrix[M][N];
double filteredMatrix[M][N];
double filterMatrix[m][n];

void readImageMatrixFromFile(const string &path) {
    ifstream fin(path);
    int x;
    fin >> x >> x;
    for (int i = 0; i < M; i++) {
        for (int j = 0; j < N; j++) {
            fin >> imageMatrix[i][j];
        }
    }
}

void readFilterMatrixFromFile(const string &path) {
    ifstream fin(path);
    int x;
    fin >> x >> x;
    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            fin >> filterMatrix[i][j];
        }
    }
}

void writeFilteredMatrixToFile(const string &path) {
    ofstream fout(path);
    for (int i = 0; i < M; i++) {
        for (int j = 0; j < N; j++) {
            fout << filteredMatrix[i][j] << ' ';
        }
        fout << '\n';
    }
}

void solveWithFilterSizeThree(int start, int end) {
    for (int i = start; i < end; i++) {
        for (int j = 0; j < N; j++) {
            filteredMatrix[i][j] = 0.0;
            for (int k = -1; k < 2; k++) {
                for (int l = -1; l < 2; l++) {
                    double initialImagePixel;
                    if (i - k == -1 && j - l == -1 || i - k == M && j - l == N || i - k == -1 && j - l == N ||
                        i - k == M && j - l == -1) {
                        initialImagePixel = imageMatrix[i][j];
                    } else if (i - k == -1 || i - k == M)
                        initialImagePixel = imageMatrix[i][j - l];
                    else if (j - l == -1 || j - l == N)
                        initialImagePixel = imageMatrix[i - k][j];
                    else
                        initialImagePixel = imageMatrix[i - k][j - l];

                    filteredMatrix[i][j] += filterMatrix[1 - k][1 - l] *
                                            initialImagePixel;
                }
            }
        }
    }
}

void solveWithFilterSizeFive(int start, int end) {
    for (int i = start; i < end; i++) {
        for (int j = 0; j < N; j++) {
            filteredMatrix[i][j] = 0;
            for (int k = -2; k < 3; k++) {
                for (int l = -2; l < 3; l++) {
                    double initialImagePixel = 0;
                    if (i - k <= -1 && j - l <= -1 || i - k >= M && j - l >= N || i - k <= -1 && j - l >= N ||
                        i - k >= M && j - l <= -1) {
                        initialImagePixel = imageMatrix[i][j];
                    } else if (i - k <= -1 || i - k >= M)
                        initialImagePixel = imageMatrix[i][j - l];
                    else if (j - l <= -1 || j - l >= N)
                        initialImagePixel = imageMatrix[i - k][j];
                    else
                        initialImagePixel = imageMatrix[i - k][j - l];

                    filteredMatrix[i][j] += filterMatrix[2 - k][2 - l] *
                                            initialImagePixel;
                }
            }
        }
    }
}

void solve(int start, int end) {
    if ((m == n && n == 3)) {
        solveWithFilterSizeThree(start, end);
    } else {
        solveWithFilterSizeFive(start, end);
    }
}

void solveMultipleThreads(int threadsNumber) {
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
        threads[i] = thread(solve, start, end);
        start = end;
        end += chunk_size;
    }

    for (int i = 0; i < threadsNumber; i++) {
        threads[i].join();
    }
}

int main(int argc, char **argv) {
    int threadsNumber = stoi(argv[1]);

    readImageMatrixFromFile(R"(D:\Proiecte\C++\PPD\lab1\resources\input\imageMatrix.in)");
    readFilterMatrixFromFile(R"(D:\Proiecte\C++\PPD\lab1\resources\input\filterMatrix.in)");

    if (threadsNumber == 0) {
        auto start = chrono::steady_clock::now();
        solve(0, M);
        auto finish = chrono::steady_clock::now();
        stringstream path;
        path << R"(D:\Proiecte\C++\PPD\lab1\resources\output\filteredMatrix-M)" << M << "-N" << N << "-m" << m << "-n"
             << n << "-th" << threadsNumber << "-static";
        writeFilteredMatrixToFile(path.str());
        cout << chrono::duration<double, milli>((finish - start)).count() << endl;
    } else {
        auto start = chrono::steady_clock::now();
        solveMultipleThreads(threadsNumber);
        auto finish = chrono::steady_clock::now();
        stringstream path;
        path << R"(D:\Proiecte\C++\PPD\lab1\resources\output\filteredMatrix-M)" << M << "-N" << N << "-m" << m << "-n"
             << n << "-th" << threadsNumber << "-static";
        writeFilteredMatrixToFile(path.str());
        cout << chrono::duration<double, milli>((finish - start)).count() << endl;
    }

    return 0;
}
