import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.awt.Point;
import java.util.*;

public class SimpleGrid extends Application {

    private Timeline loop;

    @Override
    public void start(Stage stage) {
        GridPane pane = new GridPane();

        int n = hor;
        int m = ver;
        int cellSize = 50;

        Rectangle[][] cells = new Rectangle[n][m];

        for (int col = 0; col < n; col++) {
            for (int row = 0; row < m; row++) {
                Rectangle cell = new Rectangle(cellSize, cellSize);
                cell.setFill(Color.web("#f4c064"));
                cell.setStroke(Color.web("#c0a060"));
                pane.add(cell, col, row);
                cells[col][row] = cell;    
            }
        }

        int sceneWidth  = n * cellSize;
        int sceneHeight = m * cellSize;

        Scene scene = new Scene(pane, sceneWidth, sceneHeight);
        stage.setScene(scene);
        stage.setResizable(false);

        SimpleSnakeGame game = new SimpleSnakeGame(n, m);

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP -> game.setDirection(SimpleSnakeGame.Direction.UP);
                case DOWN -> game.setDirection(SimpleSnakeGame.Direction.DOWN);
                case LEFT -> game.setDirection(SimpleSnakeGame.Direction.LEFT);
                case RIGHT -> game.setDirection(SimpleSnakeGame.Direction.RIGHT);
                default -> { }
            }
        });

        loop = new Timeline(
            new KeyFrame(Duration.millis(150), e -> {
                game.step(); // Move snake one step
                draw(game, cells, n, m);

                if (game.isGameOver()) {
                    System.out.println("GAME OVER"); 
                    loop.stop();
                }
            })
        );

        loop.setCycleCount(Timeline.INDEFINITE);
        loop.play();

        stage.show();
    }

    // #1b5e20 (snake color)
    // #ff5555 (bright food color)
    // #b71c1c (darker food color)
    // #c0a060 (grid color)

    private void draw(SimpleSnakeGame game, Rectangle[][] cells, int n, int m) {
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

    public static int hor;
    public static int ver;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.print("Input horizontal size: ");
        hor = input.nextInt();
        if (hor < 5) {
            hor = 5;
        }
        if (hor > 100) {
            hor = 100;
        }
        System.out.print("Input vertical size: ");
        ver = input.nextInt();
        if (ver < 5) {
            ver = 5;
        }
        if (ver > 100) {
            ver = 100;
        }
        input.close();
        launch(args);
    }
}
