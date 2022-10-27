//
// Created by Mircea on 26.10.2022.
//
#include <iostream>
#include "Utils.h"

using namespace std;

void lab1Generator(int M, int N, int m, int n) {
    cout << "Generating matrix of pixels..." << '\n';
    double** imageMatrix = Utils::generateRandomMatrix(M, N);
    Utils::writeInputMatrixToFile(M, N, imageMatrix, R"(..\lab1\resources\input\imageMatrix.in)");
    cout << "Done!" << '\n';

    cout << "Generating matrix of filters..." << '\n';
    double** filterMatrix = Utils::generateRandomMatrix(m, n);
    Utils::writeInputMatrixToFile(m, n, filterMatrix, R"(..\lab1\resources\input\filterMatrix.in)");
    cout << "Done!" << '\n';
}

int main() {
    lab1Generator(10000, 10, 5, 5);
}
