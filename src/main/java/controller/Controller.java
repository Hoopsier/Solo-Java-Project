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
  Button startBtn;

  @FXML
  private void startSimulation() {
    try {
      simulation = new Simulation(this, Integer.parseInt(runtimeField.getText()));
      startBtn.setDisable(true);
      simulation.start();
    } catch (NumberFormatException e) {
      detailsText.setText(Detailinator.parse("Please use whole numbers and nothing else.\n", detailsText.getText()));
    } catch (Exception e) {
      detailsText.setText(detailsText.getText().concat(e.toString()));
    }
  }

  @FXML
  Text detailsText;

  @FXML
  LineChart<Double, Double> dataGraph;

  public Controller() {
  }
}
