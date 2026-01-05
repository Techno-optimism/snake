import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Grid extends Application {

    @Override
    public void start(Stage stage) {
        GridPane pane = new GridPane();

        int n = 15;
        int m = 12;
        int cellSize = 50;

        for (int col = 0; col < n; col++) {
            for (int row = 0; row < m; row++) {
                Rectangle cell = new Rectangle(cellSize, cellSize);
                cell.setFill(Color.web("#f4c064"));
                cell.setStroke(Color.web("#c0a060"));
                pane.add(cell, col, row);      
            }
        }

        int sceneWidth  = n * cellSize;
        int sceneHeight = m * cellSize;

        Scene scene = new Scene(pane, sceneWidth, sceneHeight);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.sizeToScene();

        stage.show();
    }

    // #1b5e20 (snake color)
    // #ff5555 (bright food color)
    // #b71c1c (darker food color)
    // #c0a060 (grid color)

    public static void main(String[] args) {
        launch(args);
    }
}
