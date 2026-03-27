package sk.ukf.heuristic.deprecated;

import sk.ukf.model.CSPProblem;
import sk.ukf.model.Constraint;
import sk.ukf.model.Variable;

import java.util.*;

public class LCVHeuristic implements ValueHeuristic {

    @Override
    public List<Integer> orderValues(Variable variable,
                                     CSPProblem problem,
                                     Map<Variable, Integer> assignment) {
        List<Integer> values = new ArrayList<>(variable.getDomain());

        values.sort(Comparator.comparingInt(
                value -> countConflicts(variable, value, problem, assignment)
        ));

        return values;
    }

    private int countConflicts(Variable variable,
                               Integer value,
                               CSPProblem problem,
                               Map<Variable, Integer> assignment) {
        int conflicts = 0;

        for (Variable other : problem.getVariables()) {
            if (other.equals(variable) || assignment.containsKey(other)) {
                continue;
            }

            boolean hasSupport = false;

            for (Integer otherValue : other.getDomain()) {
                Map<Variable, Integer> tempAssignment = new HashMap<>(assignment);
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
