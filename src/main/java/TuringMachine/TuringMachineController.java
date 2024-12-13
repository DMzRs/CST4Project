package TuringMachine;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import project.automatatheoryproject.StartProgram;


import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;



public class TuringMachineController {

    @FXML
    private TextField number1Field, number2Field;
    @FXML
    public TextField resultField;
    @FXML
    public TextArea transitionLogTuringMachine;
    @FXML
    private Button performButton, additionButton, multiplicationButton, resetButton;
    @FXML
    private Label resultLabel, operationLabel, transitionLogLabel, transitionDiagramLabel, instructionLabel, instructionLabel1;
    @FXML
    private ImageView transitionImage;
    @FXML
    private Separator separator;

    private int steps;
    private String operation;

    public void initialize() {
        setMainComponentsVisibility(false);
        operationButtonsVisibility(true);
        number1Field.clear();
        number2Field.clear();
        resultField.clear();
        transitionLogTuringMachine.clear();
    }

    private void setMainComponentsVisibility(boolean visibility) {
        number1Field.setVisible(visibility);
        number2Field.setVisible(visibility);
        transitionLogTuringMachine.setVisible(visibility);
        performButton.setVisible(visibility);
        resultField.setVisible(visibility);
        resultLabel.setVisible(visibility);
        operationLabel.setVisible(visibility);
        resetButton.setVisible(visibility);
        transitionLogLabel.setVisible(visibility);
        transitionDiagramLabel.setVisible(visibility);
        transitionImage.setVisible(visibility);
        separator.setVisible(visibility);
        instructionLabel.setVisible(visibility);
        instructionLabel1.setVisible(visibility);
    }

    private void operationButtonsVisibility(boolean visibility) {
        additionButton.setVisible(visibility);
        multiplicationButton.setVisible(visibility);
    }

    //to go back to main page
    @FXML
    protected void backToMainPage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartProgram.class.getResource("MainPage.fxml"));
        Scene turingMachine = new Scene(fxmlLoader.load(), 800, 701);

        Stage currentStage = (Stage) resultField.getScene().getWindow();
        currentStage.setTitle("Main Page");
        currentStage.setScene(turingMachine);
        currentStage.setResizable(false);
        currentStage.centerOnScreen();
        currentStage.show();
    }

    @FXML
    protected void additionButtonClicked() {
        operationLabel.setText("+");
        operation = "+";
        number1Field.setPromptText("Input Binary Number");
        number2Field.setPromptText("Input Binary Number");
        instructionLabel1.setText("This TM performs addition using binary digits. Thus, input must only contain 0's and 1's. Otherwise, it won't perform the operation.");
        Image transImage = new Image(getClass().getResource("/Images/BinaryAdditionDiagram.png").toExternalForm());
        transitionImage.setImage(transImage);
        setMainComponentsVisibility(true);
        operationButtonsVisibility(false);
    }

    @FXML
    protected void multiplicationButtonClicked() {
        operationLabel.setText("X");
        operation = "X";
        number1Field.setPromptText("Input Unary Number");
        number2Field.setPromptText("Input Unary Number");
        instructionLabel1.setText("This TM performs multiplication using unary digits. Thus, input must only contain 1's. Otherwise, it won't perform the operation.");
        Image transImage = new Image(getClass().getResource("/Images/UnaryMultiplicationDiagram.png").toExternalForm());
        transitionImage.setImage(transImage);
        setMainComponentsVisibility(true);
        operationButtonsVisibility(false);
    }

    @FXML
    protected void resetButtonClicked() {
        initialize();
    }

    @FXML
    protected void performButtonClicked() {
        if (operation.equals("+")) {    //Operation or BinaryAddition
            transitionLogTuringMachine.clear();
            String num1 = number1Field.getText().trim();
            String num2 = number2Field.getText().trim();
            if(num1.isEmpty()||num2.isEmpty()){
                transitionLogTuringMachine.appendText("Input is empty!\n");
            } else if (num1.matches("[01]*")&&num2.matches("[01]*")){
                TuringMachineAddition tm = new TuringMachineAddition(this);

                // Define the transitions
                tm.addTransition("q0", '0', "q0", '0', 'R');
                tm.addTransition("q0", '1', "q0", '1', 'R');
                tm.addTransition("q0", '+', "q0", '+', 'R');
                tm.addTransition("q0", '#', "q1", '#', 'L');

                tm.addTransition("q1", '0', "q1", '1', 'L');
                tm.addTransition("q1", '1', "q2", '0', 'L');
                tm.addTransition("q1", '+', "q4", '#', 'R');

                tm.addTransition("q2", '1', "q2", '1', 'L');
                tm.addTransition("q2", '0', "q2", '0', 'L');
                tm.addTransition("q2", '+', "q3", '+', 'L');

                tm.addTransition("q3", '1', "q3", '0', 'L');
                tm.addTransition("q3", '0', "q0", '1', 'R');
                tm.addTransition("q3", '#', "q0", '1', 'R');

                tm.addTransition("q4", '1', "q4", '#', 'R');
                tm.addTransition("q4", '#', "q5", '#', 'H');


                String input = number1Field.getText() + "+" + number2Field.getText();

                for (char ch : input.toCharArray()) {
                    tm.tape.addCell(ch);
                }
                tm.tape.head = tm.tape.first.next;

                // Process the Turing Machine
                tm.process();
                performButton.setVisible(false);
            } else {
                transitionLogTuringMachine.appendText("Input must only contain 0's and 1's!\n");
            }
        } else if (operation.equals("X")) {  //operation for unaryMultiplication
            transitionLogTuringMachine.clear();
            String num1 = number1Field.getText().trim();
            String num2 = number2Field.getText().trim();
            if(num1.isEmpty()||num2.isEmpty()) {
                transitionLogTuringMachine.appendText("Input is empty!\n");
            } else if (num1.matches("[1]*")&&num2.matches("[1]*")) {
                String input = number1Field.getText() + "0" + number2Field.getText();
                    LinkedList<Character> tape = new LinkedList<>();

                    // Populate the tape with input string
                    for (char c : input.toCharArray()) {
                        tape.add(c);
                    }

                    // Add '$' at the beginning and end of the tape
                    tape.addFirst('$');
                    tape.addLast('$');

                    // Run the Turing machine transitions
                    runTuringMachine(tape);

                    // Output the result
                    printTapeToResultField(tape);
                    performButton.setVisible(false);
            } else {
                transitionLogTuringMachine.appendText("Input must only contain 1's!\n");
            }
        }
    }

    // Method to simulate the Turing machine transitions
    private void runTuringMachine(LinkedList<Character> tape) {
        ListIterator<Character> iterator = tape.listIterator();  // Create an iterator for the tape
        char currentSymbol;
        int state = 0;
        iterator.next(); //skip first $ to avoid error

        while (state != 12) {// halts if reaches q12
            if (!iterator.hasNext()) {
                break;
            }

            currentSymbol = iterator.next();  // to get current symbol at the tape head

            // Transitions
            switch (state) {
                case 0:
                    transitionLogTuringMachine.appendText("\nCurrent State: " + state + ", Current Symbol: " + currentSymbol);
                    if (currentSymbol == '0') {
                        iterator.set('0');
                        transitionLogTuringMachine.appendText(", Write Symbol: 0, Move to: R, Next State: q0");
                        state = 0;
                    } else if (currentSymbol == '1') {
                        iterator.set('1');
                        transitionLogTuringMachine.appendText(", Write Symbol: 1, Move to: R, Next State: q0");
                        state = 0;
                    } else if (currentSymbol == '$') {
                        iterator.set('0');
                        iterator.add('$');
                        iterator.previous();
                        iterator.previous();
                        transitionLogTuringMachine.appendText(", Write Symbol: 0, Move to: L, Next State: q1");
                        state = 1;
                    }
                    break;

                case 1:
                    transitionLogTuringMachine.appendText("\nCurrent State: " + state + ", Current Symbol: " + currentSymbol);
                    if (currentSymbol == '0') {
                        iterator.set('0');
                        iterator.previous();
                        iterator.previous();
                        transitionLogTuringMachine.appendText(", Write Symbol: 0, Move to: L, Next State: q1");
                        state = 1;
                    } else if (currentSymbol == '1') {
                        iterator.set('1');
                        iterator.previous();
                        iterator.previous();
                        transitionLogTuringMachine.appendText(", Write Symbol: 1, Move to: L, Next State: q1");
                        state = 1;
                    } else if (currentSymbol == '$') {
                        iterator.set('$');
                        transitionLogTuringMachine.appendText(", Write Symbol: $, Move to: R, Next State: q2");
                        state = 2;
                    }
                    break;

                case 2:
                    transitionLogTuringMachine.appendText("\nCurrent State: " + state + ", Current Symbol: " + currentSymbol);
                    if (currentSymbol == '1') {
                        iterator.set('X');
                        transitionLogTuringMachine.appendText(", Write Symbol: X, Move to: R, Next State: q3");
                        state = 3;
                    } else if (currentSymbol == 'X') {
                        iterator.set('X');
                        transitionLogTuringMachine.appendText(", Write Symbol: X, Move to: R, Next State: q2");
                        state = 2;
                    } else if (currentSymbol == '0') {
                        iterator.set('0');
                        iterator.previous();
                        iterator.previous();
                        transitionLogTuringMachine.appendText(", Write Symbol: 0, Move to: L, Next State: q9");
                        state = 9;
                    }
                    break;

                case 3:
                    transitionLogTuringMachine.appendText("\nCurrent State: " + state + ", Current Symbol: " + currentSymbol);
                    if (currentSymbol == '1') {
                        iterator.set('1');
                        transitionLogTuringMachine.appendText(", Write Symbol: 1, Move to: R, Next State: q3");
                        state = 3;
                    } else if (currentSymbol == '0') {
                        iterator.set('0');
                        transitionLogTuringMachine.appendText(", Write Symbol: 0, Move to: R, Next State: q4");
                        state = 4;
                    }
                    break;

                case 4:
                    transitionLogTuringMachine.appendText("\nCurrent State: " + state + ", Current Symbol: " + currentSymbol);
                    if (currentSymbol == 'Y') {
                        iterator.set('Y');
                        transitionLogTuringMachine.appendText(", Write Symbol: Y, Move to: R, Next State: q4");
                        state = 4;
                    } else if (currentSymbol == '1') {
                        iterator.set('Y');
                        transitionLogTuringMachine.appendText(", Write Symbol: Y, Move to: R, Next State: q5");
                        state = 5;
                    } else if (currentSymbol == '0') {
                        iterator.set('0');
                        iterator.previous();
                        iterator.previous();
                        transitionLogTuringMachine.appendText(", Write Symbol: 0, Move to: L, Next State: q8");
                        state = 8;
                    }
                    break;

                case 5:
                    transitionLogTuringMachine.appendText("\nCurrent State: " + state + ", Current Symbol: " + currentSymbol);
                    if (currentSymbol == '1') {
                        iterator.set('1');
                        transitionLogTuringMachine.appendText(", Write Symbol: 1, Move to: R, Next State: q5");
                        state = 5;
                    } else if (currentSymbol == '0') {
                        iterator.set('0');
                        transitionLogTuringMachine.appendText(", Write Symbol: 0, Move to: R, Next State: q6");
                        state = 6;
                    }
                    break;

                case 6:
                    transitionLogTuringMachine.appendText("\nCurrent State: " + state + ", Current Symbol: " + currentSymbol);
                    if (currentSymbol == '1') {
                        iterator.set('1');
                        transitionLogTuringMachine.appendText(", Write Symbol: 1, Move to: R, Next State: q6");
                        state = 6;
                    } else if (currentSymbol == '$') {
                        iterator.set('1');
                        iterator.add('$');
                        iterator.previous();
                        iterator.previous();
                        transitionLogTuringMachine.appendText(", Write Symbol: 1, Move to: L, Next State: q7");
                        state = 7;
                    }
                    break;

                case 7:
                    transitionLogTuringMachine.appendText("\nCurrent State: " + state + ", Current Symbol: " + currentSymbol);
                    if (currentSymbol == '0') {
                        iterator.set('0');
                        iterator.previous();
                        iterator.previous();
                        transitionLogTuringMachine.appendText(", Write Symbol: 0, Move to: L, Next State: q7");
                        state = 7;
                    } else if (currentSymbol == '1') {
                        iterator.set('1');
                        iterator.previous();
                        iterator.previous();
                        transitionLogTuringMachine.appendText(", Write Symbol: 1, Move to: L, Next State: q7");
                        state = 7;
                    } else if (currentSymbol == 'Y') {
                        iterator.set('Y');
                        transitionLogTuringMachine.appendText(", Write Symbol: Y, Move to: R, Next State: q4");
                        state = 4;
                    }
                    break;

                case 8:
                    transitionLogTuringMachine.appendText("\nCurrent State: " + state + ", Current Symbol: " + currentSymbol);
                    if (currentSymbol == 'Y') {
                        iterator.set('1');
                        iterator.previous();
                        iterator.previous();
                        transitionLogTuringMachine.appendText(", Write Symbol: 1, Move to: L, Next State: q8");
                        state = 8;
                    } else if (currentSymbol == '1') {
                        iterator.set('1');
                        iterator.previous();
                        iterator.previous();
                        transitionLogTuringMachine.appendText(", Write Symbol: 1, Move to: L, Next State: q8");
                        state = 8;
                    } else if (currentSymbol == '0') {
                        iterator.set('0');
                        iterator.previous();
                        iterator.previous();
                        transitionLogTuringMachine.appendText(", Write Symbol: 0, Move to: L, Next State: q8");
                        state = 8;
                    } else if (currentSymbol == 'X') {
                        iterator.set('X');
                        transitionLogTuringMachine.appendText(", Write Symbol: X, Move to: R, Next State: q2");
                        state = 2;
                    }
                    break;

                case 9:
                    transitionLogTuringMachine.appendText("\nCurrent State: " + state + ", Current Symbol: " + currentSymbol);
                    if (currentSymbol == 'X') {
                        iterator.set('X');
                        iterator.previous();
                        iterator.previous();
                        transitionLogTuringMachine.appendText(", Write Symbol: X, Move to: L, Next State: q9");
                        state = 9;
                    } else if (currentSymbol == '$') {
                        iterator.set('$');
                        transitionLogTuringMachine.appendText(", Write Symbol: $, Move to: R, Next State: q10");
                        state = 10;
                    }
                    break;

                case 10:
                    transitionLogTuringMachine.appendText("\nCurrent State: " + state + ", Current Symbol: " + currentSymbol);
                    if (currentSymbol == 'X') {
                        iterator.set('$');
                        transitionLogTuringMachine.appendText(", Write Symbol: $, Move to: R, Next State: q10");
                        state = 10;
                    } else if (currentSymbol == '0') {
                        iterator.set('$');
                        transitionLogTuringMachine.appendText(", Write Symbol: $, Move to: R, Next State: q11");
                        state = 11;
                    }
                    break;

                case 11:
                    transitionLogTuringMachine.appendText("\nCurrent State: " + state + ", Current Symbol: " + currentSymbol);
                    if (currentSymbol == '1') {
                        iterator.set('$');
                        transitionLogTuringMachine.appendText(", Write Symbol: $, Move to: R, Next State: q11");
                        state = 11;
                    }else if (currentSymbol == '0') {
                        iterator.set('$');
                        transitionLogTuringMachine.appendText(", Write Symbol: $, Move to: R, Next State: q12");
                        state = 12;
                    }
                    break;
            }
            steps++;
            transitionLogTuringMachine.appendText("\nCurrent Tape: ");
            printTape(tape);
            transitionLogTuringMachine.appendText("\nStep: "+steps+"\n");

            if(state==12){
                transitionLogTuringMachine.appendText("\nHALTED!\nTotal Steps: "+steps+"\n");
            }
        }
    }

    //to print tape
    private void printTape(LinkedList<Character> tape) {
        ListIterator<Character> iterator = tape.listIterator();
        while (iterator.hasNext()) {
            transitionLogTuringMachine.appendText(String.valueOf(iterator.next()));
        }
    }

    //to print to result
    private void printTapeToResultField(LinkedList<Character> tape) {
        ListIterator<Character> iterator = tape.listIterator();
        while (iterator.hasNext()) {
            char currentChar = iterator.next();
            if (currentChar != '$') { // Skip '$' characters
                resultField.appendText(String.valueOf(currentChar));
            }
        }
    }
}
