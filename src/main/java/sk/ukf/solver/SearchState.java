package sk.ukf.solver;

import sk.ukf.model.Variable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SearchState {

    private final Map<Variable, Integer> assignment;
    private final Map<Variable, Set<Integer>> currentDomains;

    public SearchState(Map<Variable, Integer> assignment,
                       Map<Variable, Set<Integer>> currentDomains) {
        this.assignment = assignment;
        this.currentDomains = currentDomains;
    }

    public Map<Variable, Integer> getAssignment() {
        return assignment;
    }

    public Map<Variable, Set<Integer>> getCurrentDomains() {
        return currentDomains;
    }

    public SearchState deepCopy() {
        Map<Variable, Integer> newAssignment = new HashMap<>(assignment);
        Map<Variable, Set<Integer>> newDomains = new HashMap<>();

        for (Map.Entry<Variable, Set<Integer>> entry : currentDomains.entrySet()) {
            newDomains.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }

        return new SearchState(newAssignment, newDomains);
    }
}
