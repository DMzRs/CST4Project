package PushDown_Automata;

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
import java.util.Stack;

public class PDA_Controller {

    private Stack<Character> stack;

    private StringBuilder transitionLog;

    public PDA_Controller() {
        this.stack = new Stack<>();
        this.transitionLog = new StringBuilder();
    }

    // Method to log and simulate transitions
    private void logTransition(String state, String input, String stackState) {
        transitionLog.append("δ: (" + state + ", " + input + ", " + stackState + ")\n");
    }

    // Method for processing the input string with PDA
    private boolean processString(String inputString, int n) {
        stack.clear();
        String currentState = "q0";
        int aCounter = 0;
        String input = inputString;

        // Log the initial state with the full input
        logTransition(currentState, input, stack.toString());

        // Process the input string
        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i); // Get the character at the current index
            if (currentChar == 'a') {
                aCounter++;
                currentState = "q1";
                stack.push(currentChar);
                if (i+1==input.length()) {
                    logTransition(currentState, "ε", stack.toString());
                } else
                    logTransition(currentState, input.substring(i + 1), stack.toString());
            } else if (currentChar == 'b') {
                if(aCounter != n) {
                    return false;
                }
                if (stack.isEmpty() || stack.peek() != 'a') {

                    // Reject if no 'a' to pop from stack
                    logTransition(currentState, input.substring(i + 1), stack.toString());

                    return false;
                }

                stack.pop();

                currentState = "q2"; // Transition to state q1 when we start popping


                if (i+1==input.length()) {

                    logTransition(currentState, "ε", stack.toString());

                } else

                    logTransition(currentState, input.substring(i + 1), stack.toString());

            } else {

                // Reject on invalid character
                logTransition(currentState, input.substring(i + 1), stack.toString());

                return false;

            }

        }

        // Once the input string is fully processed, check if the stack is empty and reach final state
        if (stack.isEmpty()) {
            logTransition("q3", "ε", "[]");
            return true; // Accepted, stack is empty
        } else {
            // Reject if stack is not empty
            return false;
        }
    }

    //to verify if n input is integer
    private boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            nErrorLabel.setVisible(true);
            nErrorLabel.setText("Input must be a number!");
            return false;
        }
    }

    @FXML
    private TextField nField, stringField;
    @FXML
    private Label inputNLabel, stringLabel, transitionLabel, nErrorLabel, stringErrorLabel;
    @FXML
    private Button enterButton, verifyButton;
    @FXML
    private TextArea transitionLogPDA;

    @FXML
    private void initialize() {
        nErrorLabel.setVisible(false);
        stringErrorLabel.setVisible(false);
        nField.clear();
        stringField.clear();
        transitionLogPDA.clear();
        nField.setPromptText("n >= 1");
        stringComponents(false);
        transitionComponents(false);
    }

    //to set visibility on string input components
    @FXML
    private void stringComponents(boolean Visibility){
        stringField.setVisible(Visibility);
        stringLabel.setVisible(Visibility);
        verifyButton.setVisible(Visibility);
    }

    //to set Visibility on transition log components
    @FXML
    private void transitionComponents(boolean Visibility) {
        transitionLabel.setVisible(Visibility);
        transitionLogPDA.setVisible(Visibility);
    }


    //when enter button is clicked
    @FXML
    protected void enterButtonClicked() {
    String inputN = nField.getText();
    if (inputN.isEmpty()){
        nErrorLabel.setVisible(true);
        nErrorLabel.setText("Input a valid integer of N!");
    } else if (isInteger(inputN)) {
        Integer n = Integer.parseInt(inputN);
        if(n>=1) {
            nErrorLabel.setVisible(false);
            stringComponents(true);
        } else{
            nErrorLabel.setVisible(true);
            nErrorLabel.setText("n must be greater than or equal to 1");
        }
    }
    }

    //when verify button is clicked
    @FXML
    protected void verifyButtonClicked() {
        PDA_Controller pda = new PDA_Controller();
        String stringInput = stringField.getText();
        if (stringInput.isEmpty()){
            stringErrorLabel.setVisible(true);
            stringErrorLabel.setText("Input is empty!");
        } else {
            stringErrorLabel.setVisible(false);
            transitionComponents(true);
            if(pda.processString(stringInput, Integer.parseInt(nField.getText()))) {
                transitionLogPDA.appendText(String.valueOf(pda.transitionLog)); // Output transition logs
                transitionLogPDA.appendText("Accepted!");
            } else {
                transitionLogPDA.appendText(String.valueOf(pda.transitionLog)); // Output transition logs
                transitionLogPDA.appendText("Rejected: Stack not empty, number of a's and b's is not equal to n, or input invalid!");
            }
        }
    }

    //reset everything in PDA page
    @FXML
    protected void resetButtonClicked(){
        initialize();
    }

    //to go back to main page
    @FXML
    protected void backToMainPage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartProgram.class.getResource("MainPage.fxml"));
        Scene turingMachine = new Scene(fxmlLoader.load(), 800, 701);

        Stage currentStage = (Stage) nField.getScene().getWindow();
        currentStage.setTitle("Main Page");
        currentStage.setScene(turingMachine);
        currentStage.setResizable(false);
        currentStage.centerOnScreen();
        currentStage.show();
    }
}
