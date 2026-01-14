import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javafx.beans.value.ObservableValue;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import javafx.scene.paint.ImagePattern;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.ScaleTransition;
import Audio.Sound;
import java.io.File;

public class SettingsScreen extends StackPane {
    
    private Label selectSizeLabel;
    private VBox menuLayout;
    private ImagePattern island_bg;

    public SettingsScreen(Runnable onSelectTiny, Runnable onBack) {
        // Load a background image; if missing use a solid color background
        Image bgImage = new Image("file:resources/main_menu_island.png", false);
        if (!bgImage.isError()) {
            ImageView bg = new ImageView(bgImage);
            bg.setPreserveRatio(false);
            bg.setSmooth(true);
            bg.setCache(true);
            bg.fitWidthProperty().bind(this.widthProperty());
            bg.fitHeightProperty().bind(this.heightProperty());
            bg.setMouseTransparent(true);
            this.getChildren().add(0, bg);
        } else {
            // fallback background color
            Rectangle fallback = new Rectangle();
            fallback.setFill(Color.web("#3b2e22"));
            fallback.widthProperty().bind(this.widthProperty());
            fallback.heightProperty().bind(this.heightProperty());
            this.getChildren().add(0, fallback);
        }

        try {
            island_bg    = new ImagePattern(new Image("file:resources/main_menu_island.png", 512, 512, true, false));
        } catch (Exception e) {
            System.out.println("Cant find images");
        }

        // // Overlay
        // Rectangle overlay = new Rectangle();
        // overlay.setFill(Color.BLACK);
        // overlay.setOpacity(0.8);

        // // Binds the overlay size to the StackPane size
        // overlay.widthProperty().bind(this.widthProperty());
        // overlay.heightProperty().bind(this.heightProperty());


        // Menu which will hold the buttons and label
        menuLayout = new VBox(7);
        menuLayout.setAlignment(Pos.CENTER);
        menuLayout.setTranslateY(30);

        // UI elements
        selectSizeLabel = new Label("Settings");
        selectSizeLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 50));
        selectSizeLabel.setTextFill(Color.web("#ffffffff"));
        selectSizeLabel.setTranslateY(-150);

        // Tiny button

        Label backgroundSoundLabel = new Label("Background Volume: " + -5);

        Slider backgroundSoundSlider = new Slider(-80, 6, 1);
        backgroundSoundSlider.setMin(-40);
        backgroundSoundSlider.setMax(6);
        backgroundSoundSlider.setValue(-5);
        backgroundSoundSlider.setPrefHeight(5);
        backgroundSoundSlider.setPrefWidth(40);

        backgroundSoundSlider.valueProperty().addListener(

            new ChangeListener<Number>() {

                public void changed(ObservableValue <? extends Number> observable, Number oldValue, Number newValue) {

                    backgroundSoundLabel.setText("Background Volume: " + newValue);
                }
            }
        );
        

        Button tinyButton = createCustomButton("Tiny (7x7)");
        tinyButton.setOnAction(e -> {
            this.hide();
            onSelectTiny.run();
        });

        // Back button
        Button backButton = createCustomButton("Back");
        backButton.setOnAction(e -> {
            this.hide();
            onBack.run();
        });

        menuLayout.getChildren().addAll(backgroundSoundLabel, backgroundSoundSlider, backButton);

        // Add to StackPane, so menu is on top of overlay
        this.getChildren().addAll(menuLayout, selectSizeLabel);

        this.setVisible(false); // Invisible by default
    }

    private Button createCustomButton(String text) {
        Button btn = new Button(text);

        String baseStyle =
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #ffffffff;" +
            "-fx-font-family: 'Comic Sans MS';" +
            "-fx-font-size: 28px;" +
            "-fx-padding: 6 24 6 24;";

        String hoverStyle =
            "-fx-background-color: transparent;" +
            "-fx-text-fill: yellow;" +
            "-fx-font-family: 'Comic Sans MS';" +
            "-fx-font-size: 28px;" +
            "-fx-padding: 6 24 6 24;" +
            "-fx-cursor: hand;";

        btn.setStyle(baseStyle);
        btn.setFocusTraversable(false);
        btn.setPrefWidth(340);

        ScaleTransition enterScale = new ScaleTransition(Duration.millis(250), btn);
        enterScale.setToX(1.25);
        enterScale.setToY(1.25);

        ScaleTransition exitScale = new ScaleTransition(Duration.millis(250), btn);
        exitScale.setToX(1.0);
        exitScale.setToY(1.0);

        btn.setOnMouseEntered(e -> {
            btn.setStyle(hoverStyle);
            exitScale.stop();
            enterScale.playFromStart();
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle(baseStyle);
            enterScale.stop();
            exitScale.playFromStart();
        });

        return btn;
    }

    public void show() {
        this.setVisible(true);
        this.toFront();
    }

    public void hide() {
        this.setVisible(false);
    }
    
}
