//
// Created by Mircea on 09.10.2022.
//

#include "Utils.h"
#include "fstream"
#include "iostream"

using std::ifstream, std::ofstream;

double **Utils::readMatrixFromFile(const string& path) {
    ifstream fin(path);
    int m, n;
    fin >> m >> n;
    double** matrix = new double*[m];
    for (int i = 0; i < m; i++) {
        matrix[i] = new double[n];
        for (int j = 0; j < n; j++) {
            fin >> matrix[i][j];
        }
    }
    return matrix;
}

void Utils::writeMatrixToFile(const int& m, const int& n, double **&matrix, const string &path) {
    ofstream fout(path);
    for (int i = 0; i < m; i++) {
        for (int j=0; j < n; j++) {
            fout << matrix[i][j] << ' ';
        }
        fout << '\n';
    }
}

void Utils::writeInputMatrixToFile(const int& m, const int& n, double **&matrix, const string &path) {
    ofstream fout(path);
    fout << m << ' ' << n << '\n';
    for (int i = 0; i < m; i++) {
        for (int j=0; j < n; j++) {
            fout << matrix[i][j] << ' ';
        }
        fout << '\n';
    }
}

double **Utils::generateRandomMatrix(const int &m, const int &n) {
    double** matrix = new double*[m];
    for (int i = 0; i < m; i++) {
        matrix[i] = new double[n];
    }
    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            matrix[i][j] = generatePixelValue();
        }
    }
    return matrix;
}

const double Utils::generatePixelValue() {
    double r = (double) rand() / RAND_MAX;
    return 255 * r;
}
