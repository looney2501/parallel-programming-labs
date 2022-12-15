#include <iostream>
#include <chrono>
#include <omp.h>

using namespace std;

const int MAX = 10;

int a[MAX];
int b[MAX];
int c[MAX];
int c2[MAX];
double aa[MAX][MAX];
double bb[MAX][MAX];
double cc[MAX][MAX];

int produsScalar() {
    int s = 0;
    #pragma omp parallel for reduction(+:s)
    for (int i = 0; i < MAX; i++) {
        s += a[i] * b[i];
    }
    return s;
}

int produs_scalar() {
    int s = 0;
    for (int i = 0; i < MAX; i++) {
        s += a[i] * b[i];
    }
    return s;
}

void suma() {
    for (int i = 0; i < MAX; i++) {
        c[i] = a[i] + b[i];
    }
}

void produs() {
    for (int i = 0; i < MAX; i++) {
        c2[i] = a[i] * b[i];
    }
}

int main() {
    omp_set_num_threads(2);
    auto start = std::chrono::system_clock::now();

    #pragma omp parallel
    {
//        #pragma omp critical
//        {
//            cout << "Hello" << omp_get_thread_num() << endl;
//        }

        #pragma omp for schedule(static, 2)
        for (int i = 0; i < MAX; i++) {
//            a[i] = omp_get_thread_num();
            a[i] = i;
            b[i] = i;
        }

        #pragma omp for schedule(static, 2) collapse(2)
        for (int i = 0; i < MAX; i++) {
            for (int j = 0; j < MAX; j++) {
//                aa[i][j] = i + j;
                aa[i][j] = omp_get_thread_num();
                bb[i][j] = i * j;
            }
        }

        #pragma omp sections
        {
            #pragma omp section
            {
                suma();
                #pragma omp critical
                {
                    for (int i = 0; i < MAX; i++) {
                        cout << c[i] << ' ';
                    }
                    cout << "thread number: " << omp_get_thread_num() << endl;
                }
            }

            #pragma omp section
            {
                produs();
                #pragma omp critical
                {
                    for (int i = 0; i < MAX; i++) {
                        cout << c2[i] << ' ';
                    }
                    cout << "thread number: " << omp_get_thread_num() << endl;
                }
            }

            #pragma omp section
            {
                int produsScal = produsScalar();
                #pragma omp critical
                {
                    cout << "thread number: " << omp_get_thread_num() << ' ' << "produs scalar: " << produsScal << endl;
                }
            }
        }
    }

    auto end = std::chrono::system_clock::now();
    auto diff = end - start;

//    cout << "produs scalar paralel: " << produsScalar() << endl;
//    cout << "produs scalar secv: " << produs_scalar() << endl;
//
//    cout << "aa=";
//    for (int i = 0; i < MAX; i++) {
//        for (int j = 0; j < MAX; j++) {
//            cout << aa[i][j] << ' ';
//        }
//        cout << endl;
//    }
//
    cout << "computation time 3 functii=" << chrono::duration <double, nano>(diff).count() << " ns" << endl;
////    for (int i = 0; i < MAX; i++) {
////        cout << a[i] << ' ';
////    }

    return 0;
}
