package sk.ukf.model;

import java.util.Map;

public class ColumnSumConstraint implements Constraint {

    private final Variable left1;
    private final Variable left2;
    private final Variable carryIn;
    private final Variable result;
    private final Variable carryOut;

    public ColumnSumConstraint(Variable left1,
                               Variable left2,
                               Variable carryIn,
                               Variable result,
                               Variable carryOut) {
        this.left1 = left1;
        this.left2 = left2;
        this.carryIn = carryIn;
        this.result = result;
        this.carryOut = carryOut;
    }

    @Override
    public boolean isSatisfied(Map<Variable, Integer> assignment) {
        if (!assignment.containsKey(left1) ||
                !assignment.containsKey(left2) ||
                !assignment.containsKey(carryIn) ||
                !assignment.containsKey(result) ||
                !assignment.containsKey(carryOut)) {
            return true;
        }

        int a = assignment.get(left1);
        int b = assignment.get(left2);
        int cin = assignment.get(carryIn);
        int r = assignment.get(result);
        int cout = assignment.get(carryOut);

        return a + b + cin == r + 10 * cout;
    }
}
