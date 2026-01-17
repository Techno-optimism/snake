import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.text.Font;

public class WindowBuilder extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("My Awesome Window");

        Pane pane = new Pane();
        pane.setPrefSize(745, 367);
        pane.setStyle("-fx-background-color: #f4c064;");

        TextField element1 = new TextField("");
        element1.setLayoutX(372.00);
        element1.setLayoutY(199.01);
        element1.setPrefWidth(50.00);
        element1.setPrefHeight(21.00);
        element1.setPromptText("");
        element1.setFont(Font.loadFont(getClass().getResourceAsStream("/resources/fonts/Lexend.ttf"), 14.00));
        element1.setStyle("-fx-background-color: #ffe7bf; -fx-text-fill: #000; -fx-border-color: #000; -fx-border-width: 1px; -fx-border-radius: 2px; -fx-prompt-text-fill: #73664e;");
        pane.getChildren().add(element1);

        Label element2 = new Label("Input your board size");
        element2.setLayoutX(287.53125);
        element2.setLayoutY(157.99999237060547);
        element2.setPrefWidth(198);
        element2.setPrefHeight(27);
        element2.setFont(Font.loadFont(getClass().getResourceAsStream("/resources/fonts/Lexend.ttf"), 18.00));
        element2.setStyle("-fx-text-fill: #000;");
        pane.getChildren().add(element2);

        Label element3 = new Label("Rows:");
        element3.setLayoutX(299);
        element3.setLayoutY(201.1953125);
        element3.setPrefWidth(45);
        element3.setPrefHeight(18);
        element3.setFont(Font.loadFont(getClass().getResourceAsStream("/resources/fonts/Lexend.ttf"), 14.00));
        element3.setStyle("-fx-text-fill: #000;");
        pane.getChildren().add(element3);

        Label element4 = new Label("Columns:");
        element4.setLayoutX(299);
        element4.setLayoutY(228.20050048828125);
        element4.setPrefWidth(62);
        element4.setPrefHeight(18);
        element4.setFont(Font.loadFont(getClass().getResourceAsStream("/resources/fonts/Lexend.ttf"), 14.00));
        element4.setStyle("-fx-text-fill: #000;");
        pane.getChildren().add(element4);

        TextField element5 = new TextField("");
        element5.setLayoutX(372.00);
        element5.setLayoutY(227.70);
        element5.setPrefWidth(50.00);
        element5.setPrefHeight(21.00);
        element5.setPromptText("");
        element5.setFont(Font.loadFont(getClass().getResourceAsStream("/resources/fonts/Lexend.ttf"), 14.00));
        element5.setStyle("-fx-background-color: #ffe7bf; -fx-text-fill: #000; -fx-border-color: #000; -fx-border-width: 1px; -fx-border-radius: 2px; -fx-prompt-text-fill: #73664e;");
        pane.getChildren().add(element5);

        Scene scene = new Scene(pane, 745, 367);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}