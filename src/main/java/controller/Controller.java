package controller;

import java.util.Arrays;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
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

  private final XYChart.Series<Number, Number> customersInSystemData = new XYChart.Series<>();

  private final XYChart.Series<Number, Number> averageServingTimeData = new XYChart.Series<>();
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

  @FXML
  private void initialize() {
    customersInSystemData.setName("Customers in system");
    averageServingTimeData.setName("Average serving time");
    dataGraph.setTitle("Simulation metrics by time step");
    dataGraph.getXAxis().setLabel("Time step");
    dataGraph.getYAxis().setLabel("Count / time steps");
    dataGraph.setCreateSymbols(false);
    dataGraph.getData()
        .setAll(Arrays.<XYChart.Series<Number, Number>>asList(averageServingTimeData, customersInSystemData));
    clearGraph();
  }

  public synchronized void addData(int time, int customersInSystem, double averageServingTime) {
    Platform.runLater(() -> {
      if (customersInSystemData != null && customersInSystemData.getData() != null) {
        System.out.println(customersInSystem + " : " + time);
        XYChart.Data<Number, Number> data = new XYChart.Data<>(time, customersInSystem);
        customersInSystemData.getData().add(data);
      } else {
        System.err.println("customersInSystemData or its data is null");
      }
    });
    Platform.runLater(() -> {
      XYChart.Data<Number, Number> data = new XYChart.Data<>(time, averageServingTime);
      averageServingTimeData.getData().add(data);
      // Log error for average time data
      dataGraph.getData().forEach((x) -> System.out.println(x.getData()));
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
    customersInSystemData.getData().clear();
    averageServingTimeData.getData().clear();

  }
}
