//
// Created by Mircea on 20.10.2022.
//

#include <iostream>
#include <thread>
#include <functional>
#include <cmath>

using namespace std;

int arraySize = 100000;
double a[100000];
double b[100000];
double c[100000];
double d[100000];

void suma2(const double a[], const double b[], double c[], int start, int stop, const function<double(double, double)>& f) {
    for (int i = start; i < stop; i++) {
        c[i] = f(a[i], b[i]);
    }
}

bool areEqual2(const double a[], const double b[], int size) {
    for (int i = 0; i < size; i++) {
        if (a[i] != b[i]) {
            return false;
        }
    }
    return true;
}

void par_lin2(const double a[], const double b[], double c[], int start, int stop, int no_threads, const function<double(double, double)>& f) {
    thread *threads = new thread[no_threads];

    int size = stop - start;
    int begin = 0;
    int chunk_size = size / no_threads;
    int end = chunk_size;
    int r = size % no_threads;

    for (int i = 0; i < no_threads; i++) {
        if (r > 0) {
            end++;
            r--;
        }
        threads[i] = thread(suma2, a, b, c, begin, end, f);
        begin = end;
        end += chunk_size;
    }

    for (int i = 0; i < no_threads; i++) {
        threads[i].join();
    }
}

int main() {
    int no_threads = 4;

    for (int i = 0; i < arraySize; i++) {
        a[i] = i+1;
        b[i] = i*1;
    }

    auto start = chrono::steady_clock::now();
    suma2(a, b, c, 0, arraySize, [=](const double &a, const double &b) { return sqrt(pow(a, 3) + pow(b, 3)); });
    auto finish = chrono::steady_clock::now();

    cout << "Sequential time: " << chrono::duration<double, milli> ((finish-start)).count() << '\n';

    start = chrono::steady_clock::now();
    par_lin2(a, b, d, 0, arraySize, no_threads,
             [=](const double &a, const double &b) { return sqrt(pow(a, 3) + pow(b, 3)); });
    finish = chrono::steady_clock::now();

    cout << "Parallel time: " << chrono::duration<double, milli> ((finish-start)).count() << '\n';

    cout << (areEqual2(c, d, arraySize) ? "Equal" : "Not equal");

}
