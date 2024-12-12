package ContextFreeGrammar;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import project.automatatheoryproject.StartProgram;

import java.io.IOException;

public class CFG_Controller {
    @FXML
    private Label transitionLabel;
    @FXML
    private TextField stringField;
    @FXML
    private TextArea transitionLogCFG;
    @FXML
    private Button simulateButton, resetButton;

    private StringBuilder palindrome = new StringBuilder();

    //to go back to main page
    @FXML
    protected void backToMainPage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartProgram.class.getResource("MainPage.fxml"));
        Scene turingMachine = new Scene(fxmlLoader.load(), 800, 701);

        Stage currentStage = (Stage) transitionLabel.getScene().getWindow();
        currentStage.setTitle("Main Page");
        currentStage.setScene(turingMachine);
        currentStage.setResizable(false);
        currentStage.centerOnScreen();
        currentStage.show();
    }

    @FXML
    protected void simulateButtonClicked() {
        String inputString = stringField.getText().trim();
        transitionLogCFG.appendText(performCFG(inputString));
    }

    @FXML
    protected void resetButtonClicked() {
        stringField.clear();
        transitionLogCFG.clear();
    }

    // Method to simulate the production of the palindrome
    public String performCFG(String w) {
        if (!isInputInGrammar(w)) {
            return "Rejected: Only strings of 'a' and 'b' are allowed.";
        }

        // Constructing the full string based on the input w
        String constructedString = w + "c" + new StringBuilder(w).reverse().toString();

        // Start the derivation process
        palindrome.setLength(0);
        grammarProduction(w);

        String result = palindrome.toString();
        result += "\nFinal String: " + constructedString;
        result += "\n\nThe Input is Accepted.";

        return result;
    }

    //to palidrome the user input
    private void grammarProduction(String w) {
        StringBuilder current = new StringBuilder("S");
        palindrome.append("Production Rule: S → S").append("\n");
        palindrome.append(current+"\n");

        StringBuilder leftSide = new StringBuilder();

        for (char ch : w.toCharArray()) {
            if (ch == 'a') {
                current = new StringBuilder(current.toString().replaceFirst("S", "aSa"));
                leftSide.append("a");
                palindrome.append("Production Rule: S → aSa\n");
            } else if (ch == 'b') {
                current = new StringBuilder(current.toString().replaceFirst("S", "bSb"));
                palindrome.append("Production Rule: S → bSb\n");
                leftSide.append("b");
            }
            palindrome.append(current).append("\n");
        }
        palindrome.append("Production Rule: S → c\n");
        String constructedPalindrome = leftSide + "c" + leftSide.reverse();
        palindrome.append(constructedPalindrome).append("\n");
    }


    //method to validate if user inputs are only a and b
    private boolean isInputInGrammar(String str) {
        return str.matches("[ab]+");
    }
}
