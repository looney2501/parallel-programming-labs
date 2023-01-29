public class Consumer extends Thread {
    private final Parking parking;
    private final int iterations;

    public Consumer(Parking parking, int iterations) {
        this.parking = parking;
        this.iterations = iterations;
    }

    @Override
    public void run() {
        for (int i = 0; i < iterations; i++) {
            parking.removeCar();
            try {
                Thread.sleep(15);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
