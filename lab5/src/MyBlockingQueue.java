import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyBlockingQueue {
    Queue<Node> queue;
    boolean finished;
    CyclicBarrier finishedBarrier;

    public MyBlockingQueue(int producersNo) {
        queue = new LinkedList<>();
        finished = false;
        finishedBarrier = new CyclicBarrier(producersNo);
    }

    public synchronized void put(int exponent, double coefficient) {
        queue.add(new Node(exponent, coefficient));
        this.notifyAll();
    }

    public void setFinished(boolean finished) throws BrokenBarrierException, InterruptedException {
        finishedBarrier.await();

        synchronized (this) {
            if (this.finished != finished) {
                this.finished = finished;
                notifyAll();
            }
        }
    }

    public synchronized Node take() throws InterruptedException {
        while (queue.isEmpty()) {
            if (!this.finished) {
                this.wait();
            } else {
                return null;
            }
        }
        return queue.poll();
    }
}
