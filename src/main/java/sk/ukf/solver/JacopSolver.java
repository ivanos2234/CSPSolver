package sk.ukf.solver;

import org.jacop.constraints.Alldifferent;
import org.jacop.constraints.LinearInt;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.search.*;
import sk.ukf.model.Variable;

import java.util.*;

public class JacopSolver implements Solver {

    private final int base;

    public JacopSolver(int base) {
        this.base = base;
    }

    @Override
    public Solution solve(sk.ukf.model.CSPProblem problem) {
        long start = System.currentTimeMillis();

        Store store = new Store();

        int b1 = base;
        int b2 = base * base;
        int b3 = base * base * base;
        int b4 = base * base * base * base;

        // Písmená
        IntVar S = new IntVar(store, "S", 1, base - 1);
        IntVar E = new IntVar(store, "E", 0, base - 1);
        IntVar N = new IntVar(store, "N", 0, base - 1);
        IntVar D = new IntVar(store, "D", 0, base - 1);
        IntVar M = new IntVar(store, "M", 1, base - 1);
        IntVar O = new IntVar(store, "O", 0, base - 1);
        IntVar R = new IntVar(store, "R", 0, base - 1);
        IntVar Y = new IntVar(store, "Y", 0, base - 1);

        IntVar[] letters = {S, E, N, D, M, O, R, Y};

        // AllDifferent
        store.impose(new Alldifferent(letters));

        // SEND = base^3*S + base^2*E + base*N + D
        IntVar SEND = new IntVar(store, "SEND", 0, b4 - 1);
        store.impose(new LinearInt(
                new IntVar[]{S, E, N, D, SEND},
                new int[]{b3, b2, b1, 1, -1},
                "=",
                0
        ));

        // MORE = base^3*M + base^2*O + base*R + E
        IntVar MORE = new IntVar(store, "MORE", 0, b4 - 1);
        store.impose(new LinearInt(
                new IntVar[]{M, O, R, E, MORE},
                new int[]{b3, b2, b1, 1, -1},
                "=",
                0
        ));

        // MONEY = base^4*M + base^3*O + base^2*N + base*E + Y
        IntVar MONEY = new IntVar(store, "MONEY", 0, b4 * base - 1);
        store.impose(new LinearInt(
                new IntVar[]{M, O, N, E, Y, MONEY},
                new int[]{b4, b3, b2, b1, 1, -1},
                "=",
                0
        ));

        // SEND + MORE = MONEY
        store.impose(new LinearInt(
                new IntVar[]{SEND, MORE, MONEY},
                new int[]{1, 1, -1},
                "=",
                0
        ));

        Search<IntVar> search = new DepthFirstSearch<>();
        SelectChoicePoint<IntVar> select =
                new InputOrderSelect<>(store, letters, new IndomainMin<>());

        boolean solved = search.labeling(store, select);

        long end = System.currentTimeMillis();

        if (!solved) {
            return new Solution(
                    new HashMap<>(),
                    end - start,
                    0,
                    search.getBacktracks(),
                    false,
                    new ArrayList<>(),
                    search.getWrongDecisions(),
                    search.getNodes(),
                    search.getDecisions(),
                    search.getWrongDecisions(),
                    search.getMaximumDepth()
            );
        }

        Map<Variable, Integer> assignment = new HashMap<>();
        assignment.put(new Variable("S", new HashSet<>()), S.value());
        assignment.put(new Variable("E", new HashSet<>()), E.value());
        assignment.put(new Variable("N", new HashSet<>()), N.value());
        assignment.put(new Variable("D", new HashSet<>()), D.value());
        assignment.put(new Variable("M", new HashSet<>()), M.value());
        assignment.put(new Variable("O", new HashSet<>()), O.value());
        assignment.put(new Variable("R", new HashSet<>()), R.value());
        assignment.put(new Variable("Y", new HashSet<>()), Y.value());

        List<String> solutionPath = new ArrayList<>();
        solutionPath.add("S = " + S.value());
        solutionPath.add("E = " + E.value());
        solutionPath.add("N = " + N.value());
        solutionPath.add("D = " + D.value());
        solutionPath.add("M = " + M.value());
        solutionPath.add("O = " + O.value());
        solutionPath.add("R = " + R.value());
        solutionPath.add("Y = " + Y.value());

        return new Solution(
                assignment,
                end - start,
                0,
                search.getBacktracks(),
                true,
                solutionPath,
                search.getWrongDecisions(),
                search.getNodes(),
                search.getDecisions(),
                search.getWrongDecisions(),
                search.getMaximumDepth()
        );
    }
}
