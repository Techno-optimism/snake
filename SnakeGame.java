import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
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
    private int pendingGrowth = 0; // how many segments to grow over upcoming steps
    private int lastEatenAppleType = 0;
    private int foodTtl, redTtl, blueTtl, purpleTtl;

    private static final int BOMB = 4;
    private static final int DEATH = 5;
    private static final int RIP = 6;
    private final ArrayList<Bomb> bombs = new ArrayList<>();
    private boolean bombsEnabled = false;
    private int bombTTL = 30; // lives 30 ticks
    private double bombChance = 0.01;

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

        // Calculate and set the TTL dynamically based on grid size
        int maxDistance = width + height;
        this.redTtl = (int) (maxDistance * 2.0);
        this.blueTtl = (int) (maxDistance * 1.2);
        this.purpleTtl = (int) (maxDistance * 0.7);

        // The initial snake position
        int cx = width / 2;
        int cy = height / 2;

        snake = new ArrayDeque<>();

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
        if (gameOver)
            return;

        ateFood = false;

        if (!inputQueue.isEmpty()) {
            Direction nextMove = inputQueue.removeFirst();

            boolean isOpposite = (direction == Direction.UP && nextMove == Direction.DOWN) ||
                    (direction == Direction.DOWN && nextMove == Direction.UP) ||
                    (direction == Direction.LEFT && nextMove == Direction.RIGHT) ||
                    (direction == Direction.RIGHT && nextMove == Direction.LEFT);

            if (!isOpposite) {
                direction = nextMove; // Update direction if valid
            }
        }

        Point head = snake.peekFirst();
        Point newHead = movementType.calculateNextHead(head, direction, width, height);

        int roll = random.nextInt(100);

        // Wall collision (always checked first)
        if (newHead == null) {
            stopMusic();
            playEffect(DEATH);
            if (roll < 10) {
                playEffect(RIP);
            }
            gameOver = true;
            return;
        }

        // Check if the snake is about to eat food
        boolean eatingFood = newHead.equals(food);

        // If not eating and not growing from previous food, the tail is removed temporarily to check if new head position is safe
        // This essentially removes the tail from the board making its grid coordinate empty
        Point oldTail = null;
        boolean willGrow = (eatingFood || pendingGrowth > 0);

        if (!willGrow) {
            oldTail = snake.removeLast(); // Temporary removal
        }

        // Now self-collision is checked
        // Since the tail is free, it can move there (move in circles when length = 4)
        // Self collision
        if (snake.contains(newHead)) {
            // Puts the tail back for visual consistency
            if (oldTail != null) {
                snake.addLast(oldTail);
            }
            stopMusic();
            playEffect(DEATH);
            if (roll < 10) {
                playEffect(RIP);
            }
            gameOver = true;
            return;
        }

        // Move is safe (add head)
        snake.addFirst(newHead);

        if (bombsEnabled) {
            for (Bomb b : bombs) {
                if (b.pos.equals(newHead)) { // or snake.peekFirst()
                    playEffect(BOMB);
                    playEffect(DEATH);
                    gameOver = true;
                    return; // stop this step immediately
                }
            }
        }

        // Food
        if (eatingFood) {
            ateFood = true;
            lastEatenAppleType = appleType;

            if (appleType == APPLE_RED) {
                playEffect(APPLE_RED);
            } else if (appleType == APPLE_BLUE) {
                pendingGrowth += 1;
                playEffect(APPLE_BLUE);
            } else if (appleType == APPLE_PURPLE) {
                pendingGrowth += 2;
                playEffect(APPLE_PURPLE);
            }

            // Spawn new food
            spawnFood();
        }

        // Decrement pendingGrowth if it was used
        if (willGrow && pendingGrowth > 0) {
            pendingGrowth--;
        }

        // Handle food TTL
        if (foodTtl > 0) {
            foodTtl--;
            if (foodTtl == 0) {
                spawnFood(); // despawn and respawn
            }
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
            if (!isOccupied(candidate)) {
                food = candidate;

                int roll = random.nextInt(100);
                if (roll < 85) {
                    appleType = APPLE_RED;
                } else if (roll < 95) {
                    appleType = APPLE_BLUE;
                } else {
                    appleType = APPLE_PURPLE;
                }

                if (appleType == APPLE_BLUE)
                    foodTtl = blueTtl;
                else if (appleType == APPLE_PURPLE)
                    foodTtl = purpleTtl;
                else
                    foodTtl = redTtl; // Red apple's TTL

                return;
            }
        }
    }

    public void setBombTTL(int ttl) {
        this.bombTTL = ttl;
    }

    private boolean isOccupied(Point p) {
        if (snake.contains(p))
            return true;
        if (food != null && food.equals(p))
            return true;
        for (Bomb b : bombs)
            if (b.pos.equals(p))
                return true;
        return false;
    }

    private boolean isNearHead(Point p) {
        Point h = snake.peekFirst();
        int dx = Math.abs(p.x - h.x);
        int dy = Math.abs(p.y - h.y);
        return dx + dy <= 1; // blocks head + 4 neighbors
    }

    private Point randomFreeCell(java.util.Random random) {
        for (int i = 0; i < 200; i++) {
            Point p = new Point(random.nextInt(width), random.nextInt(height));
            if (isOccupied(p))
                continue;
            if (isNearHead(p))
                continue; 
            return p;
        }
        return null;
    }

    public int consumeLastEatenAppleType() {
        int t = lastEatenAppleType;
        lastEatenAppleType = 0;
        return t;
    }

    public void updateBombs(java.util.Random random) {
        if (!bombsEnabled)
            return;

        // Despawn old bombs
        for (int i = bombs.size() - 1; i >= 0; i--) {
            Bomb b = bombs.get(i);
            b.ttl--;
            if (b.ttl <= 0)
                bombs.remove(i);
        }

        // Chance to spawn new bomb
        if (random.nextDouble() < bombChance) {
            Point p = randomFreeCell(random);
            if (p != null)
                bombs.add(new Bomb(p, bombTTL));
        }
    }

    public void setBombsEnabled(boolean enabled) {
        this.bombsEnabled = enabled;
    }

    public ArrayList<Bomb> getBombs() {
        return bombs;
    }

    public static class Bomb {
        public final Point pos;
        public int ttl;

        public Bomb(Point pos, int ttl) {
            this.pos = pos;
            this.ttl = ttl;
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
        bombs.clear();

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
        UP, DOWN, LEFT, RIGHT, W, S, A, D
    }

    // Plays effect based on the given ID
    public void playEffect(int i) {
        effects.setFile(i);
        effects.play();
    }

    public void stopAllEffects() {
        effects.stop();
    }

    public void stopMusic() {
        music.stop();
    }
}