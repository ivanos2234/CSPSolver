package sk.ukf.gui;

import sk.ukf.heuristic.StateDefaultValueHeuristic;
import sk.ukf.heuristic.StateFirstUnassignedHeuristic;
import sk.ukf.heuristic.StateLCVHeuristic;
import sk.ukf.heuristic.StateMRVHeuristic;
import sk.ukf.heuristic.StateValueHeuristic;
import sk.ukf.heuristic.StateVariableHeuristic;
import sk.ukf.model.CSPProblem;
import sk.ukf.model.SendMoreMoneyFactory;
import sk.ukf.model.Variable;
import sk.ukf.solver.AC3LikeSolver;
import sk.ukf.solver.BacktrackingSolver;
import sk.ukf.solver.ForwardCheckingSolver;
import sk.ukf.solver.Solution;
import sk.ukf.solver.Solver;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Map;
import java.util.TreeMap;

public class MainApp extends Application {

    private final ComboBox<String> solverCombo = new ComboBox<>();
    private final ComboBox<String> variableHeuristicCombo = new ComboBox<>();
    private final ComboBox<String> valueHeuristicCombo = new ComboBox<>();

    private final Label sendLettersLabel = new Label("  SEND");
    private final Label moreLettersLabel = new Label("+ MORE");
    private final Label lineLabel = new Label("------");
    private final Label moneyLettersLabel = new Label(" MONEY");

    private final Label sendNumbersLabel = new Label("  ----");
    private final Label moreNumbersLabel = new Label("+ ----");
    private final Label moneyNumbersLabel = new Label(" -----");

    private final Label statsLabel = new Label("No result yet.");
    private final TextArea traceArea = new TextArea();

    @Override
    public void start(Stage stage) {
        stage.setTitle("CSP Cryptarithm Solver");

        solverCombo.getItems().addAll(
                "Plain Backtracking",
                "Forward Checking",
                "AC3-like"
        );
        solverCombo.setValue("Plain Backtracking");

        variableHeuristicCombo.getItems().addAll(
                "None",
                "MRV"
        );
        variableHeuristicCombo.setValue("None");

        valueHeuristicCombo.getItems().addAll(
                "None",
                "LCV"
        );
        valueHeuristicCombo.setValue("None");

        Button solveButton = new Button("Solve");
        solveButton.setOnAction(e -> solveProblem());

        Button clearButton = new Button("Clear");
        clearButton.setOnAction(e -> clearResult());

        HBox controls = new HBox(12,
                new Label("Solver:"), solverCombo,
                new Label("Variable heuristic:"), variableHeuristicCombo,
                new Label("Value heuristic:"), valueHeuristicCombo,
                solveButton,
                clearButton
        );
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setPadding(new Insets(10));

        Font equationFont = Font.font("Consolas", 22);
        sendLettersLabel.setFont(equationFont);
        moreLettersLabel.setFont(equationFont);
        lineLabel.setFont(equationFont);
        moneyLettersLabel.setFont(equationFont);

        sendNumbersLabel.setFont(equationFont);
        moreNumbersLabel.setFont(equationFont);
        moneyNumbersLabel.setFont(equationFont);

        Label secondLineLabel = new Label("------");
        secondLineLabel.setFont(Font.font("Consolas", 22));

        VBox equationBox = new VBox(5,
                sendLettersLabel,
                moreLettersLabel,
                lineLabel,
                moneyLettersLabel,
                new Separator(),
                sendNumbersLabel,
                moreNumbersLabel,
                secondLineLabel,
                moneyNumbersLabel
        );
        equationBox.setPadding(new Insets(15));
        equationBox.setAlignment(Pos.CENTER_LEFT);

        statsLabel.setWrapText(true);
        statsLabel.setPadding(new Insets(10));

        traceArea.setEditable(false);
        traceArea.setPrefRowCount(14);
        traceArea.setPromptText("Solution path / assignment order...");

        VBox rightPanel = new VBox(10,
                new Label("Statistics"),
                statsLabel,
                new Label("Assignment order"),
                traceArea
        );
        rightPanel.setPadding(new Insets(10));
        rightPanel.setPrefWidth(350);

        BorderPane root = new BorderPane();
        root.setTop(controls);
        root.setCenter(equationBox);
        root.setRight(rightPanel);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 950, 550);
        stage.setScene(scene);
        stage.show();
    }

    private void solveProblem() {
        try {
            CSPProblem problem = SendMoreMoneyFactory.create();

            StateVariableHeuristic variableHeuristic = createVariableHeuristic();
            StateValueHeuristic valueHeuristic = createValueHeuristic();
            Solver solver = createSolver(variableHeuristic, valueHeuristic);

            Solution solution = solver.solve(problem);

            if (!solution.isSolved() || solution.getAssignment() == null) {
                statsLabel.setText("No solution found.");
                sendNumbersLabel.setText("  ----");
                moreNumbersLabel.setText("+ ----");
                moneyNumbersLabel.setText(" -----");
                traceArea.clear();
                return;
            }

            Map<String, Integer> assignment = toNameMap(solution.getAssignment());

            String send = buildWord("SEND", assignment);
            String more = buildWord("MORE", assignment);
            String money = buildWord("MONEY", assignment);

            sendNumbersLabel.setText("  " + send);
            moreNumbersLabel.setText("+ " + more);
            moneyNumbersLabel.setText(" " + money);

            String stats = "";
            stats += "Solved: " + solution.isSolved() + "\n";
            stats += "Time (ms): " + solution.getTimeMillis() + "\n";
            stats += "Recursive calls: " + solution.getRecursiveCalls() + "\n";
            stats += "Backtracks: " + solution.getBacktracks() + "\n";
            stats += "Failed branches: " + solution.getFailedBranches() + "\n";
            stats += "Equation: " + send + " + " + more + " = " + money;

            statsLabel.setText(stats);

            traceArea.clear();

            if (solution.getSolutionPath() != null && !solution.getSolutionPath().isEmpty()) {
                int step = 1;

                for (String item : solution.getSolutionPath()) {
                    traceArea.appendText(step + ". " + item + "\n");
                    step++;
                }
            } else {
                traceArea.setText("No solution path available.");
            }

        } catch (Exception e) {
            statsLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearResult() {
        sendNumbersLabel.setText("  ----");
        moreNumbersLabel.setText("+ ----");
        moneyNumbersLabel.setText(" -----");

        statsLabel.setText("No result yet.");
        traceArea.clear();
    }

    private Solver createSolver(StateVariableHeuristic variableHeuristic,
                                StateValueHeuristic valueHeuristic) {
        String solverName = solverCombo.getValue();

        return switch (solverName) {
            case "Forward Checking" -> new ForwardCheckingSolver(variableHeuristic, valueHeuristic);
            case "AC3-like" -> new AC3LikeSolver(variableHeuristic, valueHeuristic);
            default -> new BacktrackingSolver(variableHeuristic, valueHeuristic);
        };
    }

    private StateVariableHeuristic createVariableHeuristic() {
        return switch (variableHeuristicCombo.getValue()) {
            case "MRV" -> new StateMRVHeuristic();
            default -> new StateFirstUnassignedHeuristic();
        };
    }

    private StateValueHeuristic createValueHeuristic() {
        return switch (valueHeuristicCombo.getValue()) {
            case "LCV" -> new StateLCVHeuristic();
            default -> new StateDefaultValueHeuristic();
        };
    }

    private Map<String, Integer> toNameMap(Map<Variable, Integer> rawAssignment) {
        Map<String, Integer> result = new TreeMap<>();
        for (Map.Entry<Variable, Integer> entry : rawAssignment.entrySet()) {
            result.put(entry.getKey().getName(), entry.getValue());
        }
        return result;
    }

    private String buildWord(String word, Map<String, Integer> assignment) {
        String res = "";
        for (char ch : word.toCharArray()) {
            Integer value = assignment.get(String.valueOf(ch));
            if (value != null) {
                res += value;
            } else  {
                res += "-";
            }
        }
        return res;
    }

}