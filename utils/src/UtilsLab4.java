import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UtilsLab4 {
    private static int generateRandomMonomialNumber(int bound) {
        Random r = new Random();
        return r.nextInt(450, bound + 1);
    }

    private static double generateRandomCoefficient() {
        Random r = new Random();
        return r.nextDouble() * 1000 - 500;
    }

    private static List<Integer> generateRandomDegrees(int maximumDegree, int monomialNumber) {
        List<Integer> degrees = IntStream.range(0, maximumDegree + 1).boxed().collect(Collectors.toList());
        Collections.shuffle(degrees);
        return degrees.stream()
                .limit(monomialNumber)
                .sorted()
                .toList();
    }

    public static void writeResultToFile(MyList list, int noThreads) {
        Node current = list.start.next;
        String path = "D:\\Proiecte\\Java\\PPD\\lab4\\resources\\output\\result-th-" + noThreads + ".out";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            while (current != list.end) {
                writer.write(current.exponent + " " + current.coefficient);
                writer.newLine();
                current = current.next;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {
        int monomialNumberBound = 500;
        int polynomialDegreeBound = 100000;
        int polynomialNumber = 5;

        for (int i = 0; i < polynomialNumber; i++) {
            int monomialNumber = generateRandomMonomialNumber(monomialNumberBound);
            List<Integer> degrees = generateRandomDegrees(polynomialDegreeBound, monomialNumber);

            String path = "D:\\Proiecte\\Java\\PPD\\lab4\\resources\\input\\polynomial" + i + ".in";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
                writer.write("" + monomialNumber);
                writer.newLine();
                for (int degree : degrees) {
                    double coefficient = generateRandomCoefficient();
                    writer.write(degree + " " + coefficient);
                    writer.newLine();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
