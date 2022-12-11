import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Solver {
    public static MyList sequentialSolve(int polynomialNumber) {
        MyList result = new MyList();

        for (int i = 0; i < polynomialNumber; i++) {
            String path = "D:\\Proiecte\\Java\\PPD\\lab4\\resources\\input\\polynomial" + i + ".in";
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

    public static MyList parallelSolve(int polynomialNumber, int threadsNumber) {
        MyList result = new MyList();
        MyBlockingQueue queue = new MyBlockingQueue();

        Producer producer = new Producer(queue, polynomialNumber);
        producer.start();

        Consumer[] consumers = new Consumer[threadsNumber - 1];
        for (int i = 0; i < threadsNumber - 1; i++) {
            consumers[i] = new Consumer(queue, result);
            consumers[i].start();
        }

        try {
            producer.join();
            for (int i = 0; i < threadsNumber - 1; i++) {
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

        public Producer(MyBlockingQueue queue, int polynomialNumber) {
            this.queue = queue;
            this.polynomialNumber = polynomialNumber;
        }

        @Override
        public void run() {
            for (int i = 0; i < polynomialNumber; i++) {
                String path = "D:\\Proiecte\\Java\\PPD\\lab4\\resources\\input\\polynomial" + i + ".in";
                try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
                    int monomialNumber = Integer.parseInt(reader.readLine().strip());
                    for (int j = 0; j < monomialNumber; j++) {
                        var line = reader.readLine().strip().split(" ");
                        int exponent = Integer.parseInt(line[0].strip());
                        double coefficient = Double.parseDouble(line[1].strip());
                        queue.put(exponent, coefficient);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            queue.setFinished(true);
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
