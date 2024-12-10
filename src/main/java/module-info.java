module project.automatatheoryproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens project.automatatheoryproject to javafx.fxml;
    exports project.automatatheoryproject;
    exports TuringMachine;
    opens TuringMachine to javafx.fxml;
    exports PushDown_Automata;
    opens PushDown_Automata to javafx.fxml;
}