import java.awt.Point;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import Audio.Sound;

public class SnakeGame {
    private final int width;
    private final int height;

    private Deque<Point> snake;
    private Direction direction;
    private Point food;
    private boolean ateFood;
    private boolean gameOver;
    private MovementType movementType;

    private static final int APPLE_RED = 1;
    private static final int APPLE_BLUE = 2;
    private static final int APPLE_PURPLE = 3;
    private int appleType;

    private final Random random = new Random();
    private Deque<Direction> inputQueue = new ArrayDeque<>();

    private Sound music;
    private Sound effects;

    public SnakeGame(int width, int height, MovementType movementType, Sound music, Sound effects) {
        this.width = width;
        this.height = height;
        this.movementType = movementType;

        this.music = music;
        this.effects = effects;

        int cx = width / 2;
        int cy = height / 2;

        snake = new ArrayDeque<>();

        // Stop background music when game starts
        // stopBKMusic(0);

        // Head
        snake.addFirst(new Point(cx, cy));
        // Body
        snake.addLast(new Point(cx, cy + 1));

        // Initial direction is LEFT
        this.direction = Direction.LEFT;
        gameOver = false;

        // First food
        spawnFood(); 
    }

    public void setDirection(Direction newDir) {
        if (inputQueue.size() < 2) {
            inputQueue.addLast(newDir);
        }
    }

    public void step() {
        if (gameOver) return;

        ateFood = false;

        if (!inputQueue.isEmpty()) {
            Direction nextMove = inputQueue.removeFirst();

            boolean isOpposite = 
                (direction == Direction.UP && nextMove == Direction.DOWN) ||
                (direction == Direction.DOWN && nextMove == Direction.UP) ||
                (direction == Direction.LEFT && nextMove == Direction.RIGHT) ||
                (direction == Direction.RIGHT && nextMove == Direction.LEFT);

            if (!isOpposite) {
                direction = nextMove; // Update direction if valid
            }
        }

        Point head = snake.peekFirst();
        Point newHead = movementType.calculateNextHead(head, direction, width, height);

        // Wall collision
        if (newHead == null) {
            gameOver = true;
            return;
        }

        // Self collision
        if (snake.contains(newHead)) {
            gameOver = true;
            return;
        }

        // Normal move: add head
        snake.addFirst(newHead);

        // Food
        if (newHead.equals(food)) {
            ateFood = true;
            // Tail isn't removed, snake grows
            spawnFood();
            // Sound effect for eating food
            playEating(1);
        } else {
            // Move without growing, remove tail
            snake.removeLast();
        }
    }

    public Deque<Point> getSnake() {
        return snake;
    }

    public Point getFood() {
        return food;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void spawnFood() {
        // Runs until a valid food position is found
        while (true) {
            int fx = random.nextInt(width);
            int fy = random.nextInt(height);
            Point candidate = new Point(fx, fy);
            // Any cell not on snake becomes food
            if (!snake.contains(candidate)) {
                food = candidate;

                int roll = random.nextInt(100);
                if (roll < 70) {
                    appleType = APPLE_RED;
                } else if (roll < 90) {
                    appleType = APPLE_BLUE;
                } else if (roll < 100) {
                    appleType = APPLE_PURPLE;
                }
                return;
            }
        }
    }
    
    public boolean hasEatenFood() {
        return ateFood;
    }

    // Reset method called on restart click
    // Resets snake, direction, food, and gameOver state
    public void reset() {
        snake.clear();
        inputQueue.clear(); // Clear input queue, so the snake doesn't move on new game

        int cx = width / 2;
        int cy = height / 2;

        // Initial head
        snake.addFirst(new Point(cx, cy));
        // Initial tail
        snake.addLast(new Point(cx, cy + 1));

        // Initial direction is left
        direction = Direction.LEFT;
        gameOver = false;

        // Initial food
        spawnFood(); 
    }

    public Direction getDirection() {
        return this.direction;
    }

    public int getAppleType() {
        return this.appleType;
    }

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    //playSE used for sound effect
    public void playEating(int i) {
        effects.setFile(i);
        effects.play();
    }
}