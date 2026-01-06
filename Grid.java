import javafx.application.Application;
import javafx.scene.Scene;
// import javafx.scene.control.skin.TextInputControlSkin.Direction;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.awt.Point;
import java.util.ArrayDeque;
import javafx.geometry.Pos;


public class Grid extends Application {

    private Timeline loop;
    private SnakeGame game;
    private Rectangle[][] cells;
    private GameOverScreen gameOverScreen;
    private DifficultyScreen difficultyScreen;
    private GameSizeScreen gameSizeScreen;
    private GridPane gameGrid;


    private int rows;
    private int columns;

    @Override
    public void start(Stage stage) {
        StackPane root = new StackPane();
        gameGrid = new GridPane();
        gameGrid.setAlignment(Pos.CENTER);

        // Makes the scene really big so we can have larger grids
        // The stackpane will center the grid in the window
        Scene scene = new Scene(root, 1000, 1000);
        stage.setScene(scene);
        stage.setResizable(false);

        scene.setOnKeyPressed(event -> {
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

        difficultyScreen = new DifficultyScreen(
            () -> startGame(250),
            () -> startGame(150),
            () -> startGame(75), 
            () -> startGame(45)
        );

        gameSizeScreen = new GameSizeScreen(
            () -> {
                buildGrid(7, 7);
                difficultyScreen.show();
            },
            () -> {
                buildGrid(10, 10);
                difficultyScreen.show();
            }, 
            () -> {
                buildGrid(15, 15);
                difficultyScreen.show();
            },
            () -> {
                buildGrid(20, 20);
                difficultyScreen.show();
            }
        );

        // The game grid, game overscreen and difficulty screen are added to the root StackPane
        // The game overscreen and difficulty screen are added last so they're on top of the grid
        root.getChildren().addAll(gameGrid, gameOverScreen, gameSizeScreen, difficultyScreen);
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

        cells = new Rectangle[rows][columns];
        game = new SnakeGame(rows, columns);

        int cellSize = 50;
        for (int col = 0; col < rows; col++) {
            for (int row = 0; row < columns; row++) {
                Rectangle cell = new Rectangle(cellSize, cellSize);
                cell.setFill(Color.web("#f4c064"));
                cell.setStroke(Color.web("#c0a060"));
                gameGrid.add(cell, col, row);
                cells[col][row] = cell;    
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
