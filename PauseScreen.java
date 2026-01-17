import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import Audio.Sound;

public class PauseScreen extends StackPane {

    private Sound music;
    private Sound effects;

    public PauseScreen(String iconPath, Sound music, Sound effects, Runnable onMainMenu) {
        this.music = music;
        this.effects = effects;

        setVisible(false);
        setPickOnBounds(true);

        // Dim background
        Rectangle dim = new Rectangle();
        dim.setStyle("-fx-fill: rgba(0,0,0,0.55);");
        dim.widthProperty().bind(widthProperty());
        dim.heightProperty().bind(heightProperty());

        // Main layout
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);

        // Pause icon
        ImageView icon = new ImageView();
        try {
            Image img = new Image(iconPath);
            icon.setImage(img);
            icon.setPreserveRatio(true);
            icon.setFitWidth(160);
        } catch (Exception e) {
            System.out.println("Icon not found");
        }

        // Music slider
        Label musicLabel = new Label("Music Volume");
        musicLabel.setTextFill(Color.WHITE);
        musicLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 18));

        Slider musicSlider = new Slider(0, 100, 75);
        musicSlider.setMaxWidth(300);
        musicSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateVolume(this.music, newVal.floatValue());
        });

        // Effects slider
        Label effectsLabel = new Label("Effects Volume");
        effectsLabel.setTextFill(Color.WHITE);
        effectsLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 18));

        Slider effectsSlider = new Slider(0, 100, 75);
        effectsSlider.setMaxWidth(300);
        effectsSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateVolume(this.effects, newVal.floatValue());
        });

        // Main menu button
        Button mainMenuButton = new Button("Main Menu");
        mainMenuButton.setStyle(
            "-fx-background-color: white; " +
            "-fx-text-fill: black; " +
            "-fx-font-family: 'Comic Sans MS'; " +
            "-fx-font-size: 20px; " +
            "-fx-background-radius: 20; " + // Round button
            "-fx-padding: 8 20 8 20;"
        );
        
        mainMenuButton.setOnAction(e -> {
            this.hide();
            onMainMenu.run();
        });

        // Add everything to the layout
        layout.getChildren().addAll(icon, musicLabel, musicSlider, effectsLabel, effectsSlider, mainMenuButton);

        getChildren().addAll(dim, layout);
    }

    // Method to convert
    private void updateVolume(Sound soundObject, float percentage) {
        if (soundObject == null) return;

        float range = 86.0f;
        float dbVolume = -80.0f + (percentage / 100.0f) * range;

        if (percentage <= 0) dbVolume = -80.0f;
        if (percentage >= 100) dbVolume = 6.0f;

        soundObject.setVolume(dbVolume);
    }

    public void show() { setVisible(true); }
    public void hide() { setVisible(false); }
}
