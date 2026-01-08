import java.awt.TextField;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class SimpleGameSizeScreen extends StackPane {
    public SimpleGameSizeScreen(Runnable onSelectTiny, Runnable onSelectSmall, Runnable onSelectMedium, Runnable onSelectLarge) {
        
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

        // Horizontal size for grid
        TextField horizontalSize = new TextField("Horizontal size");

        // setOnAction(e -> {
        //     this.hide();
        //     onSelectTiny.run();
        // });

        // Vertical size for grid
        TextField verticalSize = new TextField("Vertical size");

        // smallButton.setOnAction(e -> {
        //     this.hide();
        //     onSelectSmall.run();
        // });

        // Medium button
        Button mediumButton = new Button("Submit size");
        mediumButton.setOnAction(e -> {
            String horizontal = horizontalSize.getText();
            int hor = Integer.parseInt(horizontal);
            String vertical = verticalSize.getText();
            int ver = Integer.parseInt(vertical);
            this.hide();
            onSelectMedium.run();
        });

        menu.getChildren().addAll(selectDifficultyLabel, mediumButton);

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
