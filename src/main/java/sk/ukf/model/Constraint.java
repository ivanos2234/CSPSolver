package sk.ukf.model;

import java.util.Map;

public interface Constraint {
    boolean isSatisfied(Map<Variable, Integer> assignment);
}