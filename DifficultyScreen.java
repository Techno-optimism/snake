import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class DifficultyScreen extends StackPane {
    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }

    public DifficultyScreen(Runnable onSelectEasy, Runnable onSelectMedium, Runnable onSelectHard, Runnable onSelectWormFood) {
        // Overlay
        Rectangle overlay = new Rectangle();
        overlay.setFill(Color.BLACK);
        overlay.setOpacity(0.8);

        // Binds the overlay size to the StackPane size
        overlay.widthProperty().bind(this.widthProperty());
        overlay.heightProperty().bind(this.heightProperty());

        // Menu which will hold the buttons and label
        VBox menu = new VBox(20);
        menu.setAlignment(Pos.CENTER);

        // UI elements
        Label selectDifficultyLabel = new Label("Select Difficulty");
        selectDifficultyLabel.setStyle("-fx-font-size: 36px; -fx-text-fill: white; -fx-font-weight: bold;");

        // Easy button
        Button easyButton = new Button("Easy");
        easyButton.setOnAction(e -> {
            this.hide();
            onSelectEasy.run();
        });

        // Medium button
        Button mediumButton = new Button("Medium");
        mediumButton.setOnAction(e -> {
            this.hide();
            onSelectMedium.run();
        });

        // Hard button
        Button hardButton = new Button("Hard");
        hardButton.setOnAction(e -> {
            this.hide();
            onSelectHard.run();
        });

        // Worm Food button
        Button wormFoodButton = new Button("Worm Food");
        wormFoodButton.setOnAction(e -> {
            this.hide();
            onSelectWormFood.run();
        });

        menu.getChildren().addAll(selectDifficultyLabel, easyButton, mediumButton, hardButton, wormFoodButton);

        // Add to StackPane, so menu is on top of overlay
        this.getChildren().addAll(overlay, menu);
        this.setVisible(false); // Hidden by default

    }

    public void show() {
        this.setVisible(true);
        this.toFront();
    }

    public void hide() {
        this.setVisible(false);
    }
}
