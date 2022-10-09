//
// Created by Mircea on 09.10.2022.
//

#include "Solver.h"
#include "../utils/Utils.h"
#include <iostream>
#include <sstream>

using std::cout, std::stringstream;

Solver::Solver(const int &M, const int &N, const int &m, const int &n, const int &testNumber) : M(M), N(N), m(m), n(n), testNumber(testNumber) {
}

void Solver::setImageMatrix(double **imageMatrix) {
    Solver::imageMatrix = imageMatrix;
}

void Solver::setFilterMatrix(double **filterMatrix) {
    Solver::filterMatrix = filterMatrix;
}

void Solver::writeResultToFile(double **&matrix, const string& type) {
    cout << "Writing filtered matrix to file..." << '\n';
    stringstream path;
    path << "..\\lab1\\resources\\test" << testNumber << "\\filteredMatrix" << type << ".out";
    Utils::writeMatrixToFile(M, N, matrix, path.str());
    cout << "Done!" << '\n';
}

void Solver::generateInputData() {
    cout << "Generating matrix of pixels..." << '\n';
    this->imageMatrix = Utils::generateRandomMatrix(this->M, this->N);
    stringstream path1;
    path1 << "..\\lab1\\resources\\test" << testNumber << "\\imageMatrix.in";
    Utils::writeInputMatrixToFile(M, N, this->imageMatrix, path1.str());
    cout << "Done!" << '\n';

    cout << "Generating matrix of filters..." << '\n';
    this->filterMatrix = Utils::generateRandomMatrix(this->m, this->n);
    stringstream path2;
    path2 << "..\\lab1\\resources\\test" << testNumber << "\\filterMatrix.in";
    Utils::writeInputMatrixToFile(m, n, this->filterMatrix, path2.str());
    cout << "Done!" << '\n';
}

void Solver::sequentialSolve() {
    double **filteredMatrix = (n == m && n == 3) ? solveWithFilterThree() : solveWithFilterFive();

    writeResultToFile(filteredMatrix, "Sequential");
}

double **Solver::solveWithFilterThree() {
    double** filteredMatrix = new double*[M];
    for (int i = 0; i < M; i++) {
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
    return filteredMatrix;
}

double **Solver::solveWithFilterFive() {
    double** filteredMatrix = new double*[M];
    for (int i = 0; i < M; i++) {
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
    return filteredMatrix;
}
