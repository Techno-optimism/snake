import java.awt.Point;

public class ClassicMovement implements MovementType {
    @Override
    public Point calculateNextHead(Point currentHead, SnakeGame.Direction direction, int width, int height) {
        int x = currentHead.x;
        int y = currentHead.y;

        switch (direction) {
            case UP -> y--;
            case DOWN -> y++;
            case LEFT -> x--;
            case RIGHT -> x++;
        }

        // Wall collision check: if out of bounds, return null (Game Over)
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return null; 
        }

        return new Point(x, y);
    }

}