package sk.ukf.heuristic.deprecated;

import sk.ukf.model.CSPProblem;
import sk.ukf.model.Variable;

import java.util.List;
import java.util.Map;

public interface ValueHeuristic {
    List<Integer> orderValues(Variable variable, CSPProblem problem, Map<Variable, Integer> assignment);
}
