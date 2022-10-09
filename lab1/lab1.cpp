#include "Solver.h"
#include "../utils/Utils.h"

int main() {
    Solver seqSolver1(6, 6, 3, 3, 1);
//    seqSolver1.generateInputData();
    seqSolver1.setImageMatrix(Utils::readMatrixFromFile("..\\lab1\\resources\\test1\\imageMatrix.in"));
    seqSolver1.setFilterMatrix(Utils::readMatrixFromFile("..\\lab1\\resources\\test1\\filterMatrix.in"));
    seqSolver1.sequentialSolve();
    return 0;
}
