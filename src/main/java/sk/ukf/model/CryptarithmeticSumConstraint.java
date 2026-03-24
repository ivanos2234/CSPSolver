package sk.ukf.model;

import java.util.Map;

public class CryptarithmeticSumConstraint implements Constraint {

    private final Variable s;
    private final Variable e;
    private final Variable n;
    private final Variable d;
    private final Variable m;
    private final Variable o;
    private final Variable r;
    private final Variable y;

    public CryptarithmeticSumConstraint(
            Variable s, Variable e, Variable n, Variable d,
            Variable m, Variable o, Variable r, Variable y) {
        this.s = s;
        this.e = e;
        this.n = n;
        this.d = d;
        this.m = m;
        this.o = o;
        this.r = r;
        this.y = y;
    }

    @Override
    public boolean isSatisfied(Map<Variable, Integer> assignment) {
        if (!assignment.containsKey(s) || !assignment.containsKey(e) ||
                !assignment.containsKey(n) || !assignment.containsKey(d) ||
                !assignment.containsKey(m) || !assignment.containsKey(o) ||
                !assignment.containsKey(r) || !assignment.containsKey(y)) {
            return true;
        }

        int send = 1000 * assignment.get(s)
                + 100 * assignment.get(e)
                + 10 * assignment.get(n)
                + assignment.get(d);

        int more = 1000 * assignment.get(m)
                + 100 * assignment.get(o)
                + 10 * assignment.get(r)
                + assignment.get(e);

        int money = 10000 * assignment.get(m)
                + 1000 * assignment.get(o)
                + 100 * assignment.get(n)
                + 10 * assignment.get(e)
                + assignment.get(y);

        return send + more == money;
    }
}