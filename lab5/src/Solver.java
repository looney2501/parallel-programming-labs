import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;

public class Solver {
    public static MyList sequentialSolve(int polynomialNumber) {
        MyList result = new MyList();

        for (int i = 0; i < polynomialNumber; i++) {
            String path = "D:\\proiecte\\Java\\PPD\\lab5\\resources\\input\\polynomial" + i + ".in";
            try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
                int monomialNumber = Integer.parseInt(reader.readLine().strip());
                for (int j = 0; j < monomialNumber; j++) {
                    var line = reader.readLine().strip().split(" ");
                    int exponent = Integer.parseInt(line[0].strip());
                    double coefficient = Double.parseDouble(line[1].strip());
                    result.add(exponent, coefficient);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return result;
    }

    public static MyList parallelSolve(int polynomialNumber, int threadsNumber, int producersNumber) {
        int consumersNumber = threadsNumber - producersNumber;
        MyList result = new MyList();
        MyBlockingQueue queue = new MyBlockingQueue(producersNumber);

        int chunkSize = polynomialNumber / producersNumber;
        int r = polynomialNumber % producersNumber;
        int start = 0;
        int end = chunkSize;

        Producer[] producers = new Producer[producersNumber];
        for (int i = 0; i < producersNumber; i++) {
            if (r > 0) {
                end++;
                r--;
            }
            producers[i] = new Producer(queue, polynomialNumber, start, end);
            producers[i].start();
            start = end;
            end += chunkSize;
        }

        Consumer[] consumers = new Consumer[consumersNumber];
        for (int i = 0; i < consumersNumber; i++) {
            consumers[i] = new Consumer(queue, result);
            consumers[i].start();
        }

        try {
            for (int i = 0; i < producersNumber; i++) {
                producers[i].join();
            }
            for (int i = 0; i < consumersNumber; i++) {
                consumers[i].join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    private static class Producer extends Thread {
        MyBlockingQueue queue;
        int polynomialNumber;
        int start;
        int end;

        public Producer(MyBlockingQueue queue, int polynomialNumber, int start, int end) {
            this.queue = queue;
            this.polynomialNumber = polynomialNumber;
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            int readLines = 0;
            for (int i = start; i < end; i++) {
                String path = "D:\\proiecte\\Java\\PPD\\lab5\\resources\\input\\polynomial" + i + ".in";
                try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
                    int monomialNumber = Integer.parseInt(reader.readLine().strip());
                    for (int j = 0; j < monomialNumber; j++) {
                        var line = reader.readLine().strip().split(" ");
                        readLines++;
                        int exponent = Integer.parseInt(line[0].strip());
                        double coefficient = Double.parseDouble(line[1].strip());
                        queue.put(exponent, coefficient);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                queue.setFinished(true);
            } catch (BrokenBarrierException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class Consumer extends Thread {
        MyBlockingQueue queue;
        MyList result;

        public Consumer(MyBlockingQueue queue, MyList result) {
            this.queue = queue;
            this.result = result;
        }

        @Override
        public void run() {
            Node node;
            do {
                try {
                    node = queue.take();
                    if (node != null) {
                        result.add(node.exponent, node.coefficient);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } while (node != null);
        }
    }
}
