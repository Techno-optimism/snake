import java.awt.Point;

public interface MovementType {
    Point calculateNextHead(Point currentHead, SnakeGame.Direction direction, int width, int height);
}