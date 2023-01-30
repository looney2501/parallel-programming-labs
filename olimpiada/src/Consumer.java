public class Consumer extends Thread {
    ResultsPool queue;

    public Consumer(ResultsPool queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        PartialResult r;
        do {
            r = queue.pollResult();
            if (r != null) {
                System.out.println(r);
            }
        } while (r != null);
    }
}
