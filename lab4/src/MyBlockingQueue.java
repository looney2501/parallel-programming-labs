import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyBlockingQueue {
    Queue<Node> queue;
    boolean finished;

    public MyBlockingQueue() {
        queue = new LinkedList<>();
        finished = false;
    }

    public synchronized void put(int exponent, double coefficient) {
        queue.add(new Node(exponent, coefficient));
        this.notifyAll();
    }

    public synchronized void setFinished(boolean finished) {
        this.finished = finished;
        this.notifyAll();
    }

    public synchronized Node take() throws InterruptedException {
        while (queue.isEmpty()) {
            if (!finished) {
                this.wait();
            } else {
                return null;
            }
        }
        return queue.poll();
    }
}
