package sk.ukf.model;

import java.util.Map;

public class EqualityConstraint implements Constraint {

    private final Variable left;
    private final Variable right;

    public EqualityConstraint(Variable left, Variable right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean isSatisfied(Map<Variable, Integer> assignment) {
        if (!assignment.containsKey(left) || !assignment.containsKey(right)) {
            return true;
        }

        return assignment.get(left).equals(assignment.get(right));
    }
}
