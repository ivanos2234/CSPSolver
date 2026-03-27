package sk.ukf.model;

import java.util.*;

public class SendMoreMoneyFactory {

    public static CSPProblem create(int base) {

        Variable s = new Variable("S", createDomain(1, base - 1));
        Variable e = new Variable("E", createDomain(0, base - 1));
        Variable n = new Variable("N", createDomain(0, base - 1));
        Variable d = new Variable("D", createDomain(0, base - 1));
        Variable m = new Variable("M", createDomain(1, base - 1));
        Variable o = new Variable("O", createDomain(0, base - 1));
        Variable r = new Variable("R", createDomain(0, base - 1));
        Variable y = new Variable("Y", createDomain(0, base - 1));

        Variable c0 = new Variable("C0", createDomain(0, 0));
        Variable c1 = new Variable("C1", createDomain(0, 1));
        Variable c2 = new Variable("C2", createDomain(0, 1));
        Variable c3 = new Variable("C3", createDomain(0, 1));
        Variable c4 = new Variable("C4", createDomain(0, 1));

        List<Variable> variables = Arrays.asList(
                s, e, n, d, m, o, r, y,
                c0, c1, c2, c3, c4
        );

        List<Constraint> constraints = new ArrayList<>();

        constraints.add(new AllDifferentConstraint(Arrays.asList(s, e, n, d, m, o, r, y)));

        constraints.add(new ColumnSumConstraint(d, e, c0, y, c1, base));
        constraints.add(new ColumnSumConstraint(n, r, c1, e, c2, base));
        constraints.add(new ColumnSumConstraint(e, o, c2, n, c3, base));
        constraints.add(new ColumnSumConstraint(s, m, c3, o, c4, base));

        constraints.add(new EqualityConstraint(c4, m));

        return new CSPProblem(variables, constraints);
    }

    private static Set<Integer> createDomain(int from, int to) {
        Set<Integer> domain = new HashSet<>();

        for (int i = from; i <= to; i++) {
            domain.add(i);
        }

        return domain;
    }
}