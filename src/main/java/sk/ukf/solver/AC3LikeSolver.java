package sk.ukf.solver;

import sk.ukf.heuristic.*;
import sk.ukf.model.CSPProblem;
import sk.ukf.model.Constraint;
import sk.ukf.model.Variable;

import java.util.*;

public class AC3LikeSolver implements Solver {

    private List<String> solutionPath;
    private long recursiveCalls;
    private long failedBranches;
    private long backtracks;

    private final StateVariableHeuristic variableHeuristic;
    private final StateValueHeuristic valueHeuristic;

    public AC3LikeSolver() {
        this.variableHeuristic = new StateFirstUnassignedHeuristic();
        this.valueHeuristic = new StateDefaultValueHeuristic();
    }

    public AC3LikeSolver(StateVariableHeuristic variableHeuristic) {
        this.variableHeuristic = variableHeuristic;
        this.valueHeuristic = new StateDefaultValueHeuristic();
    }

    public AC3LikeSolver(StateValueHeuristic valueHeuristic) {
        this.valueHeuristic = valueHeuristic;
        this.variableHeuristic = new StateFirstUnassignedHeuristic();
    }

    public AC3LikeSolver(StateVariableHeuristic variableHeuristic,
                              StateValueHeuristic valueHeuristic) {
        this.variableHeuristic = variableHeuristic;
        this.valueHeuristic = valueHeuristic;
    }

    @Override
    public Solution solve(CSPProblem problem) {
        recursiveCalls = 0;
        backtracks = 0;
        failedBranches = 0;
        solutionPath = new ArrayList<>();

        long start = System.currentTimeMillis();

        SearchState initialState = createInitialState(problem);
        boolean solved = backtrack(problem, initialState, solutionPath);

        long end = System.currentTimeMillis();

        return new Solution(
                new HashMap<>(initialState.getAssignment()),
                end - start,
                recursiveCalls,
                backtracks,
                solved,
                solutionPath, 
                failedBranches
        );
    }

    private SearchState createInitialState(CSPProblem problem) {
        Map<Variable, Integer> assignment = new HashMap<>();
        Map<Variable, Set<Integer>> domains = new HashMap<>();

        for (Variable variable : problem.getVariables()) {
            domains.put(variable, new HashSet<>(variable.getDomain()));
        }

        return new SearchState(assignment, domains);
    }

    private boolean backtrack(CSPProblem problem, SearchState state, List<String> currentPath) {
        recursiveCalls++;

        if (state.getAssignment().size() == problem.getVariables().size()) {
            return isConsistent(problem, state.getAssignment());
        }

        Variable variable = variableHeuristic.selectVariable(problem, state);
        List<Integer> orderedValues =
                valueHeuristic.orderValues(variable, problem, state);

        for (Integer value : orderedValues) {
            if (!state.getCurrentDomains().get(variable).contains(value)) {
                continue;
            }

            SearchState nextState = state.deepCopy();
            nextState.getAssignment().put(variable, value);
            currentPath.add(variable.getName() + " = " + value);

            // System.out.println("Assigning " + variable.getName() + " = " + value);
            // printDomains(nextState);

            Set<Integer> singleton = new HashSet<>();
            singleton.add(value);
            nextState.getCurrentDomains().put(variable, singleton);

            if (!isConsistent(problem, nextState.getAssignment())) {
                currentPath.removeLast();
                failedBranches++;
                continue;
            }

            boolean domainsOk = applyArcConsistencyLike(problem, nextState);

            if (!domainsOk) {
                currentPath.removeLast();
                failedBranches++;
                continue;
            }

            if (backtrack(problem, nextState, solutionPath)) {
                state.getAssignment().clear();
                state.getAssignment().putAll(nextState.getAssignment());
                return true;
            }

            currentPath.removeLast();
            failedBranches++;
        }

        backtracks++;
        return false;
    }

    private boolean applyArcConsistencyLike(CSPProblem problem, SearchState state) {
        boolean changed = true;

        while (changed) {
            changed = false;

            for (Variable variable : problem.getVariables()) {
                if (state.getAssignment().containsKey(variable)) {
                    continue;
                }

                Set<Integer> currentDomain = state.getCurrentDomains().get(variable);
                Set<Integer> filteredDomain = new HashSet<>();

                for (Integer value : currentDomain) {
                    if (hasSupport(problem, state, variable, value)) {
                        filteredDomain.add(value);
                    }
                }

                if (filteredDomain.isEmpty()) {

                    // System.out.println("Domain wipeout for " + variable.getName());

                    return false;
                }

                if (filteredDomain.size() < currentDomain.size()) {

                    // System.out.println("Domain reduced for " + variable.getName() + ": " + currentDomain + " -> " + filteredDomain);

                    state.getCurrentDomains().put(variable, filteredDomain);
                    changed = true;
                }
            }
        }

        return true;
    }

    private boolean hasSupport(CSPProblem problem,
                               SearchState state,
                               Variable variable,
                               Integer value) {

        Map<Variable, Integer> tempAssignment = new HashMap<>(state.getAssignment());
        tempAssignment.put(variable, value);

        if (!isConsistent(problem, tempAssignment)) {
            return false;
        }

        for (Variable other : problem.getVariables()) {
            if (other.equals(variable) || tempAssignment.containsKey(other)) {
                continue;
            }

            boolean foundSupport = false;

            for (Integer otherValue : state.getCurrentDomains().get(other)) {
                Map<Variable, Integer> temp2 = new HashMap<>(tempAssignment);
                temp2.put(other, otherValue);

                if (isConsistent(problem, temp2)) {
                    foundSupport = true;
                    break;
                }
            }

            if (!foundSupport) {
                return false;
            }
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

    private void printDomains(SearchState state) {
        System.out.println("Current domains:");
        for (Map.Entry<Variable, Set<Integer>> entry : state.getCurrentDomains().entrySet()) {
            System.out.println(entry.getKey().getName() + " -> " + entry.getValue());
        }
        System.out.println("--------------------------");
    }
}
