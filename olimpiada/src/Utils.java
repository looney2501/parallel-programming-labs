import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Utils {
    public static void generateResults() {
        Integer[] startTimes = { 0, 30, 75, 135, 155 };
        Integer[] deadlines = { 30, 75, 135, 155, 225 };
        for (int i = 0; i < 10; i++) {
            try (FileWriter writer = new FileWriter("D:\\proiecte\\Java\\PPD\\olimpiada\\resources\\judet" + i + ".in")) {
                for (int j = 0; j < 100; j++) {
                    for (int k = 0; k < 5; k++) {
                        int idConcurent = 100 * i + j;
                        int idProblema = k;
                        int punctaj = generatePunctaj();
                        int timpIncarcare = generateTimpIncarcare(startTimes[k], deadlines[k]);
                        writer.write(idConcurent + "," + idProblema + "," + punctaj + "," + timpIncarcare + "\n");
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static int generateTimpIncarcare(Integer startTime, Integer deadline) {
        Random r = new Random();
        return r.nextInt(startTime + 1, deadline + 5);
    }

    private static int generatePunctaj() {
        Random r = new Random();
        return r.nextInt(10, 101);
    }
}
