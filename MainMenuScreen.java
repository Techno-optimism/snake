import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

public class MainMenuScreen extends StackPane {
    private Label title;
    private VBox menuLayout;
    private Rectangle overlay;
    private ImagePattern island_bg;

    Sound sound = new Sound();

    public MainMenuScreen(Runnable onSelectClassic, Runnable onSelectTimed, Runnable onSelectStats, Runnable onSelectSettings) {
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

        // ImageView titleImage = new ImageView();
        // try {
        //     titleImage.setImage(new Image("file:resources/snake_title.png"));
        //     titleImage.setPreserveRatio(true);
        //     titleImage.setFitWidth(400);
        //     titleImage.setPreserveRatio(true);
        //     titleImage.setSmooth(false);
        //     getChildren().add(titleImage);
        // } catch (Exception e) {
        //     Label title = new Label("SNAKE");
        //     title.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        //     getChildren().add(title);
        // }

        try {
            island_bg    = new ImagePattern(new Image("file:resources/main_menu_island.png", 512, 512, true, false));
        } catch (Exception e) {
            System.out.println("Cant find images");
        }

        // Overlay (covers the whole menu) - start transparent and bind to this pane
        overlay = new Rectangle();
        overlay.setFill(Color.TRANSPARENT);
        overlay.setOpacity(0.0);
        overlay.widthProperty().bind(this.widthProperty());
        overlay.heightProperty().bind(this.heightProperty());
        overlay.setMouseTransparent(true);

        // // When showing
        // FadeTransition fadeInOverlay = new FadeTransition(Duration.seconds(0.5), overlay);
        // fadeInOverlay.setToValue(0.25);
        // fadeInOverlay.play();

        // Menu which will hold the buttons and label
        VBox menu = new VBox(20);
        menu.setAlignment(Pos.CENTER);

        title = new Label("SNAKE!");
        title.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 80));
        title.setTextFill(Color.rgb(105, 199, 22, 1));
        title.setTranslateY(-250);

        // Glow effect
        DropShadow glow = new DropShadow();
        glow.setColor(Color.WHITE);
        glow.setRadius(25);
        glow.setSpread(0.1);
        glow.setBlurType(BlurType.GAUSSIAN);
        title.setEffect(glow);
        title.setOpacity(0);

        // Custom buttons
        Button classicButton = createCustomButton("Classic");
        classicButton.setOnAction(e -> {
            this.hide();
            onSelectClassic.run();
        });

        Button timedButton = createCustomButton("Timed");
        timedButton.setOnAction(e -> {
            this.hide();
            onSelectTimed.run();
        });

        Button statsButton = createCustomButton("Stats");
        statsButton.setOnAction(e -> {
            this.hide();
            onSelectStats.run();
        });

        Button settingsButton = createCustomButton("Settings");
        settingsButton.setOnAction(e -> {
            this.hide();
            onSelectSettings.run();
        });

        Button quitButton = createCustomButton("Quit");
        quitButton.setOnAction(e -> System.exit(0));

        // Menu UI
        menuLayout = new VBox(20);
        menuLayout.setAlignment(Pos.CENTER);
        menuLayout.getChildren().addAll(classicButton, timedButton, statsButton, settingsButton, quitButton);
        menuLayout.setTranslateY(100);
        menuLayout.setOpacity(0); // Start invisible to fade in on show

        // Stack
        this.getChildren().addAll(title, menuLayout);
        this.setVisible(true);

        // Play background music
        playMusic(0);
    }

    private Button createCustomButton(String text) {
        Button btn = new Button(text);

        String baseStyle =
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #ffffffff;" +
            "-fx-font-family: 'Comic Sans MS';" +
            "-fx-font-size: 42px;" +
            "-fx-padding: 6 24 6 24;";

        String hoverStyle =
            "-fx-background-color: transparent;" +
            "-fx-text-fill: yellow;" +
            "-fx-font-family: 'Comic Sans MS';" +
            "-fx-font-size: 42px;" +
            "-fx-padding: 6 24 6 24;" +
            "-fx-cursor: hand;";

        btn.setStyle(baseStyle);
        btn.setFocusTraversable(false);
        btn.setPrefWidth(240);

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

    public void playMusic(int i) {

        sound.setFile(i);
        sound.play();
        sound.loop();
    }
    public void stopMusic() {

        sound.stop();
    
    }
}
