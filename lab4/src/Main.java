public class Main {
    public static void main(String[] args) {
        int threadsNumber = Integer.parseInt(args[0]);
        int polynomialNumber = 5;
        MyList result;

        long startTime = System.nanoTime();
        if (threadsNumber == 1) {
            result = Solver.sequentialSolve(polynomialNumber);
        } else {
            result = Solver.parallelSolve(polynomialNumber, threadsNumber);
        }
        long endTime = System.nanoTime();

        System.out.println(endTime - startTime);

        UtilsLab4.writeResultToFile(result, threadsNumber);
    }
}
