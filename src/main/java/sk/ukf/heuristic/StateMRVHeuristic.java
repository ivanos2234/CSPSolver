package sk.ukf.heuristic;

import sk.ukf.model.CSPProblem;
import sk.ukf.model.Variable;
import sk.ukf.solver.SearchState;

public class StateMRVHeuristic implements StateVariableHeuristic {

    @Override
    public Variable selectVariable(CSPProblem problem, SearchState state) {
        Variable best = null;
        int minSize = Integer.MAX_VALUE;

        for (Variable variable : problem.getVariables()) {
            if (state.getAssignment().containsKey(variable)) {
                continue;
            }

            int size = state.getCurrentDomains().get(variable).size();
            if (size < minSize) {
                minSize = size;
                best = variable;
            }
        }

        return best;
    }
}
