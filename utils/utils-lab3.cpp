#include <cstdlib>
#include <string>
#include <fstream>

using std::string, std::ifstream, std::ofstream;

int generateRandomDigit() {
    return rand() % 10;
}

int *generateVectorOfIntegers(int size) {
    int *v = new int[size];
    for (int i = 0; i < size; i++) {
        v[i] = generateRandomDigit();
    }
    return v;
}

void writeInputVectorToFile(int *v, int n, string fileName) {
    ofstream fout(fileName);
    fout << n << '\n';
    for (int i = n - 1; i >= 0; i--) {
        fout << v[i] << ' ';
    }
    fout.close();
}

void generateInputDataLab3(int n1, int n2) {
    int *v1 = generateVectorOfIntegers(n1);
    writeInputVectorToFile(v1, n1, R"(..\lab3\resources\input\number1.txt)");
    int *v2 = generateVectorOfIntegers(n2);
    writeInputVectorToFile(v2, n2, R"(..\lab3\resources\input\number2.txt)");
    delete[] v1;
    delete[] v2;
}

void writeVectorToFile(unsigned char *v, int n, string fileName) {
    ofstream fout(fileName);
    unsigned char digit;
    for (int i = 0; i < n; i++) {
        digit = v[i] + '0';
        fout << digit << ' ';
    }
}
