package TowerOfHanoi;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class Towers {

    @FXML
    private Pane towerPane;

    @FXML
    private TextArea process;

    @FXML
    private Spinner<Integer> diskSpinner;

    @FXML
    private Label movesCountLabel;

    @FXML
    private Button runButton;

    private boolean isAnimationRunning = false;
    private TextArea movesArea;
    private static final int BASE_Y = 400;
    private static final int DISK_HEIGHT = 18;
    private static final int DISK_BASE_WIDTH = 100;
    private static final int TOWER_WIDTH = 10;
    private static final int SPACE_BETWEEN_TOWERS = 190;
    private static final int BOTTOM_Y = 200;
    private Stack<Rectangle>[] towers = new Stack[3]; // Array of stacks representing the towers
    private int numOfDisks = 3; // Default number of disks
    private ArrayList<int[]> moveList = new ArrayList<>(); // Stores the sequence of moves
    private Timeline animation;
    private double animationSpeed = 1.0;
    private Random random = new Random(42);


    @FXML
    private void initialize(){
        // Initialize towers as stacks
        for (int i = 0; i < 3; i++) {
            towers[i] = new Stack<>();
        }

        resetGame();
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(3, 12, 3);
        diskSpinner.setValueFactory(valueFactory);
    }

    @FXML
    private void handleRun() {
        if (towers[0].isEmpty() && towers[1].isEmpty() && towers[2].size() == numOfDisks) {
            process.appendText("Can't run animation since it has already reached the final state.\n");
            return;
        }

        if (isAnimationRunning) {
            process.appendText("Animation is already running...\n");
            return;
        }

        process.appendText("Starting simulation...\n");
        isAnimationRunning = true;

        moveList.clear();
        movesCountLabel.setText("0");
        numOfDisks = diskSpinner.getValue(); // Use the number of disks selected by the user
        solveHanoi(numOfDisks, 0, 2, 1);
        playAnimation();
    }

    @FXML
    private void handlePause() {
        if (animation != null && animation.getStatus() == Animation.Status.RUNNING) {
            process.appendText("Pausing animation...\n");
            animation.pause();
            isAnimationRunning = false;
        } else if (animation != null && animation.getStatus() == Animation.Status.PAUSED) {
            process.appendText("Resuming animation...\n");
            animation.play();
            isAnimationRunning = true;
        } else {
            process.appendText("No animation to pause or resume.\n");
        }
    }

    @FXML
    private void handleShowMoves() {
        if (movesArea == null) {
            movesArea = new TextArea();
            movesArea.setEditable(false);
            movesArea.setWrapText(true);
            movesArea.setPrefHeight(300);
        }

        if (moveList.isEmpty()) {
            movesArea.setText("No moves to display. Please click 'Run' to calculate the moves.");
        } else {
            StringBuilder moves = new StringBuilder();
            for (int i = 0; i < moveList.size(); i++) {
                int[] move = moveList.get(i);
                moves.append("Move ").append(i + 1).append(": Tower ").append(move[0] + 1)
                        .append(" -> Tower ").append(move[1] + 1).append("\n");
            }
            movesArea.setText(moves.toString());
        }

        // Create new window to display the moves
        Stage movesStage = new Stage();
        movesStage.setTitle("Transition Moves");

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 15; -fx-background-color: #f9f9f9;");
        Label titleLabel = new Label("List of Moves:");
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        layout.getChildren().addAll(titleLabel, movesArea);

        Scene scene = new Scene(layout, 400, 400);
        movesStage.setScene(scene);
        movesStage.show();
    }



    @FXML
    private void handleReset() {
        resetGame();
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    @FXML
    private void handleSlow() {
        animationSpeed = 2.0;
        if (animation != null) animation.setRate(0.5);
    }

    @FXML
    private void handleFast() {
        animationSpeed = 0.5;
        if (animation != null) animation.setRate(2.0);
    }

    @FXML
    public void handleSkip(ActionEvent actionEvent) {
        if (moveList.isEmpty()) {
            process.appendText("No moves to skip. Please run the simulation first.\n");
            return;
        }

        if (animation != null) {
            animation.stop();
            isAnimationRunning = false;
        }

        if (towers[0].isEmpty() && towers[1].isEmpty()) {
            process.appendText("Can't run animation since it has already reached the final state.\n");
            return;
        }

        // Clear the remaining disks from Tower A and Tower B
        while (!towers[0].isEmpty()) {
            Rectangle disk = towers[0].pop();
            towerPane.getChildren().remove(disk);
        }
        while (!towers[1].isEmpty()) {
            Rectangle disk = towers[1].pop();
            towerPane.getChildren().remove(disk);
        }

        // Remove all disks from Tower C
        while (!towers[2].isEmpty()) {
            Rectangle disk = towers[2].pop();
            towerPane.getChildren().remove(disk);
        }

        random = new Random(42);
        for (int i = numOfDisks; i > 0; i--) {
            double diskWidth = DISK_BASE_WIDTH * (i / (double) numOfDisks);
            diskWidth = Math.max(diskWidth, 11);

            double centerX = getTowerXPosition(2) + TOWER_WIDTH / 2 - diskWidth / 2;
            double yPosition = BASE_Y - DISK_HEIGHT * (numOfDisks - i + 1);

            Rectangle disk = new Rectangle(centerX, yPosition, diskWidth, DISK_HEIGHT);
            disk.setFill(Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble()));

            towers[2].push(disk);
            towerPane.getChildren().add(disk);
        }
        process.appendText("Simulation skipped. All disks are now on Tower C in correct order.\n");
        isAnimationRunning = false;
        runButton.setDisable(true);
    }


    private void playAnimation() {
        if (animation != null) {
            animation.stop();
        }

        animation = new Timeline();
        animation.setCycleCount(1);
        animation.getKeyFrames().clear();

        double totalDuration = animationSpeed * moveList.size();

        for (int i = 0; i < moveList.size(); i++) {
            final int moveIndex = i;
            double offsetTime = totalDuration * (moveIndex / (double) moveList.size());

            animation.getKeyFrames().add(new KeyFrame(Duration.seconds(offsetTime), event -> {
                if (!moveList.isEmpty() && moveIndex < moveList.size()) {
                    int[] move = moveList.get(moveIndex);
                    moveDisk(move[0], move[1]);
                }
            }));
        }

        animation.setOnFinished(event -> {
            process.appendText("Simulation completed.\n");
            isAnimationRunning = false;
        });

        animation.play();
    }


    private void solveHanoi(int n, int from, int to, int aux) {
        if (n == 0) return;

        // Move n-1 disks from 'from' to 'aux' using 'to' as auxiliary
        solveHanoi(n - 1, from, aux, to);

        // Add the move to the moveList
        moveList.add(new int[]{from, to});  // Move from 'from' tower to 'to' tower

        // Move n-1 disks from 'aux' to 'to' using 'from' as auxiliary
        solveHanoi(n - 1, aux, to, from);

        // Update moves count dynamically
        movesCountLabel.setText(String.valueOf(moveList.size()));
    }

    private void moveDisk(int from, int to) {
        if (!towers[from].isEmpty()) {
            Rectangle disk = towers[from].pop();  // Remove disk from source tower
            towers[to].push(disk);  // Add disk to destination tower

            // Calculate new X and Y positions
            int centerX = getTowerXPosition(to) + TOWER_WIDTH / 2 - (int) (disk.getWidth() / 2);
            double newY = BASE_Y - DISK_HEIGHT * (towers[to].size());

            // Update disk position
            disk.setX(centerX);
            disk.setY(newY);

            // Check if this is the last move and stop the animation
            if (towers[to].size() == numOfDisks && from == 0 && to == 2) {
                process.appendText("Simulation completed.\n");
                isAnimationRunning = false;
                animation.stop();
            }
        }
    }

    private int getTowerXPosition(int towerIndex) {
        // Calculate the X position based on the number of towers
        return 210 + towerIndex * SPACE_BETWEEN_TOWERS;
    }

    private void resetGame() {
        towerPane.getChildren().clear();
        moveList.clear();

        if (movesArea != null) movesArea.clear();
        movesCountLabel.setText("0");
        numOfDisks = diskSpinner.getValue();

        // Stop animation and reset its state
        if (animation != null) {
            animation.stop();
            animation = null;
        }
        isAnimationRunning = false;

        // Initialize
        for (int i = 0; i < 3; i++) {
            int centerX = getTowerXPosition(i);
            Rectangle pole = new Rectangle(centerX, BOTTOM_Y, TOWER_WIDTH, 600);
            pole.setFill(Color.web("#905605"));
            towerPane.getChildren().add(pole);
            towers[i].clear();
        }

        // Add disks to Tower 1
        random = new Random(42);
        for (int i = numOfDisks; i > 0; i--) {
            double diskWidth = DISK_BASE_WIDTH * (i / (double) numOfDisks);
            diskWidth = Math.max(diskWidth, 11);

            double centerX = getTowerXPosition(0) + TOWER_WIDTH / 2 - diskWidth / 2;
            double yPosition = BASE_Y - DISK_HEIGHT * (numOfDisks - i + 1);

            Rectangle disk = new Rectangle(centerX, yPosition, diskWidth, DISK_HEIGHT);
            disk.setFill(Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble()));
            towers[0].push(disk);
            towerPane.getChildren().add(disk);
        }
        runButton.setDisable(false);

        // Add labels for towers
        String[] labelTexts = {"A", "B", "C"};
        for (int i = 0; i < 3; i++) {
            Label label = new Label(labelTexts[i]);
            label.setLayoutX(getTowerXPosition(i) + TOWER_WIDTH / 2 - 5);
            label.setLayoutY(BOTTOM_Y + -50);
            label.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: black;" );
            towerPane.getChildren().add(label);
        }

    }
}
