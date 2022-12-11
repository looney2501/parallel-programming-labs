public class Main {
    public static void main(String[] args) {
        int threadsNumber = Integer.parseInt(args[0]);
        int producersNumber = 1;
        if (threadsNumber != 1) {
            producersNumber = Integer.parseInt(args[1]);
        }
        int polynomialNumber = 5;
        MyList result;

        long startTime = System.nanoTime();
        if (threadsNumber == 1) {
            result = Solver.sequentialSolve(polynomialNumber);
        } else {
            result = Solver.parallelSolve(polynomialNumber, threadsNumber, producersNumber);
        }
        long endTime = System.nanoTime();

        System.out.println(endTime - startTime);

        UtilsLab5.writeResultToFile(result, threadsNumber, producersNumber);
    }
}
