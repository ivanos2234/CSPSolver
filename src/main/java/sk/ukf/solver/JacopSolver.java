package sk.ukf.solver;

import org.jacop.constraints.Alldifferent;
import org.jacop.constraints.LinearInt;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.search.*;
import sk.ukf.model.Variable;

import java.util.*;

public class JacopSolver implements Solver {

    @Override
    public Solution solve(sk.ukf.model.CSPProblem problem) {
        long start = System.currentTimeMillis();

        Store store = new Store();

        // Písmená
        IntVar S = new IntVar(store, "S", 1, 9);
        IntVar E = new IntVar(store, "E", 0, 9);
        IntVar N = new IntVar(store, "N", 0, 9);
        IntVar D = new IntVar(store, "D", 0, 9);
        IntVar M = new IntVar(store, "M", 1, 9);
        IntVar O = new IntVar(store, "O", 0, 9);
        IntVar R = new IntVar(store, "R", 0, 9);
        IntVar Y = new IntVar(store, "Y", 0, 9);

        IntVar[] letters = {S, E, N, D, M, O, R, Y};

        // AllDifferent
        store.impose(new Alldifferent(letters));

        // SEND = 1000*S + 100*E + 10*N + D
        IntVar SEND = new IntVar(store, "SEND", 0, 9999);
        store.impose(new LinearInt(
                new IntVar[]{S, E, N, D, SEND},
                new int[]{1000, 100, 10, 1, -1},
                "=",
                0
        ));

        // MORE = 1000*M + 100*O + 10*R + E
        IntVar MORE = new IntVar(store, "MORE", 0, 9999);
        store.impose(new LinearInt(
                new IntVar[]{M, O, R, E, MORE},
                new int[]{1000, 100, 10, 1, -1},
                "=",
                0
        ));

        // MONEY = 10000*M + 1000*O + 100*N + 10*E + Y
        IntVar MONEY = new IntVar(store, "MONEY", 0, 99999);
        store.impose(new LinearInt(
                new IntVar[]{M, O, N, E, Y, MONEY},
                new int[]{10000, 1000, 100, 10, 1, -1},
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
                    0,
                    false,
                    new ArrayList<>(),
                    0
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
                0,
                true,
                solutionPath,
                0
        );
    }
}
