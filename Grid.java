import java.util.ArrayList;
import java.util.Random;

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
    private SettingsScreen settingsScreen;
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

    private ImagePattern blueApplePattern, redApplePattern, purpleApplePattern;
    private ImagePattern headUp, headDown, headLeft, headRight;
    private ImagePattern bodyHorizontal, bodyVertical, bodyUpRight, bodyUpLeft, bodyDownRight, bodyDownLeft;
    private ImagePattern tailUp, tailDown, tailLeft, tailRight;

    private final Random random = new Random();

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
        gameGrid.setSnapToPixel(true);
        gameGrid.setStyle("-fx-background-color: #f4c064;");

        // Outer wall
        StackPane gridContainer = new StackPane(gameGrid);
        gridContainer.setStyle(
            "-fx-background-color: #8B4513;" + // Outer wall
            "-fx-padding: 10px;"               // Thickness of the wall
        );

        // Prevent grid from growing too large
        gridContainer.setMaxSize(StackPane.USE_PREF_SIZE, StackPane.USE_PREF_SIZE);

        // Add the container to the game area; screens are added after they're constructed
        gameArea.getChildren().add(gridContainer);

        // Assemble BorderPane
        // top bar is attached only when a mode is selected
        mainLayout.setCenter(gridContainer);

        // Create a root StackPane so overlay screens can cover the whole window
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #f4c064;");

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
                System.out.println("GOING BACK TO SIZE SCREEN NOW");
                gameSizeScreen.show();
            }
        );

        gameSizeScreen = new GameSizeScreen(
            // NOTE: The size is only stored here and buildGrid() is not called yet
            // 
            // Reason: buildGrid() initializes the SnakeGame logic, which requires
            // gameMovementType (Classic/Wrap) to be known. Since movement type is selected
            // later (in MovementTypeScreen), calling buildGrid() now 
            // would lock in the wrong (default) movement rules
            //
            // The actual grid building happens in MovementTypeScreen callbacks
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
        
        settingsScreen = new SettingsScreen(
            () -> {
                
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
            },
            () -> {
                // Settings selected
                mainMenuScreen.hide();
                settingsScreen.show();
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
                difficultyScreen.show();
            }
        );

        // Load images for snake and food
        try {
            headUp    = new ImagePattern(new Image("file:resources/Snake head/snake_head_up.png", 512, 512, true, false));
            headDown  = new ImagePattern(new Image("file:resources/Snake head/snake_head_down.png", 512, 512, true, false));
            headLeft  = new ImagePattern(new Image("file:resources/Snake head/snake_head_left.png", 512, 512, true, false));
            headRight = new ImagePattern(new Image("file:resources/Snake head/snake_head_right.png", 512, 512, true, false));

            bodyHorizontal = new ImagePattern(new Image("file:resources/Snake body/snake_body_horizontal.png", 512, 512, true, false));
            bodyVertical = new ImagePattern(new Image("file:resources/Snake body/snake_body_vertical.png", 512, 512, true, false));
            bodyUpRight = new ImagePattern(new Image("file:resources/Snake body/snake_body_up_right.png", 512, 512, true, false));
            bodyUpLeft = new ImagePattern(new Image("file:resources/Snake body/snake_body_up_left.png", 512, 512, true, false));
            bodyDownRight = new ImagePattern(new Image("file:resources/Snake body/snake_body_down_right.png", 512, 512, true, false));
            bodyDownLeft = new ImagePattern(new Image("file:resources/Snake body/snake_body_down_left.png", 512, 512, true, false));

            tailUp = new ImagePattern(new Image("file:resources/Snake tail/snake_tail_up.png", 512, 512, true, false));
            tailDown = new ImagePattern(new Image("file:resources/Snake tail/snake_tail_down.png", 512, 512, true, false));
            tailLeft = new ImagePattern(new Image("file:resources/Snake tail/snake_tail_left.png", 512, 512, true, false));
            tailRight = new ImagePattern(new Image("file:resources/Snake tail/snake_tail_right.png", 512, 512, true, false));

            redApplePattern = new ImagePattern(new Image("file:resources/red_apple.png", 512, 512, true, false));
            blueApplePattern = new ImagePattern(new Image("file:resources/blue_apple.png", 512, 512, true, false));
            purpleApplePattern = new ImagePattern(new Image("file:resources/purple_apple.png", 512, 512, true, false));

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
            settingsScreen, // Layer 5
            mainMenuScreen); // Layer 6 (top)

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
                cells[col][row].setStroke(null); // No border
            }
        }

        int currentScore = game.getSnake().size() - 2;
        scoreLabel.setText("Score: " + currentScore);



        // Draw snake
        var snakeList = new ArrayList<>(game.getSnake());

        for (int i = 0; i < snakeList.size(); i++) {
            // if (p.x < 0 || p.x >= columns || p.y < 0 || p.y >= rows) {
            //     continue; // Skip invalid points
            // }

            Point current = snakeList.get(i);

            // -----------------------------
            //     Head (first element)
            // -----------------------------
            if (i == 0) {
                Point neck = snakeList.size() > 1 ? snakeList.get(1) : null;

                // Makes head keep its direction on wall collision (prevents decapitation of poor snake)
                if (neck != null) {
                    if (current.x > neck.x) {
                        cells[current.x][current.y].setFill(headRight);
                    } else if (current.x < neck.x) {
                        cells[current.x][current.y].setFill(headLeft);
                    } else if (current.y > neck.y) {
                        cells[current.x][current.y].setFill(headDown);
                    } else if (current.y < neck.y) {
                        cells[current.x][current.y].setFill(headUp);
                    }
                // Default (on start)
                } else {
                    cells[current.x][current.y].setFill(headLeft); 
                }

            // -----------------------------
            //     Tail (last element)
            // -----------------------------
            } else if (i == snakeList.size() - 1) {
                // Segment connected to the tail
                Point prev = snakeList.get(i - 1);

                if (prev.y < current.y) {
                cells[current.x][current.y].setFill(tailUp);
                }
                else if (prev.y > current.y) {
                    cells[current.x][current.y].setFill(tailDown);
                }
                else if (prev.x < current.x) {
                    cells[current.x][current.y].setFill(tailLeft);
                }
                else if (prev.x > current.x) {
                    cells[current.x][current.y].setFill(tailRight);
                }
            }

            // -----------------------------
            //    Body (middle elements)
            // -----------------------------
            else {
            Point prev = snakeList.get(i - 1);
            Point next = snakeList.get(i + 1);

            // Checks for neighbors
            boolean left = (prev.x < current.x || next.x < current.x);
            boolean right = (prev.x > current.x || next.x > current.x);
            // Note: Y-axis is reversed
            boolean up = (prev.y < current.y || next.y < current.y);
            boolean down = (prev.y > current.y || next.y > current.y);

            if (left && right) {
                // Only left and right neighbors (horizontal mid body)
                cells[current.x][current.y].setFill(bodyHorizontal);
            }
            else if (up && down) {
                // Only up and down neighbors (vertical mid body)
                cells[current.x][current.y].setFill(bodyVertical);
            }
            else if (up && right) cells[current.x][current.y].setFill(bodyUpRight);
            else if (up && left) cells[current.x][current.y].setFill(bodyUpLeft);
            else if (down && right) cells[current.x][current.y].setFill(bodyDownRight);
            else if (down && left) cells[current.x][current.y].setFill(bodyDownLeft);
            }
        }

        //for (Point p : game.getSnake()) {
            //cells[p.x][p.y].setFill(Color.web("#1b5e20"));   
        //}

        // Draw food
        Point f = game.getFood();
        if (f != null) {
            // Random color for food
            int type = game.getAppleType();
            if (type == 1) {
                cells[f.x][f.y].setFill(redApplePattern); // Red apple
            } else if (type == 2) {
                cells[f.x][f.y].setFill(blueApplePattern); // Blue apple
            } else {
                cells[f.x][f.y].setFill(purpleApplePattern); // Purple apple
            }
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
        gameGrid.setStyle("-fx-background-color: #f4c064;"); 

        // Initialize cells
        cells = new Rectangle[columns][rows];

        System.out.println("Building grid. Type of movement: " + gameMovementType);
        MovementType movementType = new ClassicMovement();

        // Set movement type
        if (gameMovementType == MOVEMENT_CLASSIC) {
            System.out.println("Using Classic Movement");
            movementType = new ClassicMovement();
        }
        else if (gameMovementType == MOVEMENT_WRAP) {
            System.out.println("Using Wrap Movement");
            movementType = new WrapMovement();
        }

        game = new SnakeGame(columns, rows, movementType);

        double availableSize = 1150.0;
        double sizeBasedOnWidth = availableSize / columns;
        double sizeBasedOnHeight = availableSize / rows;
        double cellSize = Math.floor(Math.min(sizeBasedOnWidth, sizeBasedOnHeight));

        // Avoid gaps from GridPane spacing
        gameGrid.setHgap(0);
        gameGrid.setVgap(0);

        // Create grid cells
        for (int x = 0; x < columns; x++) {
            for (int y = 0; y < rows; y++) {
                Rectangle cell = new Rectangle(cellSize + 0.6, cellSize + 0.6); // + 0.6 to prevent segment gaps

                // cell.setStrokeWidth(1.0);
                // cell.setStrokeType(StrokeType.INSIDE); // Stroke inside to avoid increasing size of each cell
                gameGrid.add(cell, x, y);
                cells[x][y] = cell;    
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    // #c0a060" stroke color
    // #f4c064 background color
}