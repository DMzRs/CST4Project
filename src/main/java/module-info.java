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
    exports ContextFreeGrammar;
    opens ContextFreeGrammar to javafx.fxml;
    exports DeterministicFiniteAutomata;
    opens DeterministicFiniteAutomata to javafx.fxml;
    exports NonDeterministicFiniteAutomata;
    opens NonDeterministicFiniteAutomata to javafx.fxml;
    exports TowerOfHanoi to javafx.graphics;
    opens TowerOfHanoi to javafx.fxml;
}