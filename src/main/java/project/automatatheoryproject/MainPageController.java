package project.automatatheoryproject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class MainPageController {
    @FXML
    Label turingMachLabel;
    @FXML
    protected void enterTuringMachine() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainPage.class.getResource("TuringMachinePage.fxml"));
        Scene turingMachine = new Scene(fxmlLoader.load(), 700, 550);

        Stage currentStage = (Stage) turingMachLabel.getScene().getWindow();
        currentStage.setTitle("Turing Machine Page");
        currentStage.setScene(turingMachine);
        currentStage.setResizable(false);
        currentStage.centerOnScreen();
        currentStage.show();
    }
}
