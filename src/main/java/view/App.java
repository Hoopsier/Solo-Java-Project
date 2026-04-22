package view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Hello world!
 */
public class App extends Application {
  public static void main(String[] args) {
    launch(args);
  }

  public void start(Stage stage) {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/UI.fxml"));

    System.out.println("Hello World!");
  }
}
