import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

public class Printer extends Thread {
    private final AtomicBoolean finished;
    private final Parking.ParkingRegistry parkingRegistry;
    private final Lock parkingRegistryLock;
    private int oldLength;
    private int freeSpaces;

    public Printer(AtomicBoolean finished, Parking.ParkingRegistry parkingRegistry, int freeSpaces) {
        this.finished = finished;
        this.parkingRegistry = parkingRegistry;
        this.oldLength = 0;
        this.freeSpaces = freeSpaces;
        parkingRegistryLock = parkingRegistry.getLock();
    }

    @Override
    public void run() {
        while (!finished.get()) {
            printNewFreeSpaces();
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        printNewFreeSpaces();
    }

    private void printNewFreeSpaces() {
        parkingRegistryLock.lock();
        if (parkingRegistry.getTransactions().size() > oldLength) {
            for (int i = oldLength; i < parkingRegistry.getTransactions().size(); i++) {
                if (parkingRegistry.getTransactions().get(i).equals("intrare")) {
                    freeSpaces--;
                }
                if (parkingRegistry.getTransactions().get(i).equals("iesire")) {
                    freeSpaces++;
                }
            }
            oldLength = parkingRegistry.getTransactions().size();
            System.out.println(freeSpaces);
        }
        parkingRegistryLock.unlock();
    }
}
