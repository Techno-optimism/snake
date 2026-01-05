import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Gui extends Application {
    public void start(Stage stage) throws Exception {
        GridPane pane = new GridPane();
        int n = 5;
        int m = 5;
        pane.addRow(0, new Rectangle(800/n,800/n,Color.GREEN));
        pane.addColumn(0, new Rectangle(800/m,800/m,Color.RED));
        pane.addColumn(1, new Rectangle(800/m,800/m,Color.RED));
        pane.add(new Rectangle(50,50,Color.BLUE), m-2, n-2);
        pane.add(new Rectangle(50,50,Color.BLUE), m-1, n-2);
        pane.add(new Rectangle(50,50,Color.BLUE), m, n);



        stage.setScene(new Scene(pane, 800, 800));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
