package view;

import java.io.IOException;

import controller.Controller;
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
  Controller controller = new Controller();

  public static void main(String[] args) {
    launch(args);
  }

  public void start(Stage stage) {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/UI.fxml"));

    fxmlLoader.setController(controller);
    Parent root;
    try {
      root = fxmlLoader.load();
    } catch (IOException e) {
      System.err.println("FXMLLoader Failed To .load(): " + e);
      Platform.exit();
      return;
    }
    stage.setScene(new Scene(root));
    stage.show();
  }
}
