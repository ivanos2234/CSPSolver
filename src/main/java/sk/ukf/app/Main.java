package sk.ukf.app;

import sk.ukf.heuristic.LCVHeuristic;
import sk.ukf.heuristic.MRVHeuristic;
import sk.ukf.model.CSPProblem;
import sk.ukf.model.SendMoreMoneyFactory;
import sk.ukf.model.Variable;
import sk.ukf.solver.BacktrackingSolver;
import sk.ukf.solver.ForwardCheckingSolver;
import sk.ukf.solver.Solution;
import sk.ukf.solver.Solver;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        CSPProblem problem = SendMoreMoneyFactory.create();

        System.out.println("=== Plain Backtracking ===");
        runAndPrint(new BacktrackingSolver(), problem);

        System.out.println("\n=== Backtracking + MRV ===");
        runAndPrint(new BacktrackingSolver(new MRVHeuristic()), problem);

        // System.out.println("\n=== Backtracking + LCV ===");
        // runAndPrint(new BacktrackingSolver(new LCVHeuristic()), problem);

        System.out.println("\n=== Backtracking + MRV + LCV ===");
        runAndPrint(new BacktrackingSolver(new MRVHeuristic(), new LCVHeuristic()), problem);

        System.out.println("\n=== Forward Checking + MRV ===");
        runAndPrint(new ForwardCheckingSolver(new MRVHeuristic()), problem);

        // System.out.println("\n=== Forward Checking + LCV ===");
        // runAndPrint(new ForwardCheckingSolver(new LCVHeuristic()), problem);

        System.out.println("\n=== Forward Checking + MRV + LCV===");
        runAndPrint(new ForwardCheckingSolver(new MRVHeuristic(), new LCVHeuristic()), problem);
    }

    private static void runAndPrint(Solver solver, CSPProblem problem) {
        Solution solution = solver.solve(problem);

        System.out.println("Solved: " + solution.isSolved());
        System.out.println("Time (ms): " + solution.getTimeMillis());
        System.out.println("Recursive calls: " + solution.getRecursiveCalls());
        System.out.println("Backtracks: " + solution.getBacktracks());

        for (Map.Entry<Variable, Integer> entry : solution.getAssignment().entrySet()) {
            System.out.println(entry.getKey().getName() + " = " + entry.getValue());
        }
    }
}