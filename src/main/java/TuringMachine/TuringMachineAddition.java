package TuringMachine;

import java.util.HashMap;
import java.util.Map;

class Transition {
    String currentState;
    char readSymbol;
    String nextState;
    char writeSymbol;
    char direction;

    Transition(String currentState, char readSymbol, String nextState, char writeSymbol, char direction) {
        this.currentState = currentState;
        this.readSymbol = readSymbol;
        this.nextState = nextState;
        this.writeSymbol = writeSymbol;
        this.direction = direction;
    }
}

class TransitionFunction {
    private Map<String, Map<Character, Transition>> transitions;

    TransitionFunction() {
        transitions = new HashMap<>();
    }

    public void addTransition(Transition transition) {
        transitions
                .computeIfAbsent(transition.currentState, k -> new HashMap<>())
                .put(transition.readSymbol, transition);
    }

    public Transition getTransition(String currentState, char readSymbol) {
        return transitions.getOrDefault(currentState, new HashMap<>()).get(readSymbol);
    }
}

class Cell {
    char data;
    Cell prev;
    Cell next;

    Cell(char data) {
        this.data = data;
        this.prev = null;
        this.next = null;
    }
}

class TuringTape {
    Cell head;
    Cell first;
    Cell last;

    TuringTape() {
        first = new Cell('#');
        last = new Cell('#');
        first.next = last;
        last.prev = first;
        head = first;
    }

    public void moveRight() {
        if (head.next != null) {
            head = head.next;
        }
    }

    public void moveRightmost() {
        if (head.next != null) {
            head = last;
        }
    }

    public void moveLeft() {
        if (head.prev != null) {
            head = head.prev;
        }
    }

    public void write(char value) {
        if (head != null) {
            head.data = value;
        }
    }

    public char read() {
        if (head != null) {
            return head.data;
        }
        return '\0';
    }

    public void addCell(char value) {
        Cell newCell = new Cell(value);
        newCell.prev = head;
        newCell.next = head.next;

        if (head.next != null) {
            head.next.prev = newCell;
        } else {
            last = newCell;
        }

        head.next = newCell;
        head = newCell;
    }

    public void extendLeft() {
        Cell newCell = new Cell('#');
        newCell.next = first;
        first.prev = newCell;
        first = newCell;
        if (head == null) {
            head = newCell;
        }
    }
}

public class TuringMachineAddition {
    TuringTape tape;
    TransitionFunction transitionFunction;
    String currentState;
    int step;
    private TuringMachineController controller;

    public TuringMachineAddition(TuringMachineController controller) {
        this.controller = controller;
        this.tape = new TuringTape();
        this.transitionFunction = new TransitionFunction();
        this.currentState = "q0";
        this.step = 0;
    }

    public void process() {
        while (true) {
            char symbol = tape.read();
            Transition transition = transitionFunction.getTransition(currentState, symbol);

            if (currentState.equals("q0") && symbol != '#') {
                tape.moveRightmost();
                controller.transitionLogTuringMachine.appendText("\n\nStep " + ++step + ": Skipping to rightmost.");
                System.out.println();

                continue;
            }

            step++;

            controller.transitionLogTuringMachine.appendText("\n\nStep: " + step + ", Current State: " + currentState + ", Read Symbol: " + symbol +", Write Symbol: "+ transition.writeSymbol + ", Move to: " + transition.direction + ", Next State: " + transition.nextState);
            controller.transitionLogTuringMachine.appendText("\nTape: ");
            printTapeToOutput();


            if (transition == null) {
                controller.transitionLogTuringMachine.appendText("\n\nRejected. Invalid tape symbol found. (" + step + " steps)");
                break;
            }

            if (currentState.equals("q3") && symbol == '#') {
                tape.extendLeft();
            }

            tape.write(transition.writeSymbol);

            switch (transition.direction) {
                case 'R':
                    tape.moveRight();
                    break;
                case 'L':
                    tape.moveLeft();
                    break;
                case 'H':
                    controller.transitionLogTuringMachine.appendText("\n\nHalting.");
                    printAnswerToOutput();
                    controller.transitionLogTuringMachine.appendText("\n\nAccepted. (" + step + " steps)");
                    return;
                default:
                    controller.transitionLogTuringMachine.appendText("\n\nRejected. Invalid direction. (" + step + " steps)");
                    return;
            }

            currentState = transition.nextState;
        }
    }

    private void printTapeToOutput() {
        StringBuilder tapeString = new StringBuilder();
        Cell current = tape.first;
        while (current != null) {
            tapeString.append(current.data).append(" ");
            current = current.next;
        }
        controller.transitionLogTuringMachine.appendText(tapeString.toString());
    }

    private void printAnswerToOutput() {
        StringBuilder tapeString = new StringBuilder();
        Cell current = tape.first;
        while (current != null) {
            if (current.data != '#') {
                tapeString.append(current.data);
            }
            current = current.next;
        }
        controller.resultField.setText(tapeString.toString());
    }

    public void addTransition(String currentState, char readSymbol, String nextState, char writeSymbol, char direction) {
        transitionFunction.addTransition(new Transition(currentState, readSymbol, nextState, writeSymbol, direction));
    }
}
