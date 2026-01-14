import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class PauseScreen extends StackPane {

    public PauseScreen(String iconPath) {
        setVisible(false);
        setPickOnBounds(true);

        Rectangle dim = new Rectangle();
        dim.setStyle("-fx-fill: rgba(0,0,0,0.55);");
        dim.widthProperty().bind(widthProperty());
        dim.heightProperty().bind(heightProperty());

        Image img = new Image(iconPath);
        ImageView icon = new ImageView(img);
        icon.setPreserveRatio(true);
        icon.setFitWidth(160);

        addEventFilter(MouseEvent.ANY, e -> e.consume());

        getChildren().addAll(dim, icon);
    }

    public void show() { setVisible(true); }
    public void hide() { setVisible(false); }
}
