//
// Created by Mircea on 26.10.2022.
//
#include <iostream>
#include "utils.h"

using namespace std;

void lab1Generator(int M, int N, int m, int n) {
    cout << "Generating matrix of pixels..." << '\n';
    double** imageMatrix = utils::generateRandomMatrix(M, N);
    utils::writeInputMatrixToFile(M, N, imageMatrix, R"(..\lab1\resources\input\imageMatrix.in)");
    utils::writeInputMatrixToFile(M, N, imageMatrix, R"(..\lab2\resources\input\imageMatrix.in)");
    cout << "Done!" << '\n';

    cout << "Generating matrix of filters..." << '\n';
    double** filterMatrix = utils::generateRandomMatrix(m, n);
    utils::writeInputMatrixToFile(m, n, filterMatrix, R"(..\lab1\resources\input\filterMatrix.in)");
    utils::writeInputMatrixToFile(m, n, filterMatrix, R"(..\lab2\resources\input\filterMatrix.in)");
    cout << "Done!" << '\n';
}

int main() {
    lab1Generator(10000, 10, 5, 5);
}
