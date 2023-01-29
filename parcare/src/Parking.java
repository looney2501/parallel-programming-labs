import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Parking {
    public static class ParkingRegistry {
        private final List<String> transactions;
        private final Lock lock;

        public ParkingRegistry() {
            transactions = new ArrayList<>();
            lock = new ReentrantLock();
        }

        public void addTransaction(String transaction) {
            transactions.add(transaction);
        }

        List<String> getTransactions() {
            return transactions;
        }

        public Lock getLock() {
            return lock;
        }
    }

    private final int n;
    private int p;
    private final Lock lock;
    private final Condition notEmpty;
    private final Condition notFull;
    private final AtomicBoolean finished;
    private final CyclicBarrier finishedBarrier;

    public ParkingRegistry getParkingRegistry() {
        return parkingRegistry;
    }

    private final ParkingRegistry parkingRegistry;
    private final Lock parkingRegistryLock;

    public Parking(int n, int p, int producersNo) {
        this.n = n;
        this.p = p;
        this.lock = new ReentrantLock();
        this.notEmpty = lock.newCondition();
        this.notFull = lock.newCondition();
        parkingRegistry = new ParkingRegistry();
        parkingRegistryLock = parkingRegistry.getLock();
        finished = new AtomicBoolean(false);
        finishedBarrier = new CyclicBarrier(producersNo);
    }

    public void addCar() {
        lock.lock();

        while (p == n) {
            try {
                notFull.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        p++;

        parkingRegistryLock.lock();
        parkingRegistry.addTransaction("intrare");
        parkingRegistryLock.unlock();

        notEmpty.signal();

        lock.unlock();
    }

    public void removeCar() {
        lock.lock();

        while (p == 0) {
            try {
                if (!finished.get()) {
                    notEmpty.await();
                } else {
                    break;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if (p > 0) {
            p--;

            parkingRegistryLock.lock();
            parkingRegistry.addTransaction("iesire");
            parkingRegistryLock.unlock();
        }

        notFull.signal();

        lock.unlock();
    }

    public void setFinished(boolean value) {
        try {
            finishedBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }

        lock.lock();
        finished.compareAndSet(!value, value);
        lock.unlock();
    }
}
