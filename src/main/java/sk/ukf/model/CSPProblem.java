package sk.ukf.model;

import java.util.*;

public class CSPProblem {

    private final List<Variable> variables;
    private final List<Constraint> constraints;

    public CSPProblem(List<Variable> variables, List<Constraint> constraints) {
        this.variables = variables;
        this.constraints = constraints;
    }

    public List<Variable> getVariables() { return variables; }
    public List<Constraint> getConstraints() { return constraints; }
}
