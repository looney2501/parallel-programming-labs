//
// Created by Mircea on 09.10.2022.
//

#ifndef PPD_SOLVER_H
#define PPD_SOLVER_H
#include <string>

using std::string;

class Solver {
private:
    const int M, N, m, n, testNumber;
    double** imageMatrix, **filterMatrix;

    void writeResultToFile(double**& matrix, const string& type);
    double** solveWithFilterThree();
    double** solveWithFilterFive();

public:
    Solver(const int& M, const int& N, const int& m, const int& n, const int& testNumber);

    void setImageMatrix(double **imageMatrix);
    void setFilterMatrix(double **filterMatrix);

    void generateInputData();
    void sequentialSolve();
};


#endif //PPD_SOLVER_H
