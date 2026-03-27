package sk.ukf.heuristic;

import sk.ukf.model.CSPProblem;
import sk.ukf.model.Constraint;
import sk.ukf.model.Variable;
import sk.ukf.solver.SearchState;

import java.util.*;

public class StateLCVHeuristic implements StateValueHeuristic {

    @Override
    public List<Integer> orderValues(Variable variable, CSPProblem problem, SearchState state) {

        List<Integer> values = new ArrayList<>(state.getCurrentDomains().get(variable));

        values.sort(Comparator.comparingInt(
                value -> countConflicts(variable, value, problem, state)
        ));

        return values;
    }

    private int countConflicts(Variable variable,
                               Integer value,
                               CSPProblem problem,
                               SearchState state) {
        int conflicts = 0;

        for (Variable other : problem.getVariables()) {
            if (other.equals(variable) || state.getAssignment().containsKey(other)) {
                continue;
            }

            boolean hasSupport = false;

            for (Integer otherValue : state.getCurrentDomains().get(other)) {
                Map<Variable, Integer> tempAssignment = new HashMap<>(state.getAssignment());
                tempAssignment.put(variable, value);
                tempAssignment.put(other, otherValue);

                if (isConsistent(problem, tempAssignment)) {
                    hasSupport = true;
                } else {
                    conflicts++;
                }
            }

            if (!hasSupport) {
                conflicts += 1000;
            }
        }

        return conflicts;
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
