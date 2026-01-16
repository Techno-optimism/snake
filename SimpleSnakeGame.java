import java.awt.Point;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

public class SimpleSnakeGame {
    private final int width;
    private final int height;

    private Deque<Point> snake;
    private Direction direction;
    private Point food;
    private boolean gameOver;

    private final Random random = new Random();
    private Deque<Direction> inputQueue = new ArrayDeque<>();

    public SimpleSnakeGame(int width, int height) {
        this.width = width;
        this.height = height;

        int cx = width / 2;
        int cy = height / 2;

        snake = new ArrayDeque<>();

        // Head
        snake.addFirst(new Point(cx, cy));
        // Body
        snake.addLast(new Point(cx, cy + 1));

        // Initial direction is LEFT
        direction = Direction.LEFT;
        gameOver = false;

        // First food
        spawnFood(); 
    }

    // Author: Emil
    // Makes it so user can queue a max of 2 inputs
    public void setDirection(Direction newDir) {
        if (inputQueue.size() < 2) {
            inputQueue.addLast(newDir);
        }
    }

    // Author: Emil
    public void step() {
        if (gameOver) return;

        if (!inputQueue.isEmpty()) {
            Direction nextMove = inputQueue.removeFirst();

            // Prevents user from going opposite direction
            boolean isOpposite = 
                (direction == Direction.UP && nextMove == Direction.DOWN) ||
                (direction == Direction.DOWN && nextMove == Direction.UP) ||
                (direction == Direction.LEFT && nextMove == Direction.RIGHT) ||
                (direction == Direction.RIGHT && nextMove == Direction.LEFT);

            // Update direction if valid
            if (!isOpposite) {
                direction = nextMove; 
            }
        }

        Point head = snake.peekFirst();
        int x = head.x;
        int y = head.y;

        switch (direction) {
            case UP -> y--;
            case W -> y--;
            case DOWN -> y++;
            case S -> y++;
            case LEFT -> x--;
            case A -> x--;
            case RIGHT -> x++;
            case D -> x++;
        }

        x = (x + width) % width; // changes x and y values for toroidal wrapping
        y = (y + height) % height;

        Point newHead = new Point(x, y);

        // Checks

        // Wall collision
        // x and y cant be equal to width or height (because of 0 indexing)
        if (x < 0 || x >= width || y < 0 || y >= height) {
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
            // Tail isn't removed, snake grows
            spawnFood();
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
                return;
            }
        }
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

    public enum Direction {
        UP, DOWN, LEFT, RIGHT, W, S, A, D
    }
}