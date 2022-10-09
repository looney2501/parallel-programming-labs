import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        Solver seqSolver1 = new Solver(6, 6, 3, 3, 1);
//        seqSolver1.generateInputData();
        seqSolver1.setImageMatrix(Utils.readMatrixFromFile(Path.of("lab1\\resources\\test1\\imageMatrix.in")));
        seqSolver1.setFilterMatrix(Utils.readMatrixFromFile(Path.of("lab1\\resources\\test1\\filterMatrix.in")));
        seqSolver1.sequentialSolve();
    }
}
