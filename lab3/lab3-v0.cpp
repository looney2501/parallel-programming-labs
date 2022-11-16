#include "../utils/utils-lab3.cpp"
#include <algorithm>
#include <iostream>
#include <chrono>
#define SIZE 100001

using namespace std;

unsigned char *v1, *v2, *v3;
int n1, n2, minL, carry, resultL;

void setUp() {
    unsigned char digit;
    v1 = new unsigned char [SIZE];
    ifstream fin(R"(..\lab3\resources\input\number1.txt)");
    fin >> n1;
    for (int i = 0; i < n1; i++) {
        fin >> digit;
        v1[i] = digit - '0';
    }
    fin.close();

    v2 = new unsigned char [SIZE];
    fin = ifstream(R"(..\lab3\resources\input\number2.txt)");
    fin >> n2;
    for (int i = 0; i < n2; i++) {
        fin >> digit;
        v2[i] = digit - '0';
    }
    fin.close();

    v3 = new unsigned char [SIZE];

    minL = min(n1, n2);
    resultL = n1 + n2 - minL + 1;
}

void tearDown() {
    writeVectorToFile(v3, resultL, R"(..\lab3\resources\output\number3-v0.txt)");
    delete[] v1;
    delete[] v2;
    delete[] v3;
}

void calculate() {
    carry = 0;
    for (int i = 0; i < minL; i++) {
        int s = carry + v1[i] + v2[i];
        v3[i] = s % 10;
        carry = s / 10;
    }
    if (n1 > n2) {
        for (int i = minL; i < n1; i++) {
            int s = carry + v1[i];
            v3[i] = s % 10;
            carry = s / 10;
        }
    } else {
        for (int i = minL; i < n2; i++) {
            int s = carry + v2[i];
            v3[i] = s % 10;
            carry = s / 10;
        }
    }
    v3[resultL-1] = carry;
    if (v3[resultL - 1] == 0) {
        resultL--;
    }
}

int main(int argc, char** argv) {
    setUp();

    auto start = chrono::steady_clock::now();

    calculate();

    auto finish = chrono::steady_clock::now();
    auto time = chrono::duration <double, nano>(finish - start).count();
    cout << time;

    tearDown();
}