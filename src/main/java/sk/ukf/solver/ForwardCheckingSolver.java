package sk.ukf.solver;

import sk.ukf.heuristic.*;
import sk.ukf.model.CSPProblem;
import sk.ukf.model.Constraint;
import sk.ukf.model.Variable;

import java.util.*;

public class ForwardCheckingSolver implements Solver {

    private List<String> solutionPath;
    private long recursiveCalls;
    private long backtracks;
    private long failedBranches;

    private final StateVariableHeuristic variableHeuristic;
    private final StateValueHeuristic valueHeuristic;

    public ForwardCheckingSolver() {
        this.variableHeuristic = new StateFirstUnassignedHeuristic();
        this.valueHeuristic = new StateDefaultValueHeuristic();
    }

    public ForwardCheckingSolver(StateVariableHeuristic variableHeuristic) {
        this.variableHeuristic = variableHeuristic;
        this.valueHeuristic = new StateDefaultValueHeuristic();
    }

    public ForwardCheckingSolver(StateValueHeuristic valueHeuristic) {
        this.valueHeuristic = valueHeuristic;
        this.variableHeuristic = new StateFirstUnassignedHeuristic();
    }

    public ForwardCheckingSolver(StateVariableHeuristic variableHeuristic,
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
        boolean solved = forwardCheckBacktrack(problem, initialState, solutionPath);

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

    private boolean forwardCheckBacktrack(CSPProblem problem, SearchState state, List<String> currentPath) {
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

            Set<Integer> singleton = new HashSet<>();
            singleton.add(value);
            nextState.getCurrentDomains().put(variable, singleton);

            if (!isConsistent(problem, nextState.getAssignment())) {
                currentPath.removeLast();
                failedBranches++;
                continue;
            }

            boolean domainsOk = applyForwardChecking(problem, nextState, variable);

            if (!domainsOk) {
                currentPath.removeLast();
                failedBranches++;
                continue;
            }

            if (forwardCheckBacktrack(problem, nextState, solutionPath)) {
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

    private boolean applyForwardChecking(CSPProblem problem,
                                         SearchState state,
                                         Variable newlyAssigned) {

        for (Variable other : problem.getVariables()) {
            if (state.getAssignment().containsKey(other)) {
                continue;
            }

            Set<Integer> originalDomain = state.getCurrentDomains().get(other);
            Set<Integer> filteredDomain = new HashSet<>();

            for (Integer value : originalDomain) {
                Map<Variable, Integer> tempAssignment = new HashMap<>(state.getAssignment());
                tempAssignment.put(other, value);

                if (isConsistent(problem, tempAssignment)) {
                    filteredDomain.add(value);
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
