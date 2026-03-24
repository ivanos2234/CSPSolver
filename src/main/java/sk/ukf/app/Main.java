package sk.ukf.app;

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

        Solver solver = new BacktrackingSolver();
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