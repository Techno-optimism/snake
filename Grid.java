import java.util.ArrayList;
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
import javafx.scene.paint.ImagePattern;
import javafx.scene.image.Image;


public class Grid extends Application {

    private Timeline loop;
    private SnakeGame game;
    private Rectangle[][] cells;
    private GameOverScreen gameOverScreen;
    private DifficultyScreen difficultyScreen;
    private GameSizeScreen gameSizeScreen;
    private MovementTypeScreen movementTypeScreen;
    private MainMenuScreen mainMenuScreen;
    private GridPane gameGrid;
    private HBox topBar;
    private int gameMode = 0;
    private int gameMovementType = 0;
    private static final int MODE_NONE = 0;
    private static final int MODE_CLASSIC = 1;
    private static final int MODE_TIMED = 2;
    private static final int MOVEMENT_CLASSIC = 1;
    private static final int MOVEMENT_WRAP = 2;
    private BorderPane mainLayout;


    private int rows;
    private int columns;
    private int selectedRows;
    private int selectedCols;
    private int currentSpeed = 150;
    private Label scoreLabel;
    private Label highScoreLabel;
    private ImagePattern bodyPattern, tailPattern, applePattern;
    private ImagePattern headUp, headDown, headLeft, headRight;

    @Override
    public void start(Stage stage) {
        mainLayout = new BorderPane();

        // Top bar
        topBar = new HBox(50);
        topBar.setPrefHeight(50);
        topBar.setStyle("-fx-background-color: white; -fx-alignment: CENTER;");

        // Score label
        scoreLabel = new Label("Score: 0");
        scoreLabel.setStyle("-fx-font-family: 'Comic Sans MS'; -fx-font-size: 20px; -fx-text-fill: black;");

        // High score label
        highScoreLabel = new Label("High Score: 0");
        highScoreLabel.setStyle("-fx-font-family: 'Comic Sans MS'; -fx-font-size: 20px; -fx-text-fill: black;");

        // score/highscore labels are added when a mode that shows them is selected

        // Game area (stackpane)
        StackPane gameArea = new StackPane();

        gameGrid = new GridPane();
        gameGrid.setAlignment(Pos.CENTER);
        gameGrid.setStyle(
            "-fx-background-color: #f4c064;" + // Background color
            "-fx-border-color: #c0a060;" +     // Border color
            "-fx-border-width: 2px;" +          // Border width
            "-fx-border-style: solid;"         // Border style
        );

        // Add the grid to the game area; screens are added after they're constructed
        gameArea.getChildren().add(gameGrid);

        // Assemble BorderPane
        // top bar is attached only when a mode is selected
        mainLayout.setCenter(gameArea);

        // Create a root StackPane so overlay screens can cover the whole window
        StackPane root = new StackPane();

        // Create scene and set up stage
        Scene scene = new Scene(root, 1250, 1250);

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
            // On restart click
            () -> {
                scoreLabel.setText("Score: 0");
                game.reset();
                startGame(currentSpeed);
            },
            // On exit to main menu click
            () -> {
                scoreLabel.setText("Score: 0");
                game.reset();
                // hide grid and clear mode/ui
                setGameGridVisible(false);
                gameMode = MODE_NONE;
                topBar.getChildren().clear();
                mainLayout.setTop(null);
                mainMenuScreen.show();
            },
            // On quit click
            () -> System.exit(0)
        );

        difficultyScreen = new DifficultyScreen(
            () -> {
                currentSpeed = 250;
                movementTypeScreen.show();
            },
            () -> {
                currentSpeed = 150;
                movementTypeScreen.show();
            },
            () -> {
                currentSpeed = 75;
                movementTypeScreen.show();
            },
            () -> {
                currentSpeed = 45;
                movementTypeScreen.show();
            },
            () -> {
                gameSizeScreen.show();
            }
        );

        gameSizeScreen = new GameSizeScreen(
            // NOTE: We only STORE the size here. We do NOT call buildGrid() yet.
            // 
            // Reason: buildGrid() initializes the 'SnakeGame' logic, which requires
            // gameMovementType (Classic/Wrap) to be known. Since movement type is selected
            // later (in MovementTypeScreen), calling buildGrid() now 
            // would lock in the wrong (default) movement rules.
            //
            // The actual grid building happens in MovementTypeScreen callbacks.
            () -> {
                this.selectedRows = 7;
                this.selectedCols = 7;
                difficultyScreen.show();
            },
            () -> {
                this.selectedRows = 10;
                this.selectedCols = 10;
                difficultyScreen.show();
            }, 
            () -> {
                this.selectedRows = 15;
                this.selectedCols = 15;
                difficultyScreen.show();
            },
            () -> {
                this.selectedRows = 20;
                this.selectedCols = 20;
                difficultyScreen.show();
            },
            () -> {
                mainMenuScreen.show();
            }
        );

        mainMenuScreen = new MainMenuScreen(
            () -> {
                // Classic mode selected
                mainMenuScreen.hide();
                gameMode = MODE_CLASSIC;
                // Attaches top bar and makes score UI visible for classic
                mainLayout.setTop(topBar);
                if (!topBar.getChildren().contains(scoreLabel)) {
                    topBar.getChildren().addAll(scoreLabel, highScoreLabel);
                }
                gameSizeScreen.show();
            },
            () -> {
                // Timed mode selected
                mainMenuScreen.hide();
                gameMode = MODE_TIMED;
                // Attaches the top bar but removes score UI for timed mode
                mainLayout.setTop(topBar);
                topBar.getChildren().removeAll(scoreLabel, highScoreLabel);
                gameSizeScreen.show();
            }
        );

        movementTypeScreen = new MovementTypeScreen(
            () -> {
                // Classic mode selected
                movementTypeScreen.hide();
                System.out.println("Classic movement selected");
                gameMovementType = MOVEMENT_CLASSIC;

                // Now the grid is built with the selected size and movement type
                buildGrid(selectedRows, selectedCols);
                startGame(currentSpeed);
            },
            () -> {
                // Wrap mode selected
                movementTypeScreen.hide();
                System.out.println("Wrap movement selected");
                gameMovementType = MOVEMENT_WRAP;

                // Now the grid is built with the selected size and movement type
                buildGrid(selectedRows, selectedCols);
                startGame(currentSpeed);
            }, 
            () -> {
                gameSizeScreen.show();
            }
        );

        // Load images for snake and food
        try {
            headUp    = new ImagePattern(new Image("file:resources/Snake_head_up.png", 0, 0, true, false));
            headDown  = new ImagePattern(new Image("file:resources/Snake_head_down.png", 0, 0, true, false));
            headLeft  = new ImagePattern(new Image("file:resources/Snake_head_left.png", 0, 0, true, false));
            headRight = new ImagePattern(new Image("file:resources/Snake_head_right.png", 0, 0, true, false));

        } catch (Exception e) {
            System.out.println("Cant find images");
        }

        stage.setScene(scene);
        stage.setTitle("Snake Game");
        stage.setResizable(false);

        // The screens are added to the root so they can cover the whole window
        root.getChildren().addAll(
            mainLayout, // The game itself
            gameOverScreen, // Layer 1
            movementTypeScreen, // Layer 2
            difficultyScreen, // Layer 3
            gameSizeScreen, // Layer 4
            mainMenuScreen); // Layer 5 (top)

        // Show the first screen
        mainMenuScreen.show();
        stage.show();
    }

    // #1b5e20 (snake color)
    // #ff5555 (bright food color)
    // #b71c1c (darker food color)
    // #c0a060 (grid color)

    // Used to clear the grid and draw the game state onto the grid each tick
    private void setGameGridVisible(boolean visible) {
        gameGrid.setVisible(visible);
        if (!visible) {
            loop.stop();
        }
    }

    private void draw(SnakeGame game, Rectangle[][] cells, int n, int m) {
        // Clear all cells to background color
        for (int col = 0; col < n; col++) {
            for (int row = 0; row < m; row++) {
                cells[col][row].setFill(Color.web("#f4c064"));
                cells[col][row].setStroke(Color.web("#c0a060"));  
            }
        }

        int currentScore = game.getSnake().size() - 2;
        scoreLabel.setText("Score: " + currentScore);



        // Draw snake
        var snakeList = new ArrayList<>(game.getSnake());

        for (int i = 0; i < snakeList.size(); i++) {
            Point p = snakeList.get(i);

            if (p.x < 0 || p.x >= columns || p.y < 0 || p.y >= rows) {
                continue; // Skip invalid points
            }

            if (i == 0) {
                SnakeGame.Direction d = game.getDirection();
                if (d == null) d = SnakeGame.Direction.LEFT;
                // Head
                switch (d) {
                    case UP: cells[p.x][p.y].setFill(headUp); break;
                    case DOWN: cells[p.x][p.y].setFill(headDown); break;
                    case LEFT: cells[p.x][p.y].setFill(headLeft); break;
                    case RIGHT: cells[p.x][p.y].setFill(headRight); break;
                }
            } else {
                // Body
                cells[p.x][p.y].setFill(Color.web("#1b5e20"));   
            }
        }



        //for (Point p : game.getSnake()) {
            //cells[p.x][p.y].setFill(Color.web("#1b5e20"));   
        //}

        // Draw food
        Point f = game.getFood();
        if (f != null) {
            cells[f.x][f.y].setFill(Color.web("#b71c1c"));
        }
    }

    public void startGame(int speed) {
        this.currentSpeed = speed;
        setGameGridVisible(true);
        // Show or hide score UI depending on selected game mode
        if (gameMode == MODE_CLASSIC) {
            if (!topBar.getChildren().contains(scoreLabel)) {
                topBar.getChildren().addAll(scoreLabel, highScoreLabel);
            }
        } else {
            topBar.getChildren().removeAll(scoreLabel, highScoreLabel);
        }

        draw(game, cells, rows, columns);

        loop = new Timeline(
            new KeyFrame(Duration.millis(speed), e -> {
                game.step();
                draw(game, cells, rows, columns);

                if (game.isGameOver()) {
                    gameOverScreen.show();

                    // Update high score if beaten
                    int finalScore = game.getSnake().size() - 2;
                    int previousHighScore = Integer.parseInt(highScoreLabel.getText().split(": ")[1]);
                    if (finalScore > previousHighScore) {
                        highScoreLabel.setText("High Score: " + finalScore);
                    }

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
        gameGrid.setStyle("-fx-background-color: #f4c064; -fx-border-color: #c0a060; -fx-border-width: 2px;");

        // Initialize cells and game
        cells = new Rectangle[rows][columns];

        System.out.println("Building grid. Type of movement: " + gameMovementType);
        MovementType strategy = new ClassicMovement();

        // Set movement strategy based on game mode
        if (gameMovementType == MOVEMENT_CLASSIC) {
            System.out.println("Using Classic Movement Strategy");
            strategy = new ClassicMovement();
        }
        else if (gameMovementType == MOVEMENT_WRAP) {
            System.out.println("Using Wrap Movement Strategy");
            strategy = new WrapMovement();
        }

        game = new SnakeGame(rows, columns, strategy);

        double availableSize = 1150.0;
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
                cell.setFill(Color.web("#f4c064")); // Background color
                cell.setStroke(null);
                // cell.setStrokeWidth(1.0);
                // cell.setStrokeType(StrokeType.INSIDE); // Stroke inside to avoid increasing size of each cell
                gameGrid.add(cell, col, row);
                cells[col][row] = cell;    
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    // #c0a060" stroke color
    // #f4c064 background color
}