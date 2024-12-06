package project.automatatheoryproject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {
    @FXML
    Label automataLabel;

    @FXML
    protected void enterMainPage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartProgram.class.getResource("MainPage.fxml"));
        Scene mainPage = new Scene(fxmlLoader.load(), 800, 701);

        Stage currentStage = (Stage) automataLabel.getScene().getWindow();
        currentStage.setTitle("Main Page");
        currentStage.setScene(mainPage);
        currentStage.setResizable(false);
        currentStage.centerOnScreen();
        currentStage.show();
    }

    @FXML
    protected void exitHomePage() throws IOException {
        System.exit(0);
    }
}