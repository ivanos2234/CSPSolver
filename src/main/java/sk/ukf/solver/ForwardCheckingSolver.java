package sk.ukf.solver;

import sk.ukf.heuristic.DefaultValueHeuristic;
import sk.ukf.heuristic.FirstUnassignedHeuristic;
import sk.ukf.heuristic.ValueHeuristic;
import sk.ukf.heuristic.VariableHeuristic;
import sk.ukf.model.CSPProblem;
import sk.ukf.model.Constraint;
import sk.ukf.model.Variable;

import java.util.*;

public class ForwardCheckingSolver implements Solver {

    private long recursiveCalls;
    private long backtracks;

    private final VariableHeuristic variableHeuristic;
    private final ValueHeuristic valueHeuristic;

    public ForwardCheckingSolver() {
        this.variableHeuristic = new FirstUnassignedHeuristic();
        this.valueHeuristic = new DefaultValueHeuristic();
    }

    public ForwardCheckingSolver(VariableHeuristic variableHeuristic) {
        this.variableHeuristic = variableHeuristic;
        this.valueHeuristic = new DefaultValueHeuristic();
    }
    public ForwardCheckingSolver(ValueHeuristic valueHeuristic) {
        this.valueHeuristic = valueHeuristic;
        this.variableHeuristic = new FirstUnassignedHeuristic();
    }

    public ForwardCheckingSolver(VariableHeuristic variableHeuristic,
                                 ValueHeuristic valueHeuristic) {
        this.variableHeuristic = variableHeuristic;
        this.valueHeuristic = valueHeuristic;
    }

    @Override
    public Solution solve(CSPProblem problem) {
        recursiveCalls = 0;
        backtracks = 0;

        long start = System.currentTimeMillis();

        SearchState initialState = createInitialState(problem);
        boolean solved = forwardCheckBacktrack(problem, initialState);

        long end = System.currentTimeMillis();

        return new Solution(
                new HashMap<>(initialState.getAssignment()),
                end - start,
                recursiveCalls,
                backtracks,
                solved
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

    private boolean forwardCheckBacktrack(CSPProblem problem, SearchState state) {
        recursiveCalls++;

        if (state.getAssignment().size() == problem.getVariables().size()) {
            return allConstraintsSatisfied(problem, state.getAssignment());
        }

        Variable variable = variableHeuristic.selectVariable(problem, state.getAssignment());
        List<Integer> orderedValues =
                valueHeuristic.orderValues(variable, problem, state.getAssignment());

        for (Integer value : orderedValues) {
            if (!state.getCurrentDomains().get(variable).contains(value)) {
                continue;
            }

            SearchState nextState = state.deepCopy();
            nextState.getAssignment().put(variable, value);

            Set<Integer> singleton = new HashSet<>();
            singleton.add(value);
            nextState.getCurrentDomains().put(variable, singleton);

            if (!isConsistent(problem, nextState.getAssignment())) {
                backtracks++;
                continue;
            }

            boolean domainsOk = applyForwardChecking(problem, nextState, variable);

            if (!domainsOk) {
                backtracks++;
                continue;
            }

            if (forwardCheckBacktrack(problem, nextState)) {
                state.getAssignment().clear();
                state.getAssignment().putAll(nextState.getAssignment());
                return true;
            }

            backtracks++;
        }

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

    private boolean allConstraintsSatisfied(CSPProblem problem, Map<Variable, Integer> assignment) {
        for (Constraint constraint : problem.getConstraints()) {
            if (!constraint.isSatisfied(assignment)) {
                return false;
            }
        }
        return true;
    }
}
