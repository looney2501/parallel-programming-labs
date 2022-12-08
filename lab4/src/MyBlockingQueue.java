import java.util.LinkedList;
import java.util.Queue;

public class MyBlockingQueue {
    Queue<Node> queue;

    public MyBlockingQueue() {
        queue = new LinkedList<>();
    }

    public synchronized void put(int exponent, double coefficient) {
        queue.add(new Node(exponent, coefficient));
        this.notifyAll();
    }

    public synchronized void finish(int consumersNumber) {
        for (int i = 0; i < consumersNumber; i++) {
            queue.add(null);
        }
        this.notifyAll();
    }

    public synchronized Node take() throws InterruptedException {
        while (queue.isEmpty()) {
            this.wait();
        }
        return queue.poll();
    }
}
