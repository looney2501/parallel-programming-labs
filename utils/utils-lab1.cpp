//
// Created by Mircea on 09.10.2022.
//

#include <string>
#include <fstream>
#include <iostream>
#include <filesystem>

using namespace std;

class Utils {
public:
    static void readMatrixFromFile(double **&matrix, int &m, int &n, const string &path) {
        ifstream fin(path);
        fin >> m >> n;
        matrix = new double *[m];
        for (int i = 0; i < m; i++) {
            matrix[i] = new double[n];
            for (int j = 0; j < n; j++) {
                fin >> matrix[i][j];
            }
        }
    }

    static void writeMatrixToFile(const int &m, const int &n, double **&matrix, const string &path) {
        ofstream fout(path);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                fout << matrix[i][j] << ' ';
            }
            fout << '\n';
        }
    }

    static void writeInputMatrixToFile(const int &m, const int &n, double **&matrix, const string &path) {
        ofstream fout(path);
        fout << m << ' ' << n << '\n';
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                fout << matrix[i][j] << ' ';
            }
            fout << '\n';
        }
    }

    static double **generateRandomMatrix(const int &m, const int &n) {
        double **matrix = new double *[m];
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

private:
    static double generatePixelValue() {
        double r = (double) rand() / RAND_MAX;
        return 255 * r;
    }
};
