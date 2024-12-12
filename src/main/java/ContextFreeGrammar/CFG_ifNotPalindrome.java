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

public class CFG_ifNotPalindrome {
    @FXML
    private Label transitionLabel;
    @FXML
    private TextField stringField;
    @FXML
    private TextArea transitionLogCFG;
    @FXML
    private Button simulateButton, resetButton;

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
        String inputString = stringField.getText();
        if(isStringValid(inputString)) {
            transitionLogCFG.appendText("The string input " + inputString + " is accepted by the grammar");
        } else {
            transitionLogCFG.appendText("The string input " + inputString + " is not accepted by the grammar");
        }
    }

    @FXML
    protected void resetButtonClicked() {
        stringField.clear();
        transitionLogCFG.clear();
    }

    //method to verify if string is valid
    private boolean isStringValid(String UserInput) {
        return ruleProductions(UserInput);
    }

    private boolean ruleProductions(String UserInput) {
        // Base case: reject if the input is empty
        if (UserInput.isEmpty()) {
            return false;
        }

        // For production rule S → c
        if (UserInput.length() == 1) {
            transitionLogCFG.appendText(UserInput + "\nS → c\n");
            return UserInput.charAt(0) == 'c' || UserInput.charAt(0) == 'C';
        }

        // For production rule S → aSa
        if ((UserInput.charAt(0) == 'a' && UserInput.charAt(UserInput.length() - 1) == 'a') ||
                (UserInput.charAt(0) == 'A' && UserInput.charAt(UserInput.length() - 1) == 'A')) {
            transitionLogCFG.appendText(UserInput + "\nS → aSa\n");
            return ruleProductions(UserInput.substring(1, UserInput.length() - 1));
        }

        // For production rule S → bSb
        if ((UserInput.charAt(0) == 'b' && UserInput.charAt(UserInput.length() - 1) == 'b') ||
                (UserInput.charAt(0) == 'B' && UserInput.charAt(UserInput.length() - 1) == 'B')) {
            transitionLogCFG.appendText(UserInput + "\nS → bSb\n");
            return ruleProductions(UserInput.substring(1, UserInput.length() - 1));
        }

        // Reject input
        return false;
    }


}
