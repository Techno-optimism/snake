import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GameSizeScreen extends StackPane {
    public GameSizeScreen(Runnable onSelectTiny, Runnable onSelectSmall, Runnable onSelectMedium, Runnable onSelectLarge) {
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
        Label selectDifficultyLabel = new Label("Select Size");
        selectDifficultyLabel.setStyle("-fx-font-size: 36px; -fx-text-fill: white; -fx-font-weight: bold;");

        // Tiny button
        Button tinyButton = new Button("Tiny (7x7)");
        tinyButton.setOnAction(e -> {
            this.hide();
            onSelectTiny.run();
        });

        // Small button
        Button smallButton = new Button("Small (10x10)");
        smallButton.setOnAction(e -> {
            this.hide();
            onSelectSmall.run();
        });

        // Medium button
        Button mediumButton = new Button("Medium (15x15)");
        mediumButton.setOnAction(e -> {
            this.hide();
            onSelectMedium.run();
        });

        // Large button
        Button largeButton = new Button("Large (20x20)");
        largeButton.setOnAction(e -> {
            this.hide();
            onSelectLarge.run();
        });

        menu.getChildren().addAll(selectDifficultyLabel, tinyButton, smallButton, mediumButton, largeButton);

        // Add to StackPane, so menu is on top of overlay
        this.getChildren().addAll(overlay, menu);

        this.setVisible(true); // Visible by default to select size at start
    }

    public void show() {
        this.setVisible(true);
        this.toFront();
    }

    public void hide() {
        this.setVisible(false);
    }  
}
