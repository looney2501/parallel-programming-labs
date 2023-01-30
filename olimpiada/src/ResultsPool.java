import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ResultsPool {
    Queue<PartialResult> queue;
    int capacity;
    AtomicBoolean isFinished;
    CyclicBarrier finishedBarrier;
    Lock lock;
    Condition notEmpty;
    Condition notFull;

    public ResultsPool(int producersNo, int capacity) {
        this.queue = new LinkedList<>();
        this.capacity = capacity;
        this.finishedBarrier = new CyclicBarrier(producersNo);
        this.isFinished = new AtomicBoolean(false);
        this.lock = new ReentrantLock();
        this.notFull = lock.newCondition();
        this.notEmpty = lock.newCondition();
    }

    public void addResult(PartialResult[] r) {
        lock.lock();

        while (capacity == 41) {
            try {
                notFull.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        for (PartialResult result : r) {
            queue.add(result);
        }
        capacity-=10;

        notEmpty.signal();

        lock.unlock();
    }

    public PartialResult pollResult() {
        lock.lock();

        while (capacity == 0) {
            if (!isFinished.get()) {
                try {
                    notEmpty.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                return null;
            }
        }

        capacity++;
        PartialResult result = queue.poll();

        notFull.signal();

        lock.unlock();
        return result;
    }

    public void setFinished(boolean value) {
        try {
            finishedBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }

        lock.lock();
        isFinished.compareAndSet(!value, value);
        lock.unlock();
    }
}
