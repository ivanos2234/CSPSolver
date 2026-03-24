package sk.ukf.app;


import sk.ukf.model.CSPProblem;
import sk.ukf.model.SendMoreMoneyFactory;

public class Main {
    public static void main(String[] args) {
        CSPProblem problem = SendMoreMoneyFactory.create();
        System.out.println("Variables: " + problem.getVariables().size());
        System.out.println("Constraints: " + problem.getConstraints().size());
    }
}