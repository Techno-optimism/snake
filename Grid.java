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
import java.awt.Dimension;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.image.Image;
import javafx.stage.Screen;

public class Grid extends Application {

    private GridPane gameGrid;
    private HBox topBar;
    private Timeline loop;
    private SnakeGame game;
    private Rectangle[][] cells;

    private GameOverScreen gameOverScreen;
    private DifficultyScreen difficultyScreen;
    private GameSizeScreen gameSizeScreen;
    private SettingsScreen settingsScreen;
    private MovementTypeScreen movementTypeScreen;
    private MainMenuScreen mainMenuScreen;
    private StatsScreen statsScreen;
    private PauseScreen pauseScreen;

    private int totalGamesPlayed = 0;
    private int classicHighScore = 0;
    private int timedHighScore = 0;

    private boolean paused = false;

    private int gameMode = 0;
    private int gameMovementType = 0;
    private static final int MODE_NONE = 0;
    private static final int MODE_CLASSIC = 1;
    private static final int MODE_TIMED = 2;
    private static final int MOVEMENT_CLASSIC = 1;
    private static final int MOVEMENT_WRAP = 2;
    private BorderPane mainLayout;
    private double screenWidth, screenHeight;

    private int rows;
    private int columns;
    private int selectedRows;
    private int selectedCols;
    private int currentSpeed = 150;

    private Label classicScoreLabel, timedScoreLabel;
    private Label classicHighScoreLabel, timedHighScoreLabel;
    private Label timerLabel; // shows remaining time
    private double elapsedMs = 0; // elapsed time
    private int initialTime, timeLeft; // in seconds
    private int foodBonus; // timer increase per food

    private ImagePattern bombPattern;
    private ImagePattern blueApplePattern, redApplePattern, purpleApplePattern;
    private ImagePattern headUp, headDown, headLeft, headRight;
    private ImagePattern bodyHorizontal, bodyVertical, bodyUpRight, bodyUpLeft, bodyDownRight, bodyDownLeft;
    private ImagePattern tailUp, tailDown, tailLeft, tailRight;

    private final Random random = new Random();

    @Override
    public void start(Stage stage) {
        mainLayout = new BorderPane();

        // --------------------------
        // Classic and timed labels
        // --------------------------
        // Classic
        classicScoreLabel = new Label("Score: 0");
        classicScoreLabel.setStyle("-fx-font-family: 'Comic Sans MS'; -fx-font-size: 20px; -fx-text-fill: black;");
        classicHighScoreLabel = new Label("High Score: 0");
        classicHighScoreLabel.setStyle("-fx-font-family: 'Comic Sans MS'; -fx-font-size: 20px; -fx-text-fill: black;");

        // Timed
        timedScoreLabel = new Label("Score: 0");
        timedScoreLabel.setStyle("-fx-font-family: 'Comic Sans MS'; -fx-font-size: 20px; -fx-text-fill: black;");
        timedHighScoreLabel = new Label("High Score: 0");
        timedHighScoreLabel.setStyle("-fx-font-family: 'Comic Sans MS'; -fx-font-size: 20px; -fx-text-fill: black;");
        timerLabel = new Label("Time left: 0");
        timerLabel.setStyle("-fx-font-family: 'Comic Sans MS'; -fx-font-size: 20px; -fx-text-fill: black;");

        // Top bar container
        topBar = new HBox(10);
        topBar.setPrefHeight(50);
        topBar.setAlignment(Pos.CENTER);
        topBar.setStyle("-fx-background-color: white; -fx-padding: 10;");

        // Game area (StackPane)
        StackPane gameArea = new StackPane();

        gameGrid = new GridPane();
        gameGrid.setAlignment(Pos.CENTER);
        gameGrid.setSnapToPixel(true);
        gameGrid.setStyle("-fx-background-color: #f4c064;");

        // Outer wall
        StackPane gridContainer = new StackPane(gameGrid);
        gridContainer.setStyle(
                "-fx-background-color: #8B4513;" + // Outer wall
                        "-fx-padding: 10px;" // Thickness of the wall
        );

        // Prevent grid from growing too large
        gridContainer.setMaxSize(StackPane.USE_PREF_SIZE, StackPane.USE_PREF_SIZE);

        // Add the container to the game area; screens are added after they're
        // constructed
        gameArea.getChildren().add(gridContainer);

        // Assemble BorderPane
        // top bar is attached only when a mode is selected
        mainLayout.setCenter(gridContainer);

        // Create a root StackPane so overlay screens can cover the whole window
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #f4c064;");

        // Create scene and set up stage
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        screenWidth = bounds.getWidth();
        screenHeight = bounds.getHeight();
        Scene scene = new Scene(root, screenWidth * 0.55, screenHeight * 0.9);

        scene.setOnKeyPressed(event -> {
            if (game == null)
                return;

            switch (event.getCode()) {
                case SPACE -> togglePause();

                case UP -> {
                    if (!paused)
                        game.setDirection(SnakeGame.Direction.UP);
                }
                case DOWN -> {
                    if (!paused)
                        game.setDirection(SnakeGame.Direction.DOWN);
                }
                case LEFT -> {
                    if (!paused)
                        game.setDirection(SnakeGame.Direction.LEFT);
                }
                case RIGHT -> {
                    if (!paused)
                        game.setDirection(SnakeGame.Direction.RIGHT);
                }

                default -> {
                }
            }
        });

        gameOverScreen = new GameOverScreen(
                // On restart click
                () -> {
                    game.reset();

                    elapsedMs = 0;
                    timeLeft = initialTime;

                    // Only reset current score, not high score
                    if (gameMode == MODE_CLASSIC) {
                        classicScoreLabel.setText("Score: 0");
                    } else if (gameMode == MODE_TIMED) {
                        timedScoreLabel.setText("Score: 0");
                    }

                    if (gameMode == MODE_TIMED) {
                        timerLabel.setText("Time left: " + timeLeft);
                    }
                    startGame(currentSpeed);
                },
                // On exit to main menu click
                () -> {
                    resetScoreLabels(); // Resets all labels
                    game.reset();
                    // hide grid and clear mode/ui
                    setGameGridVisible(false);
                    gameMode = MODE_NONE;
                    topBar.getChildren().clear();
                    mainLayout.setTop(null);
                    mainMenuScreen.show();
                },
                // On quit click
                () -> System.exit(0));

        difficultyScreen = new DifficultyScreen(
                () -> {
                    currentSpeed = 250;
                    timeLeft = 45;
                    initialTime = timeLeft;
                    foodBonus = 10;
                    movementTypeScreen.show();
                },
                () -> {
                    currentSpeed = 150;
                    timeLeft = 30;
                    initialTime = timeLeft;
                    foodBonus = 5;
                    movementTypeScreen.show();
                },
                () -> {
                    currentSpeed = 75;
                    timeLeft = 10;
                    initialTime = timeLeft;
                    foodBonus = 2;
                    movementTypeScreen.show();
                },
                () -> {
                    currentSpeed = 45;
                    timeLeft = 5;
                    initialTime = timeLeft;
                    foodBonus = 2;
                    movementTypeScreen.show();
                },
                () -> {
                    gameSizeScreen.show();
                });

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
                });

        settingsScreen = new SettingsScreen(
                () -> {

                },
                () -> {
                    mainMenuScreen.show();
                });

        mainMenuScreen = new MainMenuScreen(
                () -> {
                    // Classic mode selected
                    mainMenuScreen.hide();
                    gameMode = MODE_CLASSIC;
                    gameSizeScreen.show();
                },
                () -> {
                    // Timed mode selected
                    mainMenuScreen.hide();
                    gameMode = MODE_TIMED;
                    gameSizeScreen.show();
                },
                () -> {
                    // Stats selected
                    mainMenuScreen.hide();
                    statsScreen.updateData(totalGamesPlayed, classicHighScore, timedHighScore);
                    statsScreen.show();
                },
                () -> {
                    // Settings selected
                    mainMenuScreen.hide();
                    settingsScreen.show();
                });

        pauseScreen = new PauseScreen("file:resources/pause.png");

        statsScreen = new StatsScreen(
                () -> {
                    // Back mode selected
                    statsScreen.hide();
                    mainMenuScreen.show();
                });

        movementTypeScreen = new MovementTypeScreen(
                () -> {
                    // Classic mode selected
                    movementTypeScreen.hide();
                    gameMovementType = MOVEMENT_CLASSIC;

                    // Now the grid is built with the selected size and movement type
                    buildGrid(selectedRows, selectedCols);
                    startGame(currentSpeed);
                },
                () -> {
                    // Wrap mode selected
                    movementTypeScreen.hide();
                    gameMovementType = MOVEMENT_WRAP;

                    // Now the grid is built with the selected size and movement type
                    buildGrid(selectedRows, selectedCols);
                    startGame(currentSpeed);
                },
                () -> {
                    difficultyScreen.show();
                });

        // Load images for snake and food
        try {
            headUp = new ImagePattern(new Image("file:resources/Snake head/snake_head_up.png", 512, 512, true, false));
            headDown = new ImagePattern(
                    new Image("file:resources/Snake head/snake_head_down.png", 512, 512, true, false));
            headLeft = new ImagePattern(
                    new Image("file:resources/Snake head/snake_head_left.png", 512, 512, true, false));
            headRight = new ImagePattern(
                    new Image("file:resources/Snake head/snake_head_right.png", 512, 512, true, false));

            bodyHorizontal = new ImagePattern(
                    new Image("file:resources/Snake body/snake_body_horizontal.png", 512, 512, true, false));
            bodyVertical = new ImagePattern(
                    new Image("file:resources/Snake body/snake_body_vertical.png", 512, 512, true, false));
            bodyUpRight = new ImagePattern(
                    new Image("file:resources/Snake body/snake_body_up_right.png", 512, 512, true, false));
            bodyUpLeft = new ImagePattern(
                    new Image("file:resources/Snake body/snake_body_up_left.png", 512, 512, true, false));
            bodyDownRight = new ImagePattern(
                    new Image("file:resources/Snake body/snake_body_down_right.png", 512, 512, true, false));
            bodyDownLeft = new ImagePattern(
                    new Image("file:resources/Snake body/snake_body_down_left.png", 512, 512, true, false));

            tailUp = new ImagePattern(new Image("file:resources/Snake tail/snake_tail_up.png", 512, 512, true, false));
            tailDown = new ImagePattern(
                    new Image("file:resources/Snake tail/snake_tail_down.png", 512, 512, true, false));
            tailLeft = new ImagePattern(
                    new Image("file:resources/Snake tail/snake_tail_left.png", 512, 512, true, false));
            tailRight = new ImagePattern(
                    new Image("file:resources/Snake tail/snake_tail_right.png", 512, 512, true, false));

            redApplePattern = new ImagePattern(new Image("file:resources/red_apple.png", 512, 512, true, false));
            blueApplePattern = new ImagePattern(new Image("file:resources/blue_apple.png", 512, 512, true, false));
            purpleApplePattern = new ImagePattern(new Image("file:resources/purple_apple.png", 512, 512, true, false));

            bombPattern = new ImagePattern(new Image("file:resources/bomb.png", 512, 512, true, false));

        } catch (Exception e) {
            System.out.println("Can't find images");
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
                statsScreen, // Layer 6
                pauseScreen, // Layer 7
                mainMenuScreen); // Layer 8 (top)

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

        // Update the score
        int currentScore = game.getSnake().size() - 2;
        updateScoreLabel(currentScore);

        // Draw bombs
        for (SnakeGame.Bomb b : game.getBombs()) {
            cells[b.pos.x][b.pos.y].setFill(bombPattern);
        }

        // Draw snake
        var snakeList = new ArrayList<>(game.getSnake());

        for (int i = 0; i < snakeList.size(); i++) {
            Point current = snakeList.get(i);

            // -----------------------------
            // Head (first element)
            // -----------------------------
            if (i == 0) {
                Point neck = snakeList.size() > 1 ? snakeList.get(1) : null;

                // Makes head keep its direction on wall collision (prevents decapitation of
                // poor snake)
                if (neck != null) {
                    int dx = current.x - neck.x;
                    int dy = current.y - neck.y;

                    // Wrapping for head
                    if (dx > 1)
                        dx = -1; // Head (higher x) on right edge pointing left, neck (lower x) on left edge
                                 // (e.g. head x = 9, neck x = 0)
                    else if (dx < -1)
                        dx = 1; // Head (lower x) on left edge pointing right, neck (higher x) on right edge

                    if (dy > 1)
                        dy = -1; // Head (higher y) on botton edge pointing up, neck (lower y) on top edge (e.g.
                                 // head y = 9, neck y = 0)
                    else if (dy < -1)
                        dy = 1; // Head (lower y) on top edge pointing down, neck (higher y) on bottom edge

                    // Standard movement
                    if (dx == 1)
                        cells[current.x][current.y].setFill(headRight); // Difference in x is 1, so neck (lower x) is to
                                                                        // the left of head (higher x), so head should
                                                                        // point right
                    else if (dx == -1)
                        cells[current.x][current.y].setFill(headLeft); // Difference in x is -1, so neck (higher x) is
                                                                       // to the right of head (lower x), so head should
                                                                       // point left
                    else if (dy == 1)
                        cells[current.x][current.y].setFill(headDown); // Difference in y is 1, so neck (lower y) is
                                                                       // above head (higher y), so head should point
                                                                       // down
                    else if (dy == -1)
                        cells[current.x][current.y].setFill(headUp); // Difference in y is -1, so neck (higher y) is
                                                                     // above head (lower y), so head should point up
                } else {
                    cells[current.x][current.y].setFill(headLeft); // Default
                }
            }
            // -----------------------------
            // Tail (last element)
            // -----------------------------
            else if (i == snakeList.size() - 1) {
                // Segment connected to the tail
                Point prev = snakeList.get(i - 1);

                int dx = prev.x - current.x;
                int dy = prev.y - current.y;

                // Wrapping for tail
                if (dx > 1)
                    dx = -1; // Tail (lower x) on the left edge pointing left, body (higher x) on the right
                             // edge
                else if (dx < -1)
                    dx = 1; // Tail (higher x) on the right edge pointing right, body (lower x) on the left
                            // edge

                if (dy > 1)
                    dy = -1; // Tail (lower y) on the top edge pointing up, body (higher y) on the bottom
                             // edge
                else if (dy < -1)
                    dy = 1; // Tail (higher y) on the bottom edge pointing down, body (lower y) on the top
                            // edge

                // Standard movement
                if (dx == 1)
                    cells[current.x][current.y].setFill(tailRight); // Difference in x is 1, so body (higher x) is to
                                                                    // the right of tail (lower x), so tail should go
                                                                    // right
                else if (dx == -1)
                    cells[current.x][current.y].setFill(tailLeft); // Difference in x is -1, so body (lower x) is to the
                                                                   // left of tail (higher x), so tail should go left
                else if (dy == 1)
                    cells[current.x][current.y].setFill(tailDown); // Difference in y is 1, so body (higher y) is below
                                                                   // tail (lower y), so tail should go down
                else if (dy == -1)
                    cells[current.x][current.y].setFill(tailUp); // Difference in y is -1 so body (lower y) is above
                                                                 // tail (higher y), so tail should go up
            }

            // -----------------------------
            // Body (middle elements)
            // -----------------------------
            else {
                Point prev = snakeList.get(i - 1);
                Point next = snakeList.get(i + 1);

                // Neighbors
                int dxPrev = prev.x - current.x;
                int dyPrev = prev.y - current.y;
                int dxNext = next.x - current.x;
                int dyNext = next.y - current.y;

                // Wrap (prev): When difference is large, it gets flipped
                if (dxPrev > 1)
                    dxPrev = -1;
                else if (dxPrev < -1)
                    dxPrev = 1;

                if (dyPrev > 1)
                    dyPrev = -1;
                else if (dyPrev < -1)
                    dyPrev = 1;

                // Wrap (next): When difference is large, it gets flipped
                if (dxNext > 1)
                    dxNext = -1;
                else if (dxNext < -1)
                    dxNext = 1;

                if (dyNext > 1)
                    dyNext = -1;
                else if (dyNext < -1)
                    dyNext = 1;

                // Left neighbors
                // Moving left: If head/prev x = 4, current = 5 and tail = 6, then prev is to
                // the left when (dxPrev = 4 - 5 = -1)
                // Moving right: If head/ x = 6, current x = 5 and tail/next = 4, then next is
                // to the left when (dxNext = 4 - 5 = -1)
                boolean isLeft = (dxPrev == -1 || dxNext == -1);

                // Right neighbors
                // Moving right: If head/prev x = 6, current x = 5 and tail x = 4, then prev is
                // to the right when (dxPrev = 6 - 5 = 1)
                // Moving left: If head x = 4, current = 5 and tail/next = 6, then next is to
                // the right when (dxNext = 6 - 5 = 1)
                boolean isRight = (dxPrev == 1 || dxNext == 1);

                // Top neighbors
                // Moving up: If head/prev y = 4, current y = 5 and tail = 6, then prev is above
                // when (dyPrev = 4 - 5 = -1)
                // Moving down: If head y = 6, current y = 5 and tail/next = 4, then next is
                // above when (dyNext = 4 - 5 = -1)
                boolean isUp = (dyPrev == -1 || dyNext == -1); // -1 is up in the grid

                // Bottom neighbors
                // Moving down: If head/prev y = 6, current y = 5 and tail = 4, then prev is
                // below when (dyPrev = 6 - 5 = 1)
                // Moving up: If head y = 4, current y = 5 and tail/next = 6, then next is below
                // when (dyNext = 6 - 5 = 1)
                boolean isDown = (dyPrev == 1 || dyNext == 1); // +1 is down in the grid

                // Correct body segnment based on neighbors
                if (isLeft && isRight) {
                    cells[current.x][current.y].setFill(bodyHorizontal);
                } else if (isUp && isDown) {
                    cells[current.x][current.y].setFill(bodyVertical);
                } else if (isUp && isRight) {
                    cells[current.x][current.y].setFill(bodyUpRight);
                } else if (isUp && isLeft) {
                    cells[current.x][current.y].setFill(bodyUpLeft);
                } else if (isDown && isRight) {
                    cells[current.x][current.y].setFill(bodyDownRight);
                } else if (isDown && isLeft) {
                    cells[current.x][current.y].setFill(bodyDownLeft);
                }
            }
        }

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
        // Prevent double-speed bugs
        if (loop != null) {
            loop.stop();
        }

        this.currentSpeed = speed;
        setGameGridVisible(true);

        StackPane header = new StackPane();
        header.setPrefHeight(50);
        header.setStyle("-fx-background-color: white; -fx-padding: 0 20 0 20;");

        if (gameMode == MODE_CLASSIC) {
            // Right side: Score Box
            HBox scoreBox = new HBox(30, classicScoreLabel, classicHighScoreLabel);
            scoreBox.setAlignment(Pos.CENTER_RIGHT);
            scoreBox.setMaxWidth(Double.MAX_VALUE);

            // Force right
            StackPane.setAlignment(scoreBox, Pos.CENTER_RIGHT);
            header.getChildren().add(scoreBox);
        } else if (gameMode == MODE_TIMED) {
            // Timer (center)
            timerLabel.setAlignment(Pos.CENTER);
            header.getChildren().add(timerLabel);

            // Score box (right)
            HBox scoreBox = new HBox(30, timedScoreLabel, timedHighScoreLabel);
            scoreBox.setAlignment(Pos.CENTER_RIGHT);
            scoreBox.setMaxWidth(Double.MAX_VALUE);
            scoreBox.setPickOnBounds(false);

            StackPane.setAlignment(scoreBox, Pos.CENTER_RIGHT);
            header.getChildren().add(scoreBox);
        }

        // Sets the top bar at the top of the screen
        mainLayout.setTop(header);

        // Reset displays
        if (gameMode == MODE_TIMED) {
            timerLabel.setText("Time left: " + initialTime);
            // Ensures the timer is visible
            timerLabel.setVisible(true);
        }

        // Initial draw
        draw(game, cells, rows, columns);

        loop = new Timeline(new KeyFrame(Duration.millis(speed), e -> {
            game.step();
            game.updateBombs(random);
            draw(game, cells, rows, columns);

            // Timed mode logic
            if (gameMode == MODE_TIMED) {
                elapsedMs += currentSpeed;
                while (elapsedMs >= 1000) {
                    elapsedMs -= 1000;
                    timeLeft--;
                }

                if (game.hasEatenFood()) {
                    timeLeft += foodBonus;
                }

                timerLabel.setText("Time left: " + timeLeft);

                if (timeLeft <= 0) {
                    loop.stop();
                    gameOverScreen.show();
                    return;
                }
            }

            // Update scores
            int currentScore = game.getSnake().size() - 2;
            updateScoreLabel(currentScore);

            // Game over logic
            if (game.isGameOver()) {
                gameOverScreen.show();
                int finalScore = game.getSnake().size() - 2;
                updateHighScore(finalScore);
                totalGamesPlayed++; // For stats screen
                loop.stop();
            }
        }));

        loop.setCycleCount(Timeline.INDEFINITE);
        loop.play();
    }

    private void togglePause() {
        paused = !paused;

        if (paused) {
            if (loop != null)
                loop.pause();
            if (pauseScreen != null)
                pauseScreen.show();
        } else {
            if (pauseScreen != null)
                pauseScreen.hide();
            if (loop != null)
                loop.play();
        }
    }

    private void resetScoreLabels() {
        classicScoreLabel.setText("Score: 0");
        classicHighScoreLabel.setText("High Score: 0");
        timedScoreLabel.setText("Score: 0");
        timedHighScoreLabel.setText("High Score: 0");
    }

    private void updateScoreLabel(int score) {
        if (gameMode == MODE_CLASSIC) {
            classicScoreLabel.setText("Score: " + score);
        } else if (gameMode == MODE_TIMED) {
            timedScoreLabel.setText("Score: " + score);
        }
    }

    private void updateHighScore(int score) {
        Label highLabel = (gameMode == MODE_CLASSIC) ? classicHighScoreLabel : timedHighScoreLabel;
        if (highLabel == null)
            return; // Safety check

        try {
            String text = highLabel.getText();
            int currentHigh = 0;

            if (text.contains(":")) {
                String[] parts = text.split(":");
                if (parts.length > 1) {
                    currentHigh = Integer.parseInt(parts[1].trim());
                }
            } else {
                // Parse the whole string as a number
                if (!text.trim().isEmpty()) {
                    currentHigh = Integer.parseInt(text.trim());
                }
            }

            // Update if new score is higher
            if (score > currentHigh) {
                highLabel.setText("High Score: " + score);
            }
        } catch (NumberFormatException e) {
            // If parsing fails, set the new score
            highLabel.setText("High Score: " + score);
        }

        if (gameMode == MODE_CLASSIC) {
            if (score > classicHighScore)
                classicHighScore = score;
        } else if (gameMode == MODE_TIMED) {
            if (score > timedHighScore)
                timedHighScore = score;
        }
    }

    public void buildGrid(int newRows, int newColumns) {
        this.rows = newRows;
        this.columns = newColumns;

        // Clear the old grid if it exists
        gameGrid.getChildren().clear();
        gameGrid.setStyle("-fx-background-color: #f4c064;");

        // Initialize cells
        cells = new Rectangle[columns][rows];

        // Default movement type
        MovementType movementType = new ClassicMovement();

        // Set movement type
        if (gameMovementType == MOVEMENT_CLASSIC) {
            movementType = new ClassicMovement();
        } else if (gameMovementType == MOVEMENT_WRAP) {
            movementType = new WrapMovement();
        }

        game = new SnakeGame(columns, rows, movementType);
        game.setBombsEnabled(true);

        // Max game size
        double maxGameWidth = screenWidth * 0.6;
        double maxGameHeight = screenHeight * 0.8;

        // Cell size based on width, columns, height and rows
        double cellFromWidth = maxGameWidth / columns;
        double cellFromHeight = maxGameHeight / rows;

        // Initialize with the smaller of the two
        double cellSize = Math.floor(Math.min(cellFromWidth, cellFromHeight));

        // Avoid gaps from GridPane spacing
        gameGrid.setHgap(0);
        gameGrid.setVgap(0);

        // Create grid cells
        for (int x = 0; x < columns; x++) {
            for (int y = 0; y < rows; y++) {
                Rectangle cell = new Rectangle(cellSize + 0.6, cellSize + 0.6); // + 0.6 to prevent segment gaps
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