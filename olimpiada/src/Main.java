public class Main {
    public static void main(String[] args) {
        Utils.generateResults();
        int producersNo = 4;
        int consumersNo = 5;
        int poolSize = 50;
        int filesNo = 10;

        ResultsPool resultsPool = new ResultsPool(producersNo, poolSize);

        int chunkSize = filesNo / producersNo;
        int r = filesNo % producersNo;
        int start = 0;
        int end = chunkSize;

        Producer[] producers = new Producer[producersNo];
        for (int i = 0; i < producersNo; i++) {
            if (r > 0) {
                end++;
                r--;
            }
            producers[i] = new Producer(resultsPool, start, end);
            producers[i].start();
            start = end;
            end += chunkSize;
        }

        Consumer[] consumers = new Consumer[consumersNo];
        for (int i = 0; i < consumersNo; i++) {
            consumers[i] = new Consumer(resultsPool);
            consumers[i].start();
        }

        try {
            for (int i = 0; i < producersNo; i++) {
                producers[i].join();
            }
            for (int i = 0; i < consumersNo; i++) {
                consumers[i].join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
