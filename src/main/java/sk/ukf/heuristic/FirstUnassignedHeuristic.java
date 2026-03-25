package sk.ukf.heuristic;


import sk.ukf.model.CSPProblem;
import sk.ukf.model.Variable;

import java.util.Map;

public class FirstUnassignedHeuristic implements VariableHeuristic {

    @Override
    public Variable selectVariable(CSPProblem problem, Map<Variable, Integer> assignment) {
        for (Variable variable : problem.getVariables()) {
            if (!assignment.containsKey(variable)) {
                return variable;
            }
        }
        return null;
    }
}