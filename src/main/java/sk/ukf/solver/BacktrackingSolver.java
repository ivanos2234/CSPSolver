package sk.ukf.solver;


import sk.ukf.model.CSPProblem;
import sk.ukf.model.Constraint;
import sk.ukf.model.Variable;

import java.util.HashMap;
import java.util.Map;

public class BacktrackingSolver implements Solver {

    private long recursiveCalls;
    private long backtracks;

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

        Variable unassigned = selectUnassignedVariable(problem, assignment);

        for (Integer value : unassigned.getDomain()) {
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

    private Variable selectUnassignedVariable(CSPProblem problem, Map<Variable, Integer> assignment) {
        for (Variable variable : problem.getVariables()) {
            if (!assignment.containsKey(variable)) {
                return variable;
            }
        }
        return null;
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
