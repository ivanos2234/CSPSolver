package sk.ukf.solver;


import sk.ukf.heuristic.DefaultValueHeuristic;
import sk.ukf.heuristic.FirstUnassignedHeuristic;
import sk.ukf.heuristic.ValueHeuristic;
import sk.ukf.heuristic.VariableHeuristic;
import sk.ukf.model.CSPProblem;
import sk.ukf.model.Constraint;
import sk.ukf.model.Variable;

import java.util.*;

public class BacktrackingSolver implements Solver {

    private final VariableHeuristic variableHeuristic;
    private final ValueHeuristic valueHeuristic;

    private long recursiveCalls;
    private long backtracks;

    public BacktrackingSolver() {
        this.variableHeuristic = new FirstUnassignedHeuristic();
        this.valueHeuristic = new DefaultValueHeuristic();
    }

    public BacktrackingSolver(VariableHeuristic variableHeuristic) {
        this.variableHeuristic = variableHeuristic;
        this.valueHeuristic = new DefaultValueHeuristic();
    }

    public BacktrackingSolver(ValueHeuristic valueHeuristic) {
        this.valueHeuristic = valueHeuristic;
        this.variableHeuristic = new FirstUnassignedHeuristic();
    }

    public BacktrackingSolver(VariableHeuristic variableHeuristic,
                              ValueHeuristic valueHeuristic) {
        this.variableHeuristic = variableHeuristic;
        this.valueHeuristic = valueHeuristic;
    }

    @Override
    public Solution solve(CSPProblem problem) {
        recursiveCalls = 0;
        backtracks = 0;

        long start = System.currentTimeMillis();

        Map<Variable, Integer> assignment = new HashMap<>();
        boolean solved = backtrack(problem, assignment);

        long end = System.currentTimeMillis();

        return new Solution(
                new HashMap<>(assignment),
                end - start,
                recursiveCalls,
                backtracks,
                solved
        );
    }

    private boolean backtrack(CSPProblem problem, Map<Variable, Integer> assignment) {
        recursiveCalls++;

        if (assignment.size() == problem.getVariables().size()) {
            return isConsistent(problem, assignment);
        }

        Variable unassigned = variableHeuristic.selectVariable(problem, assignment);
        List<Integer> orderedValues = valueHeuristic.orderValues(unassigned, problem, assignment);

        for (Integer value : orderedValues) {
            assignment.put(unassigned, value);

            if (isConsistent(problem, assignment)) {
                if (backtrack(problem, assignment)) {
                    return true;
                }
            }

            assignment.remove(unassigned);
            backtracks++;
        }

        return false;
    }

    private boolean isConsistent(CSPProblem problem, Map<Variable, Integer> assignment) {
        for (Constraint constraint : problem.getConstraints()) {
            if (!constraint.isSatisfied(assignment)) {
                return false;
            }
        }
        return true;
    }

}
