package sk.ukf.heuristic.deprecated;

import sk.ukf.model.CSPProblem;
import sk.ukf.model.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultValueHeuristic implements ValueHeuristic {

    @Override
    public List<Integer> orderValues(Variable variable,
                                     CSPProblem problem,
                                     Map<Variable, Integer> assignment) {
        return new ArrayList<>(variable.getDomain());
    }
}
