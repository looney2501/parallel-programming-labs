#include "../utils/utils-lab3.cpp"
#include <algorithm>
#include <iostream>
#include <chrono>
#include <mpi.h>
#include <fstream>
#define SIZE 100001

using namespace std;

unsigned char v1[SIZE], v2[SIZE], v3[SIZE];
int n1, n2, n, chunkSize, worldSize, processRank;

void spreadData() {
    int otherProcsNumber = worldSize - 1;
    ifstream fin1(R"(..\lab3\resources\input\number1.txt)");
    fin1 >> n1;
    ifstream fin2(R"(..\lab3\resources\input\number2.txt)");
    fin2 >> n2;

    n = max(n1, n2) + 1;
    chunkSize = n / otherProcsNumber;
    if (n % otherProcsNumber != 0) chunkSize++;
    unsigned char digit;

    for (int i = 0; i < otherProcsNumber; i++) {
        int j = 0;
        while (j < chunkSize) {
            if (fin1 >> digit) v1[i * chunkSize + j] = digit - '0';
            else break;
            j++;
        }

        j = 0;
        while (j < chunkSize) {
            if (fin2 >> digit) v2[i * chunkSize + j] = digit - '0';
            else break;
            j++;
        }

        MPI_Send(&chunkSize, 1, MPI_BYTE, i + 1, 1111, MPI_COMM_WORLD);
        MPI_Send(v1 + chunkSize * i, chunkSize, MPI_BYTE, i + 1, 1111, MPI_COMM_WORLD);
        MPI_Send(v2 + chunkSize * i, chunkSize, MPI_BYTE, i + 1, 1111, MPI_COMM_WORLD);
    }

    fin1.close();
    fin2.close();

    unsigned char carry = 0;
    MPI_Send(&carry, 1, MPI_BYTE, 1, 1111, MPI_COMM_WORLD);
}

void receiveData() {
    int otherProcsNumber = worldSize - 1;
    for (int i = 0; i < otherProcsNumber; i++) {
        MPI_Status status;
        MPI_Recv(v3 + chunkSize * i, chunkSize, MPI_BYTE, i + 1, 1111, MPI_COMM_WORLD, &status);
    }
    if (v3[n - 1] == 0) {
        n--;
    }
}

void calculateAndReturnResult() {
    int start, end;
    unsigned char carry, receivedCarry, maxCarry;

    MPI_Status statusA;
    MPI_Status statusB;
    MPI_Status statusC;
    MPI_Status statusD;

    MPI_Recv(&chunkSize, 1, MPI_BYTE, 0, 1111, MPI_COMM_WORLD, &statusA);
    MPI_Recv(v1 + chunkSize * (processRank - 1), chunkSize, MPI_BYTE, 0, 1111, MPI_COMM_WORLD, &statusB);
    MPI_Recv(v2 + chunkSize * (processRank - 1), chunkSize, MPI_BYTE, 0, 1111, MPI_COMM_WORLD, &statusC);

    start = chunkSize * (processRank - 1);
    end = chunkSize * processRank;

    carry = 0;
    for (int i = start; i < end; i++) {
        unsigned char s = v1[i] + v2[i] + carry;
        v3[i] = s % 10;
        carry = s / 10;
    }

    MPI_Recv(&receivedCarry, 1, MPI_BYTE, processRank - 1, 1111, MPI_COMM_WORLD, &statusD);

    if (receivedCarry == 1) {
        for (int i = start; i < end; i++) {
            unsigned char s = v3[i] + receivedCarry;
            v3[i] = s % 10;
            receivedCarry = s / 10;
        }
    }

    maxCarry = max(carry, receivedCarry);

    MPI_Send(v3 + chunkSize * (processRank - 1), chunkSize, MPI_BYTE, 0, 1111, MPI_COMM_WORLD);

    if (processRank < worldSize - 1) {
        MPI_Send(&maxCarry, 1, MPI_BYTE, processRank + 1, 1111, MPI_COMM_WORLD);
    }
}

int main(int argc, char** argv) {
    MPI_Init(&argc, &argv);

    MPI_Comm_rank(MPI_COMM_WORLD, &processRank);

    MPI_Comm_size(MPI_COMM_WORLD, &worldSize);

    if (processRank == 0) {
        auto start = chrono::steady_clock::now();

        spreadData();
        receiveData();

        auto finish = chrono::steady_clock::now();
        auto time = chrono::duration <double, nano>(finish - start).count();
        cout << time;

        writeVectorToFile(v3, n, R"(..\lab3\resources\output\number3-v1.txt)");
    } else {
        calculateAndReturnResult();
    }

    MPI_Finalize();
}