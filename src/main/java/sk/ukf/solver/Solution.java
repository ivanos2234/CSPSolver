package sk.ukf.solver;

import sk.ukf.model.Variable;

import java.util.List;
import java.util.Map;

public class Solution {
    private final Map<Variable, Integer> assignment;
    private final long timeMillis;
    private final long recursiveCalls;
    private final long backtracks;
    private final boolean solved;
    private List<String> solutionPath;
    private long failedBranches;

    public Solution(Map<Variable, Integer> assignment,
                    long timeMillis,
                    long recursiveCalls,
                    long backtracks,
                    boolean solved,
                    List<String> solutionPath,
                    long failedBranches) {
        this.assignment = assignment;
        this.timeMillis = timeMillis;
        this.recursiveCalls = recursiveCalls;
        this.backtracks = backtracks;
        this.solved = solved;
        this.solutionPath = solutionPath;
        this.failedBranches = failedBranches;
    }

    public Map<Variable, Integer> getAssignment() {
        return assignment;
    }

    public long getTimeMillis() {
        return timeMillis;
    }

    public long getRecursiveCalls() {
        return recursiveCalls;
    }

    public long getBacktracks() {
        return backtracks;
    }

    public boolean isSolved() {
        return solved;
    }

    public List<String> getSolutionPath() {
        return solutionPath;
    }

    public long getFailedBranches() {
        return failedBranches;
    }
}
