package view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.fxml.*;

/**
 * Hello world!
 */
public class App extends Application {
  @Override
  public void start(Stage primaryStage) {
    Label label = new Label("Hello @FontFace");
    label.setStyle("-fx-font-family: sample; -fx-font-size: 80;");
    Scene scene = new Scene(label);
    scene.getStylesheets().add("http://font.samples/web?family=samples");
    primaryStage.setTitle("Hello @FontFace");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }

}
