package sk.ukf.heuristic.deprecated;

import sk.ukf.model.CSPProblem;
import sk.ukf.model.Variable;

import java.util.Map;

public interface VariableHeuristic {
    Variable selectVariable(CSPProblem problem, Map<Variable, Integer> assignment);
}