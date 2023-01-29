import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    public static void main(String[] args) {
        Parking parking = new Parking(100, 25, 3);
        AtomicBoolean finished = new AtomicBoolean(false);

        Producer[] producers = new Producer[3];
        for (int i = 0; i < producers.length; i++) {
            producers[i] = new Producer(parking, 200);
            producers[i].start();
        }

        Consumer[] consumers = new Consumer[2];
        for (int i = 0; i < consumers.length; i++) {
            consumers[i] = new Consumer(parking, 400);
            consumers[i].start();
        }

        Printer printer = new Printer(finished, parking.getParkingRegistry(), 75);
        printer.start();


        for (Producer producer : producers) {
            try {
                producer.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        for (Consumer consumer : consumers) {
            try {
                consumer.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        finished.set(true);
        try {
            printer.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
