package sk.ukf.heuristic;

import sk.ukf.model.CSPProblem;
import sk.ukf.model.Constraint;
import sk.ukf.model.Variable;

import java.util.HashMap;
import java.util.Map;

public class MRVHeuristic implements VariableHeuristic {

    @Override
    public Variable selectVariable(CSPProblem problem, Map<Variable, Integer> assignment) {
        Variable bestVariable = null;
        int minRemainingValues = Integer.MAX_VALUE;

        for (Variable variable : problem.getVariables()) {
            if (assignment.containsKey(variable)) {
                continue;
            }

            int remainingValues = countConsistentValues(problem, variable, assignment);

            if (remainingValues < minRemainingValues) {
                minRemainingValues = remainingValues;
                bestVariable = variable;
            }
        }

        return bestVariable;
    }

    // todo make more effective
    private int countConsistentValues(CSPProblem problem,
                                      Variable variable,
                                      Map<Variable, Integer> assignment) {
        int count = 0;

        for (Integer value : variable.getDomain()) {
            Map<Variable, Integer> tempAssignment = new HashMap<>(assignment);
            tempAssignment.put(variable, value);

            if (isConsistent(problem, tempAssignment)) {
                count++;
            }
        }

        return count;
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