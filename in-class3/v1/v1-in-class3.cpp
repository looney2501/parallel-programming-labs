//
// Created by Mircea on 10.11.2022.
//

#include <mpi.h>
#include <iostream>
#define SIZE 12

using namespace std;

int main(int argc, char** argv) {
    MPI_Init(&argc, &argv);

    int rank;
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    int numProcs;
    MPI_Comm_size(MPI_COMM_WORLD, &numProcs);

    int a[SIZE], b[SIZE], c[SIZE];
    int length = SIZE / numProcs;

    if (rank == 0) {
        for (int i = 0; i < SIZE; i++) {
            a[i] = i;
            b[i] = i + 1;
        }
        for (int i = 1; i < numProcs; i++) {
            MPI_Send(a + length * i, length, MPI_INT, i, 1111, MPI_COMM_WORLD);
            MPI_Send(b + length * i, length, MPI_INT, i, 1111, MPI_COMM_WORLD);
        }
        for (int i = 0; i < length; i++) {
            c[i] = a[i] + b[i];
        }
        MPI_Status status;
        for (int i = 1; i < numProcs; i++) {
            MPI_Recv(c + i * length, length, MPI_INT, i, 1234, MPI_COMM_WORLD, &status);
        }
        for (int i = 0; i < SIZE; i++) {
            cout << c[i] << ' ';
        }
    }
    else {
        MPI_Status statusA;
        MPI_Status statusB;
        MPI_Recv(a, length, MPI_INT, 0, 1111, MPI_COMM_WORLD, &statusA);
        MPI_Recv(b, length, MPI_INT, 0, 1111, MPI_COMM_WORLD, &statusB);
        cout << "rank = " << rank << "; start = " << a[0] << "; end = " << a[length-1] << '\n';
        for (int i = 0; i < length; i++) {
            c[i] = a[i] + b[i];
        }
        MPI_Send(c, length, MPI_INT, 0, 1234, MPI_COMM_WORLD);
    }

    MPI_Finalize();
}