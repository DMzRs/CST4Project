module project.automatatheoryproject {
    requires javafx.controls;
    requires javafx.fxml;


    opens project.automatatheoryproject to javafx.fxml;
    exports project.automatatheoryproject;
    exports TuringMachine;
    opens TuringMachine to javafx.fxml;
}