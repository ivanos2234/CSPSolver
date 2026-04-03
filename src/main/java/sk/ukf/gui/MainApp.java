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
import sk.ukf.solver.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class MainApp extends Application {

    private final ComboBox<String> solverCombo = new ComboBox<>();
    private final ComboBox<String> variableHeuristicCombo = new ComboBox<>();
    private final ComboBox<String> valueHeuristicCombo = new ComboBox<>();
    private final ComboBox<Integer> baseCombo = new ComboBox<>();

    private final Label sendLettersLabel = new Label("  SEND");
    private final Label moreLettersLabel = new Label("+ MORE");
    private final Label lineLabel = new Label("------");
    private final Label moneyLettersLabel = new Label(" MONEY");

    private final Label sendNumbersLabel = new Label("  ----");
    private final Label moreNumbersLabel = new Label("+ ----");
    private final Label moneyNumbersLabel = new Label(" -----");

    private final Label statsLabel = new Label("No result yet.");
    private final TextArea traceArea = new TextArea();
    private Label heuristicsInfoLabel;

    private Scene mainScene;
    private Scene comparisonScene;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        stage.setTitle("CSP Cryptarithm Solver");

        solverCombo.getItems().addAll(
                "Plain Backtracking",
                "Forward Checking",
                "AC3-like",
                "Jacop"
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

        heuristicsInfoLabel = new Label("");
        heuristicsInfoLabel.setWrapText(true);

        solverCombo.setOnAction(e -> updateHeuristicAvailability());
        updateHeuristicAvailability();

        ArrayList<Integer> values = new ArrayList();
        for (int i = 8; i <= 28; i++) {
            values.add(i);
        }
        baseCombo.getItems().addAll(values);
        baseCombo.setValue(10);

        Button solveButton = new Button("Solve");
        solveButton.setOnAction(e -> solveProblem());

        Button clearButton = new Button("Clear");
        clearButton.setOnAction(e -> clearResult());

        Button comparisonButton = new Button("Comparison");
        comparisonButton.setOnAction(e -> showComparisonScene());

        HBox controls = new HBox(12,
                new Label("Solver:"), solverCombo,
                new Label("Variable heuristic:"), variableHeuristicCombo,
                new Label("Value heuristic:"), valueHeuristicCombo,
                new Label("Base:"), baseCombo,
                solveButton,
                clearButton,
                comparisonButton
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

        VBox topBox = new VBox(5, controls, heuristicsInfoLabel);
        root.setTop(topBox);

        mainScene = new Scene(root, 950, 600);
        stage.setScene(mainScene);
        stage.show();
    }

    private void updateHeuristicAvailability() {
        String selectedSolver = solverCombo.getValue();

        if (selectedSolver.equals("Jacop")) {
            variableHeuristicCombo.setDisable(true);
            valueHeuristicCombo.setDisable(true);

            heuristicsInfoLabel.setText(
                    "For JaCoP, custom MRV/LCV settings are ignored. JaCoP uses its own internal search strategy."
            );
        } else {
            variableHeuristicCombo.setDisable(false);
            valueHeuristicCombo.setDisable(false);

            heuristicsInfoLabel.setText("");
        }
    }

    private void solveProblem() {
        try {
            int base = baseCombo.getValue();
            CSPProblem problem = SendMoreMoneyFactory.create(base);

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
            stats += "Base: " + base + "\n";
            stats += "Time (ms): " + solution.getTimeMillis() + "\n";

            String selectedSolver = solverCombo.getValue();

            if (selectedSolver.equals("Jacop")) {
                stats += "Nodes: " + solution.getJacopNodes() + "\n";
                stats += "Decisions: " + solution.getJacopDecisions() + "\n";
                stats += "Wrong decisions: " + solution.getJacopWrongDecisions() + "\n";
                stats += "Backtracks: " + solution.getBacktracks() + "\n";
                stats += "Maximum depth: " + solution.getJacopMaximumDepth() + "\n";
            } else {
                stats += "Recursive calls: " + solution.getRecursiveCalls() + "\n";
                stats += "Backtracks: " + solution.getBacktracks() + "\n";
                stats += "Failed branches: " + solution.getFailedBranches() + "\n";
            }

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
            case "Jacop" -> new JacopSolver(baseCombo.getValue());
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
            String letter = String.valueOf(ch);
            Integer value = assignment.get(letter);
            res += digitToSymbol(value);

        }
        return res;
    }

    private String digitToSymbol(Integer value) {
        if (value == null) {
            return "-";
        }

        if (value >= 0 && value <= 9) {
            return String.valueOf(value);
        }

        char letter = (char) ('A' + (value - 10));
        return String.valueOf(letter);
    }

    private void showComparisonScene() {
        Button addCardButton = new Button("Add Card");
        Button backButton = new Button("Back");

        HBox cardContainer = new HBox(15);
        cardContainer.setPadding(new Insets(15));

        ScrollPane scrollPane = new ScrollPane(cardContainer);
        scrollPane.setFitToHeight(true);
        scrollPane.setPannable(true);

        addCardButton.setOnAction(e -> {
            VBox card = createComparisonCard(cardContainer);
            cardContainer.getChildren().add(card);
        });

        backButton.setOnAction(e -> primaryStage.setScene(mainScene));

        HBox topBar = new HBox(10, addCardButton, backButton);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER_LEFT);

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(scrollPane);

        comparisonScene = new Scene(root, 1400, 750);
        primaryStage.setScene(comparisonScene);

        cardContainer.getChildren().add(createComparisonCard(cardContainer));
    }

    private VBox createComparisonCard(HBox cardContainer) {
        ComboBox<Integer> localBaseCombo = new ComboBox<>();

        ArrayList<Integer> values = new ArrayList<>();
        for (int i = 8; i <= 28; i++) {
            values.add(i);
        }
        localBaseCombo.getItems().addAll(values);
        localBaseCombo.setValue(10);
        localBaseCombo.setPrefWidth(90);

        ComboBox<String> localSolverCombo = new ComboBox<>();
        localSolverCombo.getItems().addAll(
                "Plain Backtracking",
                "Forward Checking",
                "AC3-like",
                "JaCoP"
        );
        localSolverCombo.setValue("Plain Backtracking");
        localSolverCombo.setPrefWidth(170);

        ComboBox<String> localVariableHeuristicCombo = new ComboBox<>();
        localVariableHeuristicCombo.getItems().addAll("None", "MRV");
        localVariableHeuristicCombo.setValue("None");
        localVariableHeuristicCombo.setPrefWidth(140);

        ComboBox<String> localValueHeuristicCombo = new ComboBox<>();
        localValueHeuristicCombo.getItems().addAll("None", "LCV");
        localValueHeuristicCombo.setValue("None");
        localValueHeuristicCombo.setPrefWidth(140);

        Label infoLabel = new Label("");
        infoLabel.setWrapText(true);
        infoLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666666;");

        Label statsLabel = new Label("No result yet.");
        statsLabel.setWrapText(true);
        statsLabel.setStyle("-fx-font-size: 12px;");

        TextArea pathArea = new TextArea();
        pathArea.setEditable(false);
        pathArea.setPrefRowCount(6);

        Button runButton = new Button("Run");
        Button clearButton = new Button("Clear");
        Button removeButton = new Button("Remove");

        localSolverCombo.setOnAction(e -> {
            if (localSolverCombo.getValue().equals("JaCoP")) {
                localVariableHeuristicCombo.setValue("None");
                localValueHeuristicCombo.setValue("None");
                localVariableHeuristicCombo.setDisable(true);
                localValueHeuristicCombo.setDisable(true);
                infoLabel.setText("JaCoP uses its own internal strategy.");
            } else {
                localVariableHeuristicCombo.setDisable(false);
                localValueHeuristicCombo.setDisable(false);
                infoLabel.setText("");
            }
        });

        HBox row1 = new HBox(8, new Label("Base:"), localBaseCombo);
        HBox row2 = new HBox(8, new Label("Solver:"), localSolverCombo);
        HBox row3 = new HBox(8, new Label("Variable heuristic:"), localVariableHeuristicCombo);
        HBox row4 = new HBox(8, new Label("Value heuristic:"), localValueHeuristicCombo);

        row1.setAlignment(Pos.CENTER_LEFT);
        row2.setAlignment(Pos.CENTER_LEFT);
        row3.setAlignment(Pos.CENTER_LEFT);
        row4.setAlignment(Pos.CENTER_LEFT);

        HBox buttonBar = new HBox(6, runButton, clearButton, removeButton);
        buttonBar.setAlignment(Pos.CENTER_LEFT);

        Label sendLettersLabel = new Label("  SEND");
        Label moreLettersLabel = new Label("+ MORE");
        Label lineLabel = new Label("------");
        Label moneyLettersLabel = new Label(" MONEY");

        Label sendNumbersLabel = new Label("  ----");
        Label moreNumbersLabel = new Label("+ ----");
        Label moneyNumbersLabel = new Label(" -----");

        Font equationFont = Font.font("Consolas", 18);

        sendLettersLabel.setFont(equationFont);
        moreLettersLabel.setFont(equationFont);
        lineLabel.setFont(equationFont);
        moneyLettersLabel.setFont(equationFont);

        sendNumbersLabel.setFont(equationFont);
        moreNumbersLabel.setFont(equationFont);
        moneyNumbersLabel.setFont(equationFont);

        Label secondLineLabel = new Label("------");
        secondLineLabel.setFont(equationFont);

        VBox LettersEquation = new VBox(
                sendLettersLabel,
                moreLettersLabel,
                lineLabel,
                moneyLettersLabel
        );

        VBox NumbersEquation = new VBox(
                sendNumbersLabel,
                moreNumbersLabel,
                secondLineLabel,
                moneyNumbersLabel
        );

        HBox equationBox = new HBox(
                20,
                LettersEquation,
                NumbersEquation
        );
        equationBox.setAlignment(Pos.CENTER_LEFT);

        VBox resultBox = new VBox(4, new Label("Result"), equationBox);
        resultBox.setPadding(new Insets(10));
        resultBox.setStyle(
                "-fx-background-color: #f7f7f7;" +
                        "-fx-background-radius: 6;"
        );

        VBox statsBox = new VBox(4, new Label("Statistics"), statsLabel);
        statsBox.setPadding(new Insets(10));
        statsBox.setStyle(
                "-fx-background-color: #fafafa;" +
                        "-fx-background-radius: 6;"
        );

        VBox pathBox = new VBox(4, new Label("Solution path"), pathArea);

        VBox card = new VBox(
                10,
                row1,
                row2,
                row3,
                row4,
                infoLabel,
                buttonBar,
                resultBox,
                statsBox,
                pathBox
        );

        card.setPadding(new Insets(14));
        card.setPrefWidth(320);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #d9d9d9;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;"
        );

        runButton.setOnAction(e -> {
            int base = localBaseCombo.getValue();

            StateVariableHeuristic variableHeuristic;
            StateValueHeuristic valueHeuristic;

            if (localVariableHeuristicCombo.getValue().equals("MRV")) {
                variableHeuristic = new StateMRVHeuristic();
            } else {
                variableHeuristic = new StateFirstUnassignedHeuristic();
            }

            if (localValueHeuristicCombo.getValue().equals("LCV")) {
                valueHeuristic = new StateLCVHeuristic();
            } else {
                valueHeuristic = new StateDefaultValueHeuristic();
            }

            Solver solver;

            if (localSolverCombo.getValue().equals("Forward Checking")) {
                solver = new ForwardCheckingSolver(variableHeuristic, valueHeuristic);
            } else if (localSolverCombo.getValue().equals("AC3-like")) {
                solver = new AC3LikeSolver(variableHeuristic, valueHeuristic);
            } else if (localSolverCombo.getValue().equals("JaCoP")) {
                solver = new JacopSolver(base);
            } else {
                solver = new BacktrackingSolver(variableHeuristic, valueHeuristic);
            }

            Solution solution = solver.solve(SendMoreMoneyFactory.create(base));

            if (!solution.isSolved() || solution.getAssignment() == null) {
                sendNumbersLabel.setText("  ----");
                moreNumbersLabel.setText("+ ----");
                moneyNumbersLabel.setText(" -----");
                statsLabel.setText("No solution found.");
                pathArea.clear();
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
            stats += "Base: " + base + "\n";
            stats += "Time: " + solution.getTimeMillis() + " ms\n";

            if (localSolverCombo.getValue().equals("JaCoP")) {
                stats += "Nodes: " + solution.getJacopNodes() + "\n";
                stats += "Decisions: " + solution.getJacopDecisions() + "\n";
                stats += "Wrong decisions: " + solution.getJacopWrongDecisions() + "\n";
                stats += "Backtracks: " + solution.getBacktracks() + "\n";
                stats += "Max depth: " + solution.getJacopMaximumDepth() + "\n";
            } else {
                stats += "Recursive calls: " + solution.getRecursiveCalls() + "\n";
                stats += "Backtracks: " + solution.getBacktracks() + "\n";
                stats += "Failed branches: " + solution.getFailedBranches() + "\n";
            }

            statsLabel.setText(stats);

            pathArea.clear();
            if (solution.getSolutionPath() != null) {
                int step = 1;
                for (String item : solution.getSolutionPath()) {
                    pathArea.appendText(step + ". " + item + "\n");
                    step++;
                }
            }
        });

        clearButton.setOnAction(e -> {
            sendNumbersLabel.setText("  ----");
            moreNumbersLabel.setText("+ ----");
            moneyNumbersLabel.setText(" -----");
            statsLabel.setText("No result yet.");
            pathArea.clear();
        });

        removeButton.setOnAction(e -> cardContainer.getChildren().remove(card));

        return card;
    }

}