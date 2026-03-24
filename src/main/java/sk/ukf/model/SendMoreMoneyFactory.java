package sk.ukf.model;

import java.util.*;

public class SendMoreMoneyFactory {

    public static CSPProblem create() {

        // SEND + MORE = MONEY
        // S, M != 0

        Variable s = new Variable("S", new HashSet<>(Arrays.asList(1,2,3,4,5,6,7,8,9)));
        Variable e = new Variable("E", new HashSet<>(Arrays.asList(0,1,2,3,4,5,6,7,8,9)));
        Variable n = new Variable("N", new HashSet<>(Arrays.asList(0,1,2,3,4,5,6,7,8,9)));
        Variable d = new Variable("D", new HashSet<>(Arrays.asList(0,1,2,3,4,5,6,7,8,9)));
        Variable m = new Variable("M", new HashSet<>(Arrays.asList(1,2,3,4,5,6,7,8,9)));
        Variable o = new Variable("O", new HashSet<>(Arrays.asList(0,1,2,3,4,5,6,7,8,9)));
        Variable r = new Variable("R", new HashSet<>(Arrays.asList(0,1,2,3,4,5,6,7,8,9)));
        Variable y = new Variable("Y", new HashSet<>(Arrays.asList(0,1,2,3,4,5,6,7,8,9)));

        List<Variable> variables = Arrays.asList(s, e, n, d, m, o, r, y);

        List<Constraint> constraints = new ArrayList<>();
        constraints.add(new AllDifferentConstraint(variables));
        constraints.add(new CryptarithmeticSumConstraint(s, e, n, d, m, o, r, y));

        return new CSPProblem(variables, constraints);
    }
}