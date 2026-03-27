package sk.ukf.heuristic;

import sk.ukf.model.CSPProblem;
import sk.ukf.model.Variable;
import sk.ukf.solver.SearchState;

import java.util.ArrayList;
import java.util.List;

public class StateDefaultValueHeuristic implements StateValueHeuristic {

    @Override
    public List<Integer> orderValues(Variable variable, CSPProblem problem, SearchState state) {
        return new ArrayList<>(state.getCurrentDomains().get(variable));
    }
}
