import javafx.application.Application;
import javafx.scene.Scene;
// import javafx.scene.control.skin.TextInputControlSkin.Direction;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.awt.Point;
import java.util.ArrayDeque;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;


public class SimpleGrid extends Application {

    private Timeline loop;
    private SnakeGame game;
    private Rectangle[][] cells;
    private GameOverScreen gameOverScreen;
    private SimpleGameSizeScreen gameSizeScreen;
    private GridPane gameGrid;


    private int rows;
    private int columns;

    @Override
    public void start(Stage stage) {
        BorderPane mainLayout = new BorderPane();

        // Game area (stackpane)
        StackPane gameArea = new StackPane();

        gameGrid = new GridPane();
        gameGrid.setAlignment(Pos.CENTER);

        // Add the grid to the game area; screens are added after they're constructed
        gameArea.getChildren().add(gameGrid);

        // Assemble BorderPane
        mainLayout.setCenter(gameArea);

        // Create scene and set up stage
        Scene scene = new Scene(mainLayout, 1000, 1050);

        scene.setOnKeyPressed(event -> {
            if (game == null) return;

            switch (event.getCode()) {
                case UP -> game.setDirection(SnakeGame.Direction.UP);
                case DOWN -> game.setDirection(SnakeGame.Direction.DOWN);
                case LEFT -> game.setDirection(SnakeGame.Direction.LEFT);
                case RIGHT -> game.setDirection(SnakeGame.Direction.RIGHT);
                default -> { }
            }
        });

        gameOverScreen = new GameOverScreen(
            // Must have exactly two actions, since the constructor takes two Runnables

            // On restart click
            () -> {
                game.reset();
                gameSizeScreen.show();
            },
            // On quit click
            () -> System.exit(0)
        );

        gameSizeScreen = new SimpleGameSizeScreen(
            () -> {
                buildGrid(7, 7);
            },
            () -> {
                buildGrid(10, 10);
            }, 
            () -> {
                buildGrid(15, 15);
            },
            () -> {
                buildGrid(20, 20);
            }
        );

        stage.setScene(scene);
        stage.setTitle("Snake Game");
        stage.setResizable(false);

        // The screens are added to the game area
        gameArea.getChildren().addAll(gameOverScreen, gameSizeScreen);

        // Show the first screen
        gameSizeScreen.show();
        stage.show();
    }

    // #1b5e20 (snake color)
    // #ff5555 (bright food color)
    // #b71c1c (darker food color)
    // #c0a060 (grid color)

    // Used to clear the grid and draw the game state onto the grid each tick
    private void draw(SnakeGame game, Rectangle[][] cells, int n, int m) {
        // Clear all cells to background color
        for (int col = 0; col < n; col++) {
            for (int row = 0; row < m; row++) {
                cells[col][row].setFill(Color.web("#f4c064"));
                cells[col][row].setStroke(Color.web("#c0a060"));  
            }
        }

        // Draw snake
        for (Point p : game.getSnake()) {
            cells[p.x][p.y].setFill(Color.web("#1b5e20"));   
        }

        // Draw food
        Point f = game.getFood();
        if (f != null) {
            cells[f.x][f.y].setFill(Color.web("#b71c1c"));
        }
    }

    public void startGame(int speed) {
        draw(game, cells, rows, columns);

        loop = new Timeline(
            new KeyFrame(Duration.millis(speed), e -> {
                game.step();
                draw(game, cells, rows, columns);

                if (game.isGameOver()) {
                    gameOverScreen.show();
                    loop.stop();
                }
            })
        );
        loop.setCycleCount(Timeline.INDEFINITE);
        loop.play();
    }

    public void buildGrid(int newRows, int newColumns) {
        this.rows = newRows;
        this.columns = newColumns;

        // Clear the old grid if it exists
        gameGrid.getChildren().clear();

        // Initialize cells and game
        cells = new Rectangle[rows][columns];
        game = new SnakeGame(rows, columns);

        double availableSize = 900.0;
        double sizeBasedOnWidth = availableSize / columns;
        double sizeBasedOnHeight = availableSize / rows;
        double cellSize = Math.floor(Math.min(sizeBasedOnWidth, sizeBasedOnHeight));

        // Avoid gaps from GridPane spacing
        gameGrid.setHgap(0);
        gameGrid.setVgap(0);

        // Create grid cells
        for (int col = 0; col < rows; col++) {
            for (int row = 0; row < columns; row++) {
                Rectangle cell = new Rectangle(cellSize, cellSize);
                cell.setFill(Color.web("#f4c064"));
                cell.setStroke(Color.web("#c0a060"));
                cell.setStrokeWidth(1.0);
                cell.setStrokeType(StrokeType.INSIDE); // Stroke inside to avoid increasing size of each cell
                gameGrid.add(cell, col, row);
                cells[col][row] = cell;    
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}