import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import javafx.scene.paint.ImagePattern;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class StatsScreen extends StackPane {
    private Label totalGamesLabel;
    private Label classicHighLabel;
    private Label timedHighLabel;
    private ImagePattern island_bg;

    public StatsScreen(Runnable onBack) {

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

        // Title label
        Label titleLabel = new Label("Stats");
        titleLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 50));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setTranslateY(-150);

        // Stats labels
        totalGamesLabel = createStatLabel("Total Games: 0");
        classicHighLabel = createStatLabel("Classic Best: 0");
        timedHighLabel = createStatLabel("Timed Best: 0");

        // Back button
        Button backButton = createCustomButton("Back");
        backButton.setOnAction(e -> {
            this.hide();
            onBack.run();
        });

        // Layout container
        VBox menuLayout = new VBox(20);
        menuLayout.setAlignment(Pos.CENTER);
        menuLayout.setTranslateY(20);
        menuLayout.getChildren().addAll(totalGamesLabel, classicHighLabel, timedHighLabel, backButton);

        // Everything added to the StackPane
        this.getChildren().addAll(titleLabel, menuLayout);
        this.setVisible(false);
    }

    public void updateData(int totalGames, int classicHigh, int timedHigh) {
        totalGamesLabel.setText("Total Games: " + totalGames);
        classicHighLabel.setText("Classic Best: " + classicHigh);
        timedHighLabel.setText("Timed Best: " + timedHigh);
    }

    private Label createStatLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Comic Sans MS", 28));
        lbl.setTextFill(Color.WHITE);
        return lbl;
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
