package sk.ukf.app;

import sk.ukf.heuristic.MRVHeuristic;
import sk.ukf.model.CSPProblem;
import sk.ukf.model.SendMoreMoneyFactory;
import sk.ukf.model.Variable;
import sk.ukf.solver.BacktrackingSolver;
import sk.ukf.solver.Solution;
import sk.ukf.solver.Solver;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        CSPProblem problem = SendMoreMoneyFactory.create();

        System.out.println("=== Plain Backtracking ===");
        Solver plainSolver = new BacktrackingSolver();
        Solution plainSolution = plainSolver.solve(problem);
        printSolution(plainSolution);

        System.out.println("\n=== Backtracking + MRV ===");
        Solver mrvSolver = new BacktrackingSolver(new MRVHeuristic());
        Solution mrvSolution = mrvSolver.solve(problem);
        printSolution(mrvSolution);
    }

    private static void printSolution(Solution solution) {
        System.out.println("Solved: " + solution.isSolved());
        System.out.println("Time (ms): " + solution.getTimeMillis());
        System.out.println("Recursive calls: " + solution.getRecursiveCalls());
        System.out.println("Backtracks: " + solution.getBacktracks());

        for (Map.Entry<Variable, Integer> entry : solution.getAssignment().entrySet()) {
            System.out.println(entry.getKey().getName() + " = " + entry.getValue());
        }
    }
}