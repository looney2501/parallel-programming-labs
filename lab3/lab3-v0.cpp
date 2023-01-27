#include "../utils/utils-lab3.cpp"
#include <algorithm>
#include <iostream>
#include <chrono>
#define SIZE 100001

using namespace std;

unsigned char v1[SIZE], v2[SIZE], v3[SIZE];
int n1, n2, minL, carry, resultL;

void setUp() {
    unsigned char digit;
    ifstream fin(R"(D:\Proiecte\C++\PPD\lab3\resources\input\number1.txt)");
    fin >> n1;
    for (int i = 0; i < n1; i++) {
        fin >> digit;
        v1[i] = digit - '0';
    }
    fin.close();

    fin = ifstream(R"(D:\Proiecte\C++\PPD\lab3\resources\input\number2.txt)");
    fin >> n2;
    for (int i = 0; i < n2; i++) {
        fin >> digit;
        v2[i] = digit - '0';
    }
    fin.close();

    minL = min(n1, n2);
    resultL = n1 + n2 - minL + 1;
}

void tearDown() {
    writeVectorToFile(v3, resultL, R"(D:\Proiecte\C++\PPD\lab3\resources\output\number3-v0.txt)");
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
    auto start = chrono::steady_clock::now();

    setUp();
    calculate();

    auto finish = chrono::steady_clock::now();
    auto time = chrono::duration <double, nano>(finish - start).count();
    cout << time;

    tearDown();
}