package sk.ukf.solver;

import sk.ukf.model.CSPProblem;

public interface Solver {
    Solution solve(CSPProblem problem);
}
