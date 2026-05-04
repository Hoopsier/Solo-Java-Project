package controller;

import java.util.Arrays;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import model.Simulation;

public class Controller {
  Simulation simulation;

  @FXML
  TextField runtimeFieldM;

  @FXML
  TextField runtimeFieldH;

  @FXML
  TextField rushHoursField;

  @FXML
  TextField delayField;

  @FXML
  TextField customerQtt;

  @FXML
  Button startBtn;

  @FXML
  private void startSimulation() {
    startBtn.setDisable(true);
    try {
      int[] rushHours = Arrays.stream(
          rushHoursField.getText()
              .split(","))
          .mapToInt(s -> Integer.parseInt(s.trim()))
          .toArray();
      addDetails(Arrays.toString(rushHours));
      simulation = new Simulation(this,
          Integer.parseInt(runtimeFieldH.getText()) * 60 + Integer.parseInt(runtimeFieldM.getText()), rushHours);
      simulation.start();
      simulation.join();
    } catch (NumberFormatException e) {
      addDetails("Please use whole numbers and nothing else.");
    } catch (Exception e) {
      addDetails(e.toString());
    }
    startBtn.setDisable(false);
  }

  public void addDetails(String text) {
    detailsText.setText(Detailinator.parse(text + "\n", detailsText.getText()));
  }

  @FXML
  Text detailsText;

  @FXML
  LineChart<Double, Double> dataGraph;

  public Controller() {
  }
}
