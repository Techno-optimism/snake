import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class GameOverScreen extends StackPane {

    private Label title;
    private VBox menuLayout;
    private Rectangle overlay;

    // Runnable is an interface that has a single method run()
    // This passes two actions to be performed on button clicks
    // The contructor takes two Runnables: one for restart and one for quit
    public GameOverScreen(Runnable onRestart, Runnable onMainMenu, Runnable onQuit) {
        // Overlay
        overlay = new Rectangle();
        overlay.widthProperty().bind(this.widthProperty());
        overlay.heightProperty().bind(this.heightProperty());
        overlay.setFill(Color.BLACK);
        overlay.setOpacity(0.0); // Start invisible to fade in on show

        title = new Label("YOU DIED");
        title.setFont(Font.font("Times New Roman", FontWeight.BOLD, 80));
        title.setTextFill(Color.rgb(180, 20, 20)); // Dark red color
        title.setTranslateY(-110);

       // Glow effect
        DropShadow glow = new DropShadow();
        glow.setColor(Color.RED);
        glow.setRadius(20);
        glow.setSpread(0.1);
        glow.setBlurType(BlurType.GAUSSIAN);
        title.setEffect(glow);
        title.setOpacity(0); // Start invisible to fade in on show

        // Custom buttons
        Button restartButton = createCustomButton("Restart");
        restartButton.setOnAction(e -> {
            this.hide();
            onRestart.run();
        });

        Button mainMenuButton = createCustomButton("Main Menu");
        mainMenuButton.setOnAction(e -> {
            this.hide();
            onMainMenu.run();
        });

        Button quitButton = createCustomButton("Give Up");
        quitButton.setOnAction(e -> onQuit.run());

       menuLayout = new VBox(30);
       menuLayout.setAlignment(Pos.CENTER);
       menuLayout.getChildren().addAll(restartButton, mainMenuButton, quitButton);
       menuLayout.setTranslateY(100);
       menuLayout.setOpacity(0); // Start invisible to fade in on show

        // Stack
        this.getChildren().addAll(
            overlay, 
            title, 
            menuLayout);
        this.setVisible(false); // Hidden by default so we don't see it at game start
    }

    private Button createCustomButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #cccccc;" +
            "-fx-font-family: 'Times New Roman';" +
            "-fx-font-size: 24px;" +
            "-fx-border-color: transparent transparent #555555 transparent;" +
            "-fx-border-width: 1px;"
        );

        // Hover effects
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: white;" +
            "-fx-font-family: 'Times New Roman';" +
            "-fx-font-size: 24px;" +
            "-fx-border-color: transparent transparent white transparent;" +
            "-fx-border-width: 1px;" +
            "-fx-cursor: hand;"
        ));

        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #cccccc;" +
            "-fx-font-family: 'Times New Roman';" +
            "-fx-font-size: 24px;" +
            "-fx-border-color: transparent transparent #555555 transparent;" +
            "-fx-border-width: 1px;"
        ));
        return btn;
    }

    public void show() {
        this.setVisible(true);
        this.toFront();

        // Reset the animation
        overlay.setOpacity(0);
        title.setOpacity(0);
        title.setScaleX(1.2);
        title.setScaleY(1.2);
        menuLayout.setOpacity(0);

        // Fade in black background
        FadeTransition fadeInOverlay = new FadeTransition(Duration.seconds(0.5), overlay);
        fadeInOverlay.setToValue(0.8);
        fadeInOverlay.play();

        // Fade in title with scale effect
        FadeTransition fadeInTitle = new FadeTransition(Duration.seconds(2), title);
        fadeInTitle.setToValue(1.0);
        fadeInTitle.play();

        // Show buttons after a delay
        FadeTransition fadeInMenu = new FadeTransition(Duration.seconds(1), menuLayout);
        fadeInMenu.setDelay(Duration.seconds(2));
        fadeInMenu.setToValue(1.0);
        fadeInMenu.play();
    }

    public void hide() {
        this.setVisible(false);
    }
}