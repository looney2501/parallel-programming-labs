import java.util.LinkedList;
import java.util.Queue;

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

    public synchronized void setFinished(boolean value) {
        finished = value;
        this.notifyAll();
    }

    public synchronized Node take() throws InterruptedException {
        while (queue.isEmpty() && !finished) {
            this.wait();
        }
        if (finished) return null;
        return queue.poll();
    }
}
