import java.awt.Point;

public class WrapMovement implements MovementType {
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

        // Wrap around logic
        if (x < 0) x = width - 1;
        else if (x >= width) x = 0;
        if (y < 0) y = height - 1;
        else if (y >= height) y = 0;

        return new Point(x, y);
    }

}