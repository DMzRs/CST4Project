package DeterministicFiniteAutomata;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import java.util.LinkedList;
import java.util.ListIterator;

import javafx.util.Duration;
import project.automatatheoryproject.StartProgram;

import java.io.IOException;

public class DFA_Controller {
    @FXML
    private Circle q0Circle,q1Circle,q2Circle,q3Circle,q4Circle;
    @FXML
    private TextField stringField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button simulateButton, resetButton, skipButton, pauseButton;

    private Timeline animation;

    @FXML
    private void animateCircle(Circle circle, Color newColor, Runnable onFinished) {
        animation = new Timeline();
        KeyValue fillColor = new KeyValue(circle.fillProperty(), newColor);
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5), event -> {
            if (onFinished != null) onFinished.run();
        }, fillColor);
        animation.getKeyFrames().add(keyFrame);
        animation.play();
    }



    //to go back to main page
    @FXML
    protected void backToMainPage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartProgram.class.getResource("MainPage.fxml"));
        Scene turingMachine = new Scene(fxmlLoader.load(), 800, 701);

        Stage currentStage = (Stage) stringField.getScene().getWindow();
        currentStage.setTitle("Main Page");
        currentStage.setScene(turingMachine);
        currentStage.setResizable(false);
        currentStage.centerOnScreen();
        currentStage.show();
    }

    @FXML
    protected void initialize() {
        pauseButton.setText("Pause");
        q0Circle.setFill(Color.WHITE);
        q1Circle.setFill(Color.WHITE);
        q2Circle.setFill(Color.WHITE);
        q3Circle.setFill(Color.WHITE);
        q4Circle.setFill(Color.WHITE);
        stringField.clear();
        simulateButton.setVisible(true);
        errorLabel.setText("");
    }

    @FXML
    protected void resetButtonClicked() {
        initialize();
    }

    @FXML
    protected void simulateButtonClicked() {
        String stringInput = stringField.getText();
        if (stringInput.isEmpty()) {
            errorLabel.setText("Please enter a string!");
        } else {
            simulateButton.setVisible(false);
            runDFA(stringInput);
        }
    }

    private void runDFA(String stringInput) {
        int[] state = {0}; // Using an array to mutate state inside a lambda
        LinkedList<Character> inputQueue = new LinkedList<>();
        for (char c : stringInput.toCharArray()) {
            inputQueue.add(c);
        }

        // Ensure q0Circle is green before starting
        animateCircle(q0Circle, Color.GREEN, () -> processNextCharacter(inputQueue, state));
    }

    private void processNextCharacter(LinkedList<Character> inputQueue, int[] state) {
        if (inputQueue.isEmpty()) {
            // Final state check after processing all input
            if (state[0] == 1) {
                errorLabel.setText("Letter Accepted");
            } else if (state[0] == 2) {
                errorLabel.setText("Number Accepted");
            } else if (state[0] == 4) {
                errorLabel.setText("Word Accepted");
            } else {
                errorLabel.setText("String Rejected");
            }
            return;
        }

        char c = inputQueue.poll(); // Get the next character
        switch (state[0]) {
            case 0:
                if (Character.isDigit(c)) {
                    state[0] = 2;
                    animateCircle(q0Circle, Color.WHITE, () -> animateCircle(q2Circle, Color.GREEN, () -> processNextCharacter(inputQueue, state)));
                } else if (Character.isLetter(c)) {
                    state[0] = 1;
                    animateCircle(q0Circle, Color.WHITE, () -> animateCircle(q1Circle, Color.GREEN, () -> processNextCharacter(inputQueue, state)));
                } else {
                    errorLabel.setText("String Rejected");
                }
                break;

            case 1:
                if (Character.isDigit(c)) {
                    state[0] = 3;
                    animateCircle(q1Circle, Color.WHITE, () -> animateCircle(q3Circle, Color.GREEN, () -> processNextCharacter(inputQueue, state)));
                } else if (Character.isLetter(c)) {
                    state[0] = 4;
                    animateCircle(q1Circle, Color.WHITE, () -> animateCircle(q4Circle, Color.GREEN, () -> processNextCharacter(inputQueue, state)));
                } else {
                    errorLabel.setText("String Rejected");
                }
                break;

            case 2:
                if (Character.isDigit(c)) {
                    state[0] = 2;
                    animateCircle(q2Circle, Color.WHITE, () -> animateCircle(q2Circle, Color.GREEN, () -> processNextCharacter(inputQueue, state)));
                } else if (Character.isLetter(c)) {
                    state[0] = 3;
                    animateCircle(q2Circle, Color.WHITE, () -> animateCircle(q3Circle, Color.GREEN, () -> processNextCharacter(inputQueue, state)));
                } else {
                    errorLabel.setText("String Rejected");
                }
                break;
            case 3:
                if (Character.isDigit(c)) {
                    state[0] = 3;
                    animateCircle(q3Circle, Color.WHITE, () -> animateCircle(q3Circle, Color.GREEN, () -> processNextCharacter(inputQueue, state)));
                } else if (Character.isLetter(c)) {
                    state[0] = 3;
                    animateCircle(q3Circle, Color.WHITE, () -> animateCircle(q3Circle, Color.GREEN, () -> processNextCharacter(inputQueue, state)));
                } else {
                    errorLabel.setText("String Rejected");
                }
                break;
            case 4:
                if (Character.isDigit(c)) {
                    state[0] = 3;
                    animateCircle(q4Circle, Color.WHITE, () -> animateCircle(q3Circle, Color.GREEN, () -> processNextCharacter(inputQueue, state)));
                } else if (Character.isLetter(c)) {
                    state[0] = 4;
                    animateCircle(q4Circle, Color.WHITE, () -> animateCircle(q4Circle, Color.GREEN, () -> processNextCharacter(inputQueue, state)));
                } else {
                    errorLabel.setText("String Rejected");
                }
                break;

            default:
                errorLabel.setText("String Rejected");
                break;
        }
    }



    @FXML
    protected void pauseButtonClicked() {
        if (animation.getStatus() == Animation.Status.RUNNING) {
            animation.pause();
            pauseButton.setText("Play");
        } else if (animation.getStatus() == Animation.Status.PAUSED) {
            animation.play();
            pauseButton.setText("Pause");
        }
    }

    @FXML protected void skipButtonClicked() {
        animation.jumpTo(Duration.seconds(0.5));
    }
}
