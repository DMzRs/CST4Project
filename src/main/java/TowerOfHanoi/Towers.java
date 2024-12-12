package TowerOfHanoi;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Stack;

public class Towers extends Application {

    @FXML
    private Label titleName;

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
    private static final int BASE_Y = 400; // Base Y-coordinate for disks
    private static final int DISK_HEIGHT = 18; // Height of each disk
    private static final int DISK_BASE_WIDTH = 100; // Base width of the largest disk
    private static final int TOWER_WIDTH = 10; // Width of each tower
    private static final int SPACE_BETWEEN_TOWERS = 190; // Spacing between towers
    private static final int OFFSET_X = 150; // Horizontal offset for tower alignment
    private static final int BOTTOM_Y = 200; // Y-coordinate for the bottom of the towers (above the VBox)

    private Stack<Rectangle>[] towers = new Stack[3]; // Array of stacks representing the towers
    private int numOfDisks = 3; // Default number of disks
    private ArrayList<int[]> moveList = new ArrayList<>(); // Stores the sequence of moves
    private Timeline animation; // Animation for the tower movements
    private double animationSpeed = 1.0; // Default speed for animation



    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML layout for the Tower of Hanoi game
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/project/automatatheoryproject/TowerOfHanoiPage.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);

            // Set up the stage (window)
            primaryStage.setTitle("Tower of Hanoi");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize(){
        // Initialize towers as stacks
        for (int i = 0; i < 3; i++) {
            towers[i] = new Stack<>();
        }

        // Set up the game with the default state
        resetGame();

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(3, 12, 3);
        diskSpinner.setValueFactory(valueFactory);  // Set the spinner value factory with the correct range
    }

    @FXML
    private void handleRun() {
        // Check if the disks are already in the final state (all disks on Tower C)
        if (towers[0].isEmpty() && towers[1].isEmpty() && towers[2].size() == numOfDisks) {
            process.appendText("Can't run animation since it has already reached the final state.\n");
            return; // Prevent further execution
        }

        // Prevent running the animation if it is already running
        if (isAnimationRunning) {
            process.appendText("Animation is already running...\n");
            return; // Prevent duplicate execution
        }

        process.appendText("Starting simulation...\n");
        isAnimationRunning = true;

        moveList.clear();
        movesCountLabel.setText("0"); // Reset moves count
        numOfDisks = diskSpinner.getValue(); // Use the number of disks selected by the user
        solveHanoi(numOfDisks, 0, 2, 1);
        playAnimation();
    }

    @FXML
    private void handlePause() {
        if (animation != null && animation.getStatus() == Animation.Status.RUNNING) {
            process.appendText("Pausing animation...\n");
            animation.pause();
            isAnimationRunning = false; // Allow "Run" to be accessible
        } else if (animation != null && animation.getStatus() == Animation.Status.PAUSED) {
            process.appendText("Resuming animation...\n");
            animation.play();
            isAnimationRunning = true; // Prevent "Run" while resuming
        } else {
            process.appendText("No animation to pause or resume.\n");
        }
    }

    @FXML
    private void handleShowMoves() {
        // Ensure that movesArea is initialized before use
        if (movesArea == null) {
            movesArea = new TextArea();
            movesArea.setEditable(false);
            movesArea.setWrapText(true);

            // Set the preferred height for the TextArea
            movesArea.setPrefHeight(300); // Adjust height as needed
        }

        if (moveList.isEmpty()) {
            // If no moves have been calculated, show a message
            movesArea.setText("No moves to display. Please click 'Run' to calculate the moves.");
        } else {
            // Populate TextArea with moves
            StringBuilder moves = new StringBuilder();
            for (int i = 0; i < moveList.size(); i++) {
                int[] move = moveList.get(i);
                moves.append("Move ").append(i + 1).append(": Tower ").append(move[0] + 1)
                        .append(" -> Tower ").append(move[1] + 1).append("\n");
            }
            movesArea.setText(moves.toString());
        }

        // Create a new Stage (window) to display the moves
        Stage movesStage = new Stage();
        movesStage.setTitle("Transition Moves");

        // Create a VBox layout for the moves display
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 15; -fx-background-color: #f9f9f9;");

        // Add a Label
        Label titleLabel = new Label("List of Moves:");
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        // Add the TextArea directly to the layout
        layout.getChildren().addAll(titleLabel, movesArea);

        // Set the scene for the new stage
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
        animationSpeed = 2.0; // Slow speed
        if (animation != null) animation.setRate(0.5); // Adjust animation rate dynamically
    }

    @FXML
    private void handleFast() {
        animationSpeed = 0.5; // Fast speed
        if (animation != null) animation.setRate(2.0); // Adjust animation rate dynamically
    }

    @FXML
    public void handleSkip(ActionEvent actionEvent) {
        if (moveList.isEmpty()) {
            process.appendText("No moves to skip. Please run the simulation first.\n");
            return;
        }

        // Stop the animation if it is currently running
        if (animation != null) {
            animation.stop(); // Stop any ongoing animation
            isAnimationRunning = false; // Reset the animation state
        }

        // Check if the final state has already been reached (i.e., all disks are on Tower C)
        if (towers[0].isEmpty() && towers[1].isEmpty()) {
            process.appendText("Can't run animation since it has already reached the final state.\n");
            return;
        }

        // Clear the remaining disks from Tower A and Tower B and remove them from the display
        while (!towers[0].isEmpty()) {
            Rectangle disk = towers[0].pop();
            towerPane.getChildren().remove(disk); // Remove the disk from the pane
        }
        while (!towers[1].isEmpty()) {
            Rectangle disk = towers[1].pop();
            towerPane.getChildren().remove(disk); // Remove the disk from the pane
        }

        // Remove all disks from Tower C before placing them in the correct order
        while (!towers[2].isEmpty()) {
            Rectangle disk = towers[2].pop();
            towerPane.getChildren().remove(disk); // Remove the disk from the pane
        }

        // Move all disks to Tower C in the correct order (smallest on top, largest at the bottom)
        for (int i = numOfDisks; i > 0; i--) {
            double diskWidth = DISK_BASE_WIDTH * (i / (double) numOfDisks);
            diskWidth = Math.max(diskWidth, 11);

            // Create a disk and place it on Tower C
            double centerX = getTowerXPosition(2) + TOWER_WIDTH / 2 - diskWidth / 2;
            double yPosition = BASE_Y - DISK_HEIGHT * (numOfDisks - i + 1);

            Rectangle disk = new Rectangle(centerX, yPosition, diskWidth, DISK_HEIGHT);
            disk.setFill(Color.color(Math.random(), Math.random(), Math.random()));

            // Place the disk on Tower C
            towers[2].push(disk);
            towerPane.getChildren().add(disk);
        }

        // Print a message indicating the simulation has been skipped
        process.appendText("Simulation skipped. All disks are now on Tower C in correct order.\n");

        // Clear the moveList to indicate no further moves
        isAnimationRunning = false;  // Ensure the animation state is reset
        runButton.setDisable(true);
    }


    private void playAnimation() {
        if (animation != null) {
            animation.stop(); // Stop any previous animation
        }

        // Reset the previous animation
        animation = new Timeline();
        animation.setCycleCount(1);  // Play the animation once
        animation.getKeyFrames().clear(); // Clear any old keyframes

        // Calculate the total time for the entire animation based on the number of moves
        double totalDuration = animationSpeed * moveList.size();  // Adjust animation speed here if needed

        // Create new keyframes for each move with adjusted animation speed
        for (int i = 0; i < moveList.size(); i++) {
            final int moveIndex = i;
            // Calculate a time-based offset to spread the animation evenly across the moves
            double offsetTime = totalDuration * (moveIndex / (double) moveList.size());

            animation.getKeyFrames().add(new KeyFrame(Duration.seconds(offsetTime), event -> {
                if (!moveList.isEmpty() && moveIndex < moveList.size()) {
                    int[] move = moveList.get(moveIndex);
                    moveDisk(move[0], move[1]);  // Correctly animate each disk move
                }
            }));
        }

        // Handle when animation finishes
        animation.setOnFinished(event -> {
            process.appendText("Simulation completed.\n");
            isAnimationRunning = false; // Reset running state
        });

        animation.play();  // Start the animation
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
                // All disks have reached the goal tower
                process.appendText("Simulation completed.\n");
                isAnimationRunning = false;
                animation.stop();
            }
        }
    }

    private int getTowerXPosition(int towerIndex) {
        // Calculate the X position based on the number of towers and the spacing
        return 210 + towerIndex * SPACE_BETWEEN_TOWERS;
    }

    private void resetGame() {
        towerPane.getChildren().clear();  // Clear all graphical elements
        moveList.clear();  // Clear the move sequence

        if (movesArea != null) movesArea.clear();
        movesCountLabel.setText("0");
        numOfDisks = diskSpinner.getValue();

        // Stop animation and reset its state
        if (animation != null) {
            animation.stop();
            animation = null;
        }
        isAnimationRunning = false;

        // Initialize towers
        for (int i = 0; i < 3; i++) {
            int centerX = getTowerXPosition(i);
            Rectangle pole = new Rectangle(centerX, BOTTOM_Y, TOWER_WIDTH, 600);
            pole.setFill(Color.web("#905605"));
            towerPane.getChildren().add(pole);
            towers[i].clear();
        }

        // Add disks to Tower 1
        for (int i = numOfDisks; i > 0; i--) {
            double diskWidth = DISK_BASE_WIDTH * (i / (double) numOfDisks);
            diskWidth = Math.max(diskWidth, 11);

            double centerX = getTowerXPosition(0) + TOWER_WIDTH / 2 - diskWidth / 2;
            double yPosition = BASE_Y - DISK_HEIGHT * (numOfDisks - i + 1);

            Rectangle disk = new Rectangle(centerX, yPosition, diskWidth, DISK_HEIGHT);
            disk.setFill(Color.color(Math.random(), Math.random(), Math.random()));
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

    public static void main(String[] args) {
        launch(args);  // Launch the JavaFX application
    }
}
