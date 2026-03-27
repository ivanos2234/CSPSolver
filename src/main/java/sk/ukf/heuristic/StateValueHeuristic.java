package sk.ukf.heuristic;

import sk.ukf.model.CSPProblem;
import sk.ukf.model.Variable;
import sk.ukf.solver.SearchState;

import java.util.List;

public interface StateValueHeuristic {
    List<Integer> orderValues(Variable variable, CSPProblem problem, SearchState state);
}
