import java.util.function.BinaryOperator;

public class Main {
    private static void operationSequential(double[] a, double[] b, double[] c, BinaryOperator<Double> op) {
        for (int i=0; i < a.length; i++) {
            c[i] = op.apply(a[i], b[i]);
        }
    }

    private static class MyThread extends Thread {
        private final double[] a, b, c;
        private final int start, end;
        private final BinaryOperator<Double> op;

        public MyThread(double[] a, double[] b, double[] c, int start, int end, BinaryOperator<Double> op) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.start = start;
            this.end = end;
            this.op = op;
        }

        @Override
        public void run() {
            for (int i = start; i < end; i++) {
                c[i] = op.apply(a[i], b[i]);
            }
        }
    }

    private static class MyThread2 extends Thread {
        private final double[] a, b, c;
        private final int start, pas;
        private final BinaryOperator<Double> op;

        public MyThread2(double[] a, double[] b, double[] c, int start, int pas, BinaryOperator<Double> op) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.start = start;
            this.pas = pas;
            this.op = op;
        }

        @Override
        public void run() {
            for (int i = start; i < a.length; i+=pas) {
                c[i] = op.apply(a[i], b[i]);
            }
        }
    }

    private static void addition_with_threads(double[] a, double[] b, double[] c, int no_threads, BinaryOperator<Double> op) {
        MyThread[] threads = new MyThread[no_threads];

        int start = 0;
        int chunk_size = a.length / no_threads;
        int end = chunk_size;
        int r = a.length % no_threads;

        for (int i = 0; i < no_threads; i++) {
            if (r > 0) {
                end++;
                r--;
            }
            threads[i] = new MyThread(a, b, c, start, end, op);
            threads[i].start();
            start = end;
            end += chunk_size;
        }

        for (int i = 0; i < no_threads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void addition_with_threads2(double[] a, double[] b, double[] c, int no_threads, BinaryOperator<Double> op) {
        MyThread2[] threads = new MyThread2[no_threads];

        for (int i = 0; i < no_threads; i++) {
            threads[i] = new MyThread2(a, b, c, i, no_threads, op);
            threads[i].start();
        }

        for (int i = 0; i < no_threads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static boolean equal(double[] a, double[]  b) {
        for (int i = 0; i < a.length; i++) {
            if (Math.abs(a[i] - b[i]) > 0.001) return false;
        }
        return true;
    }

    public static void main(String[] args) {
        int length = 1000000;
        int no_threads = 8;

        double[] a = new double[length];
        double[] b = new double[length];
        double[] c1 = new double[length];
        double[] c2 = new double[length];

        BinaryOperator<Double> op = (x, y) -> Math.sqrt(Math.pow(x, 3) + Math.pow(y, 3));

        for (int i = 0; i < length; i++) {
            a[i] = i+1;
            b[i] = i*i;
        }

        long start = System.nanoTime();
        operationSequential(a, b, c1, op);
        long finish = System.nanoTime();

        System.out.println("Time: " + (finish - start));

        start = System.nanoTime();
//        addition_with_threads(a, b, c2, no_threads, op);
        addition_with_threads2(a, b, c2, no_threads, op);
        finish = System.nanoTime();

        System.out.println("Time: " + (finish - start));

        if (equal(c1, c2)) {
            System.out.println("egale");
        }
        else {
            System.out.println("nu sunt egale");
        }
    }
}
