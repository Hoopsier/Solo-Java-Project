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

/**
 * JavaFX controller for the simulation UI, including input parsing, graph
 * updates, delay controls, and status text updates.
 */
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

  /**
   * Parses the UI inputs, creates a simulation, and starts it on its own thread.
   */
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
      simulation.setDelay(Integer.parseInt(delayField.getText()));
      simulation.start();
    } catch (NumberFormatException e) {
      addDetails("Please use whole numbers and nothing else.");
      startBtn.setDisable(false);
    } catch (Exception e) {
      addDetails(e.toString());
      startBtn.setDisable(false);
    }
  }

  /**
   * Increases the running simulation's tick delay.
   */
  @FXML
  private void increaseDelay() {
    if (simulation == null) {
      return;
    }
    simulation.incDelay();
  }

  /**
   * Decreases the running simulation's tick delay.
   */
  @FXML
  private void decreaseDelay() {
    if (simulation == null) {
      return;
    }
    simulation.decDelay();
  }

  /**
   * Initializes chart labels, series names, and default graph state after FXML
   * loading.
   */
  @FXML
  private void initialize() {
    customersInSystemData.setName("Customers in system");
    averageServingTimeData.setName("Average serving time");
    dataGraph.setTitle("Simulation metrics by time step");
    dataGraph.getXAxis().setLabel("Time step");
    dataGraph.getYAxis().setLabel("Amount (min or quantity)");
    dataGraph.setCreateSymbols(false);
    dataGraph.getData()
        .setAll(Arrays.<XYChart.Series<Number, Number>>asList(averageServingTimeData, customersInSystemData));
    clearGraph();
  }

  /**
   * Adds a data point for both graph series on the JavaFX application thread.
   *
   * @param time simulation time of the data point
   * @param customersInSystem number of customers currently in the system
   * @param averageServingTime average completed serving time
   */
  public synchronized void addData(int time, int customersInSystem, double averageServingTime) {
    Platform.runLater(() -> {
      if (customersInSystemData != null && customersInSystemData.getData() != null) {
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
    });
  }

  /**
   * Appends detail text to the UI details log on the JavaFX application thread.
   *
   * @param text detail text to display
   */
  public synchronized void addDetails(String text) {
    Platform.runLater(() -> detailsText.setText(Detailinator.parse(text + "\n", detailsText.getText())));
  }

  /**
   * Re-enables the start button after a simulation stops or fails.
   */
  public void enableButton() {
    Platform.runLater(() -> startBtn.setDisable(false));
  }

  /**
   * Creates a controller instance for JavaFX or tests.
   */
  public Controller() {
  }

  /**
   * Clears all graph data series.
   */
  private void clearGraph() {
    customersInSystemData.getData().clear();
    averageServingTimeData.getData().clear();
  }
}
