package sk.ukf.model;

import java.util.*;

public class SendMoreMoneyFactory {

    public static CSPProblem create() {

        Variable s = new Variable("S", new HashSet<>(Arrays.asList(1,2,3,4,5,6,7,8,9)));
        Variable e = new Variable("E", new HashSet<>(Arrays.asList(0,1,2,3,4,5,6,7,8,9)));
        Variable n = new Variable("N", new HashSet<>(Arrays.asList(0,1,2,3,4,5,6,7,8,9)));
        Variable d = new Variable("D", new HashSet<>(Arrays.asList(0,1,2,3,4,5,6,7,8,9)));
        Variable m = new Variable("M", new HashSet<>(Arrays.asList(1,2,3,4,5,6,7,8,9)));
        Variable o = new Variable("O", new HashSet<>(Arrays.asList(0,1,2,3,4,5,6,7,8,9)));
        Variable r = new Variable("R", new HashSet<>(Arrays.asList(0,1,2,3,4,5,6,7,8,9)));
        Variable y = new Variable("Y", new HashSet<>(Arrays.asList(0,1,2,3,4,5,6,7,8,9)));

        Variable c0 = new Variable("C0", new HashSet<>(Arrays.asList(0)));
        Variable c1 = new Variable("C1", new HashSet<>(Arrays.asList(0,1)));
        Variable c2 = new Variable("C2", new HashSet<>(Arrays.asList(0,1)));
        Variable c3 = new Variable("C3", new HashSet<>(Arrays.asList(0,1)));
        Variable c4 = new Variable("C4", new HashSet<>(Arrays.asList(0,1)));

        List<Variable> variables = Arrays.asList(
                s, e, n, d, m, o, r, y,
                c0, c1, c2, c3, c4
        );

        List<Constraint> constraints = new ArrayList<>();

        constraints.add(new AllDifferentConstraint(Arrays.asList(s, e, n, d, m, o, r, y)));

        constraints.add(new ColumnSumConstraint(d, e, c0, y, c1));
        constraints.add(new ColumnSumConstraint(n, r, c1, e, c2));
        constraints.add(new ColumnSumConstraint(e, o, c2, n, c3));
        constraints.add(new ColumnSumConstraint(s, m, c3, o, c4));

        constraints.add(new EqualityConstraint(c4, m));

        return new CSPProblem(variables, constraints);
    }
}