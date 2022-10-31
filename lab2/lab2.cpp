//
// Created by Mircea on 31.10.2022.
//
#include <thread>
#include <iostream>
#include "../utils/utils.cpp"
#include "solver.cpp"

using namespace std;

int main(int argc, char** argv) {
    int threadsNumber = stoi(argv[1]);

    double** imageMatrix, ** filterMatrix;
    int M, N, m, n;
    Utils::readMatrixFromFile(imageMatrix, M, N, R"(D:\Proiecte\C++\PPD\lab2\resources\input\imageMatrix.in)");
    Utils::readMatrixFromFile(filterMatrix, m, n, R"(D:\Proiecte\C++\PPD\lab2\resources\input\filterMatrix.in)");

    auto start = chrono::steady_clock::now();
    Solver::solveWithMultipleThreads(imageMatrix, filterMatrix, M, N, m, n, threadsNumber);
    auto finish = chrono::steady_clock::now();
    stringstream path;
    path << R"(D:\Proiecte\C++\PPD\lab2\resources\output\filteredMatrix-M)" << M << "-N" << N << "-m" << m << "-n"
         << n << "-th" << threadsNumber << "-dynamic";
    Utils::writeMatrixToFile(M, N, imageMatrix, path.str());
    cout << chrono::duration<double, nano> ((finish-start)).count() << endl;

    return 0;
}