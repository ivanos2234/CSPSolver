package sk.ukf.solver;


import sk.ukf.heuristic.*;
import sk.ukf.model.CSPProblem;
import sk.ukf.model.Constraint;
import sk.ukf.model.Variable;

import java.util.*;

public class BacktrackingSolver implements Solver {

    private List<String> solutionPath;
    private long recursiveCalls;
    private long backtracks;
    private long failedBranches;
    private Solution solution;

    private final StateVariableHeuristic variableHeuristic;
    private final StateValueHeuristic valueHeuristic;

    public BacktrackingSolver() {
        this.variableHeuristic = new StateFirstUnassignedHeuristic();
        this.valueHeuristic = new StateDefaultValueHeuristic();
    }

    public BacktrackingSolver(StateVariableHeuristic variableHeuristic) {
        this.variableHeuristic = variableHeuristic;
        this.valueHeuristic = new StateDefaultValueHeuristic();
    }

    public BacktrackingSolver(StateValueHeuristic valueHeuristic) {
        this.valueHeuristic = valueHeuristic;
        this.variableHeuristic = new StateFirstUnassignedHeuristic();
    }

    public BacktrackingSolver(StateVariableHeuristic variableHeuristic,
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
                failedBranches,
                -1,
                -1,
                -1,
                -1
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
        List<Integer> orderedValues = valueHeuristic.orderValues(variable, problem, state);

        for (Integer value : orderedValues) {
            if (!state.getCurrentDomains().get(variable).contains(value)) {
                continue;
            }

            state.getAssignment().put(variable, value);
            currentPath.add(variable.getName() + " = " + value);

            if (isConsistent(problem, state.getAssignment())) {
                if (backtrack(problem, state, solutionPath)) {
                    state.getAssignment().putAll(state.getAssignment());
                    return true;
                }
            }

            state.getAssignment().remove(variable);
            currentPath.removeLast();
            failedBranches++;
        }

        backtracks++;
        return false;
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
