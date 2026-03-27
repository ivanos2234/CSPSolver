package sk.ukf.app;

import sk.ukf.heuristic.*;
import sk.ukf.model.CSPProblem;
import sk.ukf.model.SendMoreMoneyFactory;
import sk.ukf.model.Variable;
import sk.ukf.solver.*;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        CSPProblem problem = SendMoreMoneyFactory.create(10);

        System.out.println("=== Plain Backtracking ===");
        runAndPrint(new BacktrackingSolver(), problem);

        System.out.println("\n=== Backtracking + MRV ===");
        runAndPrint(new BacktrackingSolver(new StateMRVHeuristic()), problem);

        // System.out.println("\n=== Backtracking + LCV ===");
        // runAndPrint(new BacktrackingSolver(new StateLCVHeuristic()), problem);

        System.out.println("\n=== Backtracking + MRV + LCV ===");
        runAndPrint(new BacktrackingSolver(new StateMRVHeuristic(), new StateLCVHeuristic()), problem);

        System.out.println("\n=== Forward Checking ===");
        runAndPrint(new ForwardCheckingSolver(), problem);

        System.out.println("\n=== Forward Checking + MRV ===");
        runAndPrint(new ForwardCheckingSolver(new StateMRVHeuristic()), problem);

        System.out.println("\n=== Forward Checking + LCV ===");
        runAndPrint(new ForwardCheckingSolver(new StateLCVHeuristic()), problem);

        System.out.println("\n=== Forward Checking + MRV + LCV===");
        runAndPrint(new ForwardCheckingSolver(new StateMRVHeuristic(), new StateLCVHeuristic()), problem);

        System.out.println("\n=== AC3-like ===");
        runAndPrint(new AC3LikeSolver(), problem);

        System.out.println("\n=== AC3-like + MRV ===");
        runAndPrint(new AC3LikeSolver(new StateMRVHeuristic()), problem);

        System.out.println("\n=== AC3-like + LCV ===");
        runAndPrint(new AC3LikeSolver(new StateLCVHeuristic()), problem);

        System.out.println("\n=== AC3-like + MRV + LCV ===");
        runAndPrint(new AC3LikeSolver(new StateMRVHeuristic(), new StateLCVHeuristic()), problem);
    }

    private static void runAndPrint(Solver solver, CSPProblem problem) {
        Solution solution = solver.solve(problem);

        System.out.println("Solved: " + solution.isSolved());
        System.out.println("Time (ms): " + solution.getTimeMillis());
        System.out.println("Recursive calls: " + solution.getRecursiveCalls());
        System.out.println("Backtracks: " + solution.getBacktracks());
        System.out.println("Failed Branches: " + solution.getFailedBranches());
        System.out.println("Solution Path: " + solution.getSolutionPath());
        System.out.println();

        for (Map.Entry<Variable, Integer> entry : solution.getAssignment().entrySet()) {
            System.out.println(entry.getKey().getName() + " = " + entry.getValue());
        }
    }
}