import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Producer extends Thread {
    ResultsPool queue;
    int start;
    int end;

    public Producer(ResultsPool queue, int start, int end) {
        this.queue = queue;
        this.start = start;
        this.end = end;
    }

    @Override
    public void run() {
        for (int i = start; i < end; i++) {
            String path = "D:\\proiecte\\Java\\PPD\\olimpiada\\resources\\judet" + i + ".in";
            try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
                for (int j = 0; j < 50; j++) {
                    PartialResult[] readResults = new PartialResult[10];
                    for (int k = 0; k < 10; k++) {
                        var readLine = reader.readLine();
                        System.out.println(readLine);
                        var line = readLine.strip().split(",");
                        int idConcurent = Integer.parseInt(line[0]);
                        int idProblema = Integer.parseInt(line[1]);
                        int punctaj = Integer.parseInt(line[2]);
                        int timpIncarcare = Integer.parseInt(line[3]);
                        readResults[k] = new PartialResult(idConcurent, idProblema, punctaj, timpIncarcare);
                    }

                    queue.addResult(readResults);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        queue.setFinished(true);
    }
}
