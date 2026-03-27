package sk.ukf.heuristic;

import sk.ukf.model.CSPProblem;
import sk.ukf.model.Variable;
import sk.ukf.solver.SearchState;

public class StateFirstUnassignedHeuristic implements StateVariableHeuristic {

    @Override
    public Variable selectVariable(CSPProblem problem, SearchState state) {
        for (Variable variable : problem.getVariables()) {
            if (!state.getAssignment().containsKey(variable)) {
                return variable;
            }
        }
        return null;
    }
}
