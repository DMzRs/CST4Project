package project.automatatheoryproject;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.Arrays;

public class TuringMachineController {
    @FXML
    private TextField number1Field, number2Field, resultField;
    @FXML
    private TextArea transitionLog;
    @FXML
    private Button performButton, additionButton, multiplicationButton, resetButton;
    @FXML
    private Label resultLabel, operationLabel;


    private String operation;
    private String tape;

    public enum State {
        q0, q1, q2, q3, q4, q5, HALT; // Enum for states, including HALT for stopping the machine
    }

    public void initialize() {
        setMainComponentsVisibility(false);
        operationButtonsVisibility(true);
    }

    private void setMainComponentsVisibility(boolean Visibility) {
        number1Field.setVisible(Visibility);
        number2Field.setVisible(Visibility);
        transitionLog.setVisible(Visibility);
        performButton.setVisible(Visibility);
        resultField.setVisible(Visibility);
        resultLabel.setVisible(Visibility);
        operationLabel.setVisible(Visibility);
        resetButton.setVisible(Visibility);
    }

    private void operationButtonsVisibility(boolean Visibility) {
        additionButton.setVisible(Visibility);
        multiplicationButton.setVisible(Visibility);
    }

    @FXML
    protected void additionButtonClicked() {
        operationLabel.setText("+");
        operation = "+";
        number1Field.setPromptText("Input Binary Number");
        number2Field.setPromptText("Input Binary Number");
        setMainComponentsVisibility(true);
        operationButtonsVisibility(false);
    }

    @FXML
    protected void multiplicationButtonClicked() {
        operationLabel.setText("*");
        operation = "*";
        number1Field.setPromptText("Input Unary Number");
        number2Field.setPromptText("Input Unary Number");
        setMainComponentsVisibility(true);
        operationButtonsVisibility(false);
    }

    @FXML
    protected void resetButtonClicked() {
        initialize();
    }

    @FXML
    protected void performButtonClicked() {
        if (operation.equals("+")) {
            String num1 = number1Field.getText();
            String num2 = number2Field.getText();

            // Calculate the required size for the tape
            char[] tape = new char[num1.length() + num2.length() + 2];  // +2 for the space and halting symbol
            Arrays.fill(tape, ' ');

            // Fill tape with the first number
            for (int i = 0; i < num1.length(); i++) {
                tape[i] = num1.charAt(i);
            }

            // Add space between the numbers
            tape[num1.length()] = ' ';

            // Fill tape with the second number
            for (int i = 0; i < num2.length(); i++) {
                tape[num1.length() + 1 + i] = num2.charAt(i);
            }

            // Perform binary addition
            char[] resultTape = performBinaryAddition(tape, transitionLog);

            // Extract result and update the result field
            String result = new String(resultTape).trim();
            resultField.setText("Result: " + result);
        } else if (operation.equals("*")) {
            tape = number1Field.getText() + operation + number2Field.getText();
            transitionLog.appendText("Current Input: " + number1Field.getText() + operation + number2Field.getText());
        }
    }


    public static char[] performBinaryAddition(char[] tape, TextArea transitionLog) {
        State currentState = State.q0; // Initial state
        int headPosition = 0;         // Starting position of the head
        int tapeLength = tape.length;

        while (currentState != State.HALT) {
            // Ensure the headPosition stays within valid bounds
            if (headPosition < 0 || headPosition >= tapeLength) {
                throw new ArrayIndexOutOfBoundsException("Head position " + headPosition + " is out of bounds.");
            }

            char currentSymbol = tape[headPosition];
            transitionLog.appendText("State: " + currentState + ", Read Symbol: " + currentSymbol + "\n");

            switch (currentState) {
                case q0: // Move right to end of the first block
                    if (currentSymbol == '0') {
                        headPosition++;
                        currentState = State.q0;
                    } else if (currentSymbol == '1') {
                        headPosition++;
                        currentState = State.q0;
                    } else if (currentSymbol == ' ') {
                        tape[headPosition] = '1';
                        headPosition++;
                        currentState = State.q1;
                    }
                    break;

                case q1: // Move right to the end of the second block
                    if (currentSymbol == '0') {
                        headPosition++;
                        currentState = State.q1;
                    } else if (currentSymbol == '1') {
                        headPosition++;
                        currentState = State.q1;
                    } else if (currentSymbol == ' ') {
                        tape[headPosition] = '2';
                        headPosition--;
                        currentState = State.q2;
                    }
                    break;

                case q2: // Subtract one in binary
                    if (currentSymbol == '0') {
                        tape[headPosition] = '1';
                        headPosition--;
                        currentState = State.q2;
                    } else if (currentSymbol == '1') {
                        tape[headPosition] = '0';
                        headPosition--;
                        currentState = State.q3;
                    } else if (currentSymbol == ' ') {
                        headPosition--;
                        currentState = State.q2;
                    }
                    break;

                case q3: // Move left to the end of the first block
                    if (currentSymbol == '0' || currentSymbol == '1') {
                        headPosition--;
                        currentState = State.q3;
                    } else if (currentSymbol == ' ') {
                        headPosition++;
                        currentState = State.q4;
                    }
                    break;

                case q4: // Add one in binary
                    if (currentSymbol == '0') {
                        headPosition++;
                        currentState = State.q4;
                    } else if (currentSymbol == '1') {
                        headPosition++;
                        currentState = State.q4;
                    } else if (currentSymbol == ' ') {
                        currentState = State.HALT;
                    }
                    break;

                case q5: // Cleanup state, ensure tape is finalized
                    if (currentSymbol == ' ') {
                        currentState = State.HALT;
                    }
                    break;

                default:
                    throw new IllegalStateException("Invalid state: " + currentState);
            }
        }

        return tape;
    }


}