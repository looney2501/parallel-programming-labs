//
// Created by Mircea on 09.10.2022.
//

#ifndef PPD_UTILS_H
#define PPD_UTILS_H

#include <string>

using std::string;

class Utils {
public:
    static double** readMatrixFromFile(const string& path);
    static void writeMatrixToFile(const int& m, const int& n, double**& matrix, const string& path);
    static void writeInputMatrixToFile(const int& m, const int& n, double**& matrix, const string& path);
    static double** generateRandomMatrix(const int& m, const int& n);

private:
    static const double generatePixelValue();
};


#endif //PPD_UTILS_H
