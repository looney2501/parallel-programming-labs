public class Main {
    public static void main(String[] args) {
        int threadsNumber = Integer.parseInt(args[0]);
        MyList result;

        long startTime = System.nanoTime();
        if (threadsNumber == 1) {
            result = Solver.sequentialSolve(10);
        } else {
            result = Solver.parallelSolve(10, threadsNumber);
        }
        long endTime = System.nanoTime();

        System.out.println(endTime - startTime);

        UtilsLab4.writeResultToFile(result, threadsNumber);
    }
}
