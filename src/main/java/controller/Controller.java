package controller;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import model.Simulation;

public class Controller {
  Simulation simulation;

  @FXML
  TextField runtimeField;

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
    try {
      simulation = new Simulation(this, Integer.parseInt(runtimeField.getText()));
      startBtn.setDisable(true);
      simulation.start();
      simulation.join();
      startBtn.setDisable(false);
    } catch (NumberFormatException e) {
      addDetails("Please use whole numbers and nothing else.");
    } catch (Exception e) {
      addDetails(e.toString());
    }
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
