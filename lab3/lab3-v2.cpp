#include "../utils/utils-lab3.cpp"
#include <algorithm>
#include <iostream>
#include <chrono>
#include <mpi.h>
#include <fstream>

#define SIZE 100001

using namespace std;

unsigned char v1[SIZE], v2[SIZE], v3[SIZE], v11[SIZE], v22[SIZE], v33[SIZE];
int n1, n2, n, artificialLength, chunkSize, processRank, worldSize, carry, receivedCarry, maxCarry;
ifstream fin1, fin2;

void calculateLengths() {
    fin1 = ifstream(R"(D:\Proiecte\C++\PPD\lab3\resources\input\number1.txt)");
    fin1 >> n1;
    fin2 = ifstream(R"(D:\Proiecte\C++\PPD\lab3\resources\input\number2.txt)");
    fin2 >> n2;
    n = max(n1, n2) + 1;
    chunkSize = n / worldSize;
    if (n % worldSize != 0) chunkSize++;
    artificialLength = chunkSize * worldSize;
}

void readData() {
    unsigned char digit;

    for (int i = 0; i < artificialLength; i++) {
        if (fin1 >> digit) v1[i] = digit - '0';
        if (fin2 >> digit) v2[i] = digit - '0';
    }

    fin1.close();
    fin2.close();
}

void spreadData() {
    MPI_Scatter(v1, chunkSize, MPI_BYTE, v11, chunkSize, MPI_BYTE, 0, MPI_COMM_WORLD);
    MPI_Scatter(v2, chunkSize, MPI_BYTE, v22, chunkSize, MPI_BYTE, 0, MPI_COMM_WORLD);
}

void calculate() {
    carry = 0;
    for (int i = 0; i < chunkSize; i++) {
        unsigned char s = v11[i] + v22[i] + carry;
        v33[i] = s % 10;
        carry = s / 10;
    }

    if (processRank == 0) {
        receivedCarry = 0;
    } else {
        MPI_Status status;
        MPI_Recv(&receivedCarry, 1, MPI_BYTE, processRank - 1, 1111, MPI_COMM_WORLD, &status);
    }

    if (receivedCarry == 1) {
        for (int i = 0; i < chunkSize; i++) {
            unsigned char s = v33[i] + receivedCarry;
            v33[i] = s % 10;
            receivedCarry = s / 10;
        }
    }

    maxCarry = max(carry, receivedCarry);

    if (processRank < worldSize - 1) {
        MPI_Send(&maxCarry, 1, MPI_BYTE, processRank + 1, 1111, MPI_COMM_WORLD);
    }
}

void receiveData() {
    MPI_Gather(v33, chunkSize, MPI_BYTE, v3, chunkSize, MPI_BYTE, 0, MPI_COMM_WORLD);
    if (v3[n - 1] == 0) {
        n--;
    }
}

int main(int argc, char **argv) {
    MPI_Init(&argc, &argv);
    MPI_Comm_rank(MPI_COMM_WORLD, &processRank);
    MPI_Comm_size(MPI_COMM_WORLD, &worldSize);

    auto start = chrono::steady_clock::now();

    calculateLengths();
    if (processRank == 0) {
        readData();
    }
    spreadData();
    calculate();
    receiveData();

    auto finish = chrono::steady_clock::now();
    auto time = chrono::duration<double, nano>(finish - start).count();

    if (processRank == 0) {
        cout << time;
        writeVectorToFile(v3, n, R"(D:\Proiecte\C++\PPD\lab3\resources\output\number3-v2.txt)");
    }

    MPI_Finalize();
}