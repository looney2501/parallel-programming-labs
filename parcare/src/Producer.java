public class Producer extends Thread {
    private final Parking parking;
    private final int iterations;

    public Producer(Parking parking, int iterations) {
        this.parking = parking;
        this.iterations = iterations;
    }

    @Override
    public void run() {
        for (int i = 0; i < iterations; i++) {
            parking.addCar();
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        parking.setFinished(true);
    }
}
