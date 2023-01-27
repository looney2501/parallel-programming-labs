//
// Created by Mircea on 20.10.2022.
//
#include <iostream>
#include <thread>
#include <functional>
#include <cmath>

using namespace std;

void suma1(const double a[], const double b[], double c[], int start, int stop, const function<double(double, double)>& f) {
    for (int i = start; i < stop; i++) {
        c[i] = f(a[i], b[i]);
    }
}

bool areEqual1(const double a[], const double b[], int size) {
    for (int i = 0; i < size; i++) {
        if (a[i] != b[i]) {
            return false;
        }
    }
    return true;
}

void par_lin1(const double a[], const double b[], double c[], int start, int stop, int no_threads, const function<double(double, double)>& f) {
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
        threads[i] = thread(suma1, a, b, c, begin, end, f);
        begin = end;
        end += chunk_size;
    }

    for (int i = 0; i < no_threads; i++) {
        threads[i].join();
    }
}

int main() {
    int size = 100000;
    int no_threads = 4;

    double *a = new double [size];
    double *b = new double [size];
    double *c = new double [size];
    double *d = new double [size];

    for (int i = 0; i < size; i++) {
        a[i] = i+1;
        b[i] = i*1;
    }

    auto start = chrono::steady_clock::now();
    suma1(a, b, c, 0, size, [=](const double &a, const double &b) { return sqrt(pow(a, 3) + pow(b, 3)); });
    auto finish = chrono::steady_clock::now();

    cout << "Sequential time: " << chrono::duration<double, milli> ((finish-start)).count() << '\n';

    start = chrono::steady_clock::now();
    par_lin1(a, b, d, 0, size, no_threads,
             [=](const double &a, const double &b) { return sqrt(pow(a, 3) + pow(b, 3)); });
    finish = chrono::steady_clock::now();

    cout << "Parallel time: " << chrono::duration<double, milli> ((finish-start)).count() << '\n';

    cout << (areEqual1(c, d, size) ? "Equal" : "Not equal");

}
