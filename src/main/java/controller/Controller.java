package controller;

import java.util.Arrays;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import model.Simulation;

public class Controller {
  private Simulation simulation;

  @FXML
  private TextField runtimeFieldM;

  @FXML
  private TextField runtimeFieldH;

  @FXML
  private TextField rushHoursField;

  @FXML
  private TextField delayField;

  @FXML
  private Text detailsText;

  @FXML
  private AreaChart<Number, Number> dataGraph;

  private XYChart.Series<Number, Number> data = new XYChart.Series<Number, Number>();
  @FXML
  private Button startBtn;

  @FXML
  private void startSimulation() {
    clearGraph();
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
    } catch (NumberFormatException e) {
      addDetails("Please use whole numbers and nothing else.");
      startBtn.setDisable(false);
    } catch (Exception e) {
      addDetails(e.toString());
      startBtn.setDisable(false);
    }
  }

  public synchronized void addData(int time, int customers) {
    Platform.runLater(() -> {
      data.getData().add(new XYChart.Data<>(time, customers));
      dataGraph.getData().add(data);
    });
  }

  public synchronized void addDetails(String text) {
    Platform.runLater(() -> detailsText.setText(Detailinator.parse(text + "\n", detailsText.getText())));
  }

  public void enableButton() {
    Platform.runLater(() -> startBtn.setDisable(false));
  }

  public Controller() {
  }

  private void clearGraph() {
    data.getData().clear();
  }
}
