import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.sound.sampled.FloatControl;

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

    public SettingsScreen(Runnable onBack, Sound music, Sound effects) {
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
        selectSizeLabel.setTranslateY(-110);

        // Background music volume slider
        Label backgroundMusicLabel = new Label("Background Music: 75");
        backgroundMusicLabel.setTextFill(Color.WHITE);
        backgroundMusicLabel.setFont(Font.font("Comic Sans MS", 28));

        Slider backgroundMusicSlider = new Slider(0, 100, 75);
        backgroundMusicSlider.setMaxHeight(20);
        backgroundMusicSlider.setMaxWidth(400);

        backgroundMusicSlider.valueProperty().addListener(
            (observable, oldValue, newValue) -> {
                float percentage = newValue.floatValue();

                // 50%
                backgroundMusicLabel.setText("Background Music: " + (int)percentage);

                // 0 - 100 range
                float range = 86.0f;
                float dbVolume = -80.0f + (percentage / 100.0f) * range;

                // 0% is minimum
                if (percentage <= 0) {
                    dbVolume = -80.0f;
                }

                // 100% is max
                if (percentage >= 100) {
                    dbVolume = 6.0f;
                }

                music.setVolume(dbVolume);
            }
        );

        // Effects volume slider
        Label effectsLabel = new Label("Effects: 75");
        effectsLabel.setTextFill(Color.WHITE);
        effectsLabel.setFont(Font.font("Comic Sans MS", 28));

        Slider effectsSlider = new Slider(0, 100, 75);
        effectsSlider.setMaxHeight(20);
        effectsSlider.setMaxWidth(400);

        effectsSlider.valueProperty().addListener(
            (observable, oldValue, newValue) -> {
                float percentage = newValue.floatValue();

                // 50%
                effectsLabel.setText("Effects: " + (int)percentage);

                // 0 - 100 range
                float range = 86.0f;
                float dbVolume = -80.0f + (percentage / 100.0f) * range;

                // 0% is minimum
                if (percentage <= 0) {
                    dbVolume = -80.0f;
                }

                // 100% is max
                if (percentage >= 100) {
                    dbVolume = 6.0f;
                }

                effects.setVolume(dbVolume);
            }
        );

        
        // Back button
        Button backButton = createCustomButton("Back");
        backButton.setOnAction(e -> {
            this.hide();
            onBack.run();
        });

        menuLayout.getChildren().addAll(backgroundMusicLabel, backgroundMusicSlider, effectsLabel, effectsSlider, backButton);

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
