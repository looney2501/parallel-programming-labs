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
    int *aa, *bb, *cc;
    int length = SIZE / numProcs;

    aa = new int[SIZE/numProcs];
    bb = new int[SIZE/numProcs];
    cc = new int[SIZE/numProcs];

    if (rank == 0) {
        for (int i = 0; i < SIZE; i++) {
            a[i] = i;
            b[i] = i + 1;
        }
    }

    MPI_Scatter(a, length, MPI_INT, aa, length, MPI_INT, 0, MPI_COMM_WORLD);
    MPI_Scatter(b, length, MPI_INT, bb, length, MPI_INT, 0, MPI_COMM_WORLD);

    for (int i = 0; i < length; i++) {
        cc[i] = aa[i] + bb[i];
    }

    MPI_Gather(cc, length, MPI_INT, c, length, MPI_INT, 0, MPI_COMM_WORLD);

    cout << "rank = " << rank << "; start = " << aa[0] << "; end = " << aa[length-1] << '\n';

    if (rank == 0) {
        for (int i = 0; i < SIZE; i++) {
            cout << c[i] << ' ';
        }
    }

    delete aa;
    delete bb;
    delete cc;

    MPI_Finalize();
}