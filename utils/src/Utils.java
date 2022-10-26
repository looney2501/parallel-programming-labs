import java.io.*;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Random;
import java.util.Scanner;

public final class Utils {

    public static double[][] readMatrixFromFile(Path p) throws IOException {
        try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(p.toFile())))) {
            double[][] matrix;
            String[] line = scanner.nextLine().trim().split(" ");
            int m = Integer.parseInt(line[0]);
            int n = Integer.parseInt(line[1]);
            matrix = new double[m][n];
            for (int i = 0; i < m; i++) {
                line = scanner.nextLine().trim().split(" ");
                for (int j = 0; j < n; j++) {
                    matrix[i][j] = Double.parseDouble(line[j]);
                }
            }
            scanner.close();
            return matrix;
        }
    }

    public static void writeMatrixToFile(double[][] matrix, Path p) throws IOException {
        try (FileWriter writer = new FileWriter(p.toFile())) {
            DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance();
            dfs.setDecimalSeparator('.');
            DecimalFormat df = new DecimalFormat("0.00", dfs);
            df.setRoundingMode(RoundingMode.UP);

            for (double[] line : matrix) {
                for (double
                        value : line) {
                    writer.write(String.format("%s ", df.format(value)));
                }
                writer.write('\n');
            }
        }
    }

    public static void writeInputMatrixToFile(double[][] matrix, Path p) throws IOException {
        try (FileWriter writer = new FileWriter(p.toFile())) {
            writer.write(String.format("%d %d\n", matrix.length, matrix[0].length));
            DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance();
            dfs.setDecimalSeparator('.');
            DecimalFormat df = new DecimalFormat("0.00", dfs);
            df.setRoundingMode(RoundingMode.UP);

            for (double[] line : matrix) {
                for (double value : line) {
                    writer.write(String.format("%s ", df.format(value)));
                }
                writer.write('\n');
            }
        }
    }

    private static double generatePixelValue() {
        Random r = new Random();
        return 255 * r.nextDouble();
    }

    public static double[][] generateRandomMatrix(int m, int n) {
        double[][] matrix = new double[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = generatePixelValue();
            }
        }
        return matrix;
    }
}
