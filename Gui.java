import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Gui extends Application {
    public void start(Stage stage) throws Exception {
        StackPane pane = new StackPane(
            
        );

        stage.setScene(new Scene(pane, 300, 300));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
