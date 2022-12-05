import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Solver {
    public static MyList sequentialSolve(int polynomialNumber) {
        MyList result = new MyList();

        for (int i = 0; i < polynomialNumber; i++) {
            String path = "lab4\\resources\\input\\polynomial" + i + ".in";
            try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
                int monomialNumber = Integer.parseInt(reader.readLine().strip());
                for (int j = 0; j < monomialNumber; j++) {
                    var line = reader.readLine().strip().split(" ");
                    int exponent = Integer.parseInt(line[0].strip());
                    double coefficient = Double.parseDouble(line[1].strip());
                    result.add(exponent, coefficient);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return result;
    }
}
