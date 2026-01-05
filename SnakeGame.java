import java.awt.Point;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

public class SnakeGame {
    private final int width;
    private final int height;

    private Deque<Point> snake;
    private Direction direction;
    private Point food;
    private boolean gameOver;

    private final Random random = new Random();

    public SnakeGame(int width, int height) {
        this.width = width;
        this.height = height;

        int cx = width / 2;
        int cy = height / 2;

        snake = new ArrayDeque<>();

        // head
        snake.addFirst(new Point(cx, cy));
        // body
        snake.addLast(new Point(cx, cy + 1));

        // initial direction is LEFT
        direction = Direction.LEFT;
        gameOver = false;

        // first food
        spawnFood(); 
    }

    public void step() {
        if (gameOver) return;

        Point head = snake.peekFirst();
        int x = head.x;
        int y = head.y;

        switch (direction) {
            case UP -> y--;
            case DOWN -> y++;
            case LEFT -> x--;
            case RIGHT -> x++;
        }

        Point newHead = new Point(x, y);

        // Checks

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

    public void setDirection(Direction newDir) {
        // Prevent 180 degree reverse
        if (direction == Direction.UP && newDir == Direction.DOWN) return;
        if (direction == Direction.DOWN && newDir == Direction.UP) return;
        if (direction == Direction.LEFT && newDir == Direction.RIGHT) return;
        if (direction == Direction.RIGHT && newDir == Direction.LEFT) return;
        direction = newDir;
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
        // Any cell not on snake
        while (true) {
            int fx = random.nextInt(width);
            int fy = random.nextInt(height);
            Point candidate = new Point(fx, fy);
            if (!snake.contains(candidate)) {
                food = candidate;
                return;
            }
        }
    }

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}