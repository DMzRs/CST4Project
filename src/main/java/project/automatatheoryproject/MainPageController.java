package project.automatatheoryproject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class MainPageController {
    @FXML
    Label theoryLabel;

    @FXML
    protected void enterTuringMachine() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartProgram.class.getResource("TuringMachinePage.fxml"));
        Scene turingMachine = new Scene(fxmlLoader.load(), 1200, 800);

        Stage currentStage = (Stage) theoryLabel.getScene().getWindow();
        currentStage.setTitle("Turing Machine Page");
        currentStage.setScene(turingMachine);
        currentStage.setResizable(false);
        currentStage.centerOnScreen();
        currentStage.show();
    }
    @FXML
    protected void enterPushDownAutomata() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartProgram.class.getResource("PushDownAutomata.fxml"));
        Scene pda = new Scene(fxmlLoader.load(), 1000, 850);

        Stage currentStage = (Stage) theoryLabel.getScene().getWindow();
        currentStage.setTitle("Push Down Automata Page");
        currentStage.setScene(pda);
        currentStage.setResizable(false);
        currentStage.centerOnScreen();
        currentStage.show();
    }
}
