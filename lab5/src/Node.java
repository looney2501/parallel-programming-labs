import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Node {
    int exponent;
    double coefficient;
    Node next;
    Lock lock = new ReentrantLock();

    public Node() {
    }

    public Node(int exponent, double coefficient) {
        this.exponent = exponent;
        this.coefficient = coefficient;
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }
}
