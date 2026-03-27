package sk.ukf.heuristic;

import sk.ukf.model.CSPProblem;
import sk.ukf.model.Constraint;
import sk.ukf.model.Variable;
import sk.ukf.solver.SearchState;

import java.util.*;

public class StateLCVHeuristic implements StateValueHeuristic {

    @Override
    public List<Integer> orderValues(Variable variable, CSPProblem problem, SearchState state) {
        List<Integer> values = new ArrayList<>(state.getCurrentDomains().get(variable));

        values.sort((v1, v2) -> {
            int score1 = scoreValue(variable, v1, problem, state);
            int score2 = scoreValue(variable, v2, problem, state);

            return Integer.compare(score2, score1);
        });

        return values;
    }

    private int scoreValue(Variable variable,
                           Integer value,
                           CSPProblem problem,
                           SearchState state) {

        SearchState tempState = state.deepCopy();

        tempState.getAssignment().put(variable, value);

        Set<Integer> singleton = new HashSet<>();
        singleton.add(value);
        tempState.getCurrentDomains().put(variable, singleton);

        if (!isConsistent(problem, tempState.getAssignment())) {
            return -1;
        }

        boolean ok = applyLightFiltering(problem, tempState, variable);
        if (!ok) {
            return -1;
        }

        int totalRemaining = 0;

        for (Variable other : problem.getVariables()) {
            if (!tempState.getAssignment().containsKey(other)) {
                totalRemaining += tempState.getCurrentDomains().get(other).size();
            }
        }

        return totalRemaining;
    }

    private boolean applyLightFiltering(CSPProblem problem,
                                        SearchState state,
                                        Variable newlyAssigned) {

        for (Variable other : problem.getVariables()) {
            if (other.equals(newlyAssigned) || state.getAssignment().containsKey(other)) {
                continue;
            }

            Set<Integer> originalDomain = state.getCurrentDomains().get(other);
            Set<Integer> filteredDomain = new HashSet<>();

            for (Integer otherValue : originalDomain) {
                Map<Variable, Integer> tempAssignment = new HashMap<>(state.getAssignment());
                tempAssignment.put(other, otherValue);

                if (isConsistent(problem, tempAssignment)) {
                    filteredDomain.add(otherValue);
                }
            }

            if (filteredDomain.isEmpty()) {
                return false;
            }

            state.getCurrentDomains().put(other, filteredDomain);
        }

        return true;
    }

    private boolean isConsistent(CSPProblem problem, Map<Variable, Integer> assignment) {
        for (Constraint constraint : problem.getConstraints()) {
            if (!constraint.isSatisfied(assignment)) {
                return false;
            }
        }
        return true;
    }
}
