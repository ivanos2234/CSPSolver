package sk.ukf.heuristic;

import sk.ukf.model.CSPProblem;
import sk.ukf.model.Variable;
import sk.ukf.solver.SearchState;

public interface StateVariableHeuristic {
    Variable selectVariable(CSPProblem problem, SearchState state);
}