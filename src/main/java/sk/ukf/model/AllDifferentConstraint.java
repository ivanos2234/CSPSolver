package sk.ukf.model;

import java.util.*;

public class AllDifferentConstraint implements Constraint {

    private final List<Variable> variables;

    public AllDifferentConstraint(List<Variable> variables) {
        this.variables = variables;
    }

    @Override
    public boolean isSatisfied(Map<Variable, Integer> assignment) {
        Set<Integer> seen = new HashSet<>();

        for (Variable v : variables) {
            Integer value = assignment.get(v);
            if (value != null && !seen.add(value)) {
                return false;
            }
        }
        return true;
    }
}
