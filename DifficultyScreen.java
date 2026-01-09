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

public class DifficultyScreen extends StackPane {
    private Label selectDifficultyLabel;
    private VBox menuLayout;
    private ImagePattern island_bg;

    public DifficultyScreen(Runnable onSelectEasy, Runnable onSelectMedium, Runnable onSelectHard, Runnable onSelectWormFood, Runnable onBack) {
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
        selectDifficultyLabel = new Label("Select Difficulty");
        selectDifficultyLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 50));
        selectDifficultyLabel.setTextFill(Color.web("#ffffffff"));
        selectDifficultyLabel.setTranslateY(-150);

        // Easy button
        Button easyButton = createCustomButton("Easy");
        easyButton.setOnAction(e -> {
            this.hide();
            onSelectEasy.run();
        });

        // Medium button
        Button mediumButton = createCustomButton("Medium");
        mediumButton.setOnAction(e -> {
            this.hide();
            onSelectMedium.run();
        });

        // Hard button
        Button hardButton = createCustomButton("Hard");
        hardButton.setOnAction(e -> {
            this.hide();
            onSelectHard.run();
        });

        // Worm Food button
        Button wormFoodButton = createCustomButton("Worm Food");
        wormFoodButton.setOnAction(e -> {
            this.hide();
            onSelectWormFood.run();
        });

        // Back button
        Button backButton = createCustomButton("Back");
        backButton.setOnAction(e -> {
            this.hide();
            onBack.run();
        });

        menuLayout.getChildren().addAll(easyButton, mediumButton, hardButton, wormFoodButton, backButton);

        // Add to StackPane, so menu is on top of overlay
        this.getChildren().addAll(menuLayout, selectDifficultyLabel);

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
