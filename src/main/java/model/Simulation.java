package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.distribution.ExponentialDistribution;

import controller.Controller;
import model.database.SimulationResultRepository;
import model.phases.APhase;
import model.phases.BPhase;
import model.phases.CPhase;
import model.serviceObjects.ServicePointTree;

/**
 * Runs the discrete-event call-center simulation and coordinates all simulation
 * phases, event queues, generated customers, UI updates, and persistence of
 * performance results.
 */
public class Simulation extends Thread {
  private Controller viewController;
  private int time = 0;
  private final int MAXTIME;

  /** Times scheduled for the next phase-A advance. */
  private Set<Integer> timeAdvanceSet = new HashSet<>();
  private List<Integer> sortedTimeAdvanceSet;
  /** Events that start or end service at a service point. */
  private EventQueue serviceStartOrEndEvents = new EventQueue();
  /** Events that route customers to their next service point. */
  private EventQueue routingEvents = new EventQueue();
  private ServicePointTree root;
  private int[] rushHours;
  private final List<Integer> customerResponseTimes = new ArrayList<>();
  private final SimulationResultRepository resultRepository = new SimulationResultRepository();
  public int delay;

  /**
   * Creates a simulation thread.
   *
   * @param _controller UI controller to receive data and log updates, or
   *        {@code null} for headless execution
   * @param _MAXTIME maximum simulation time in minutes
   * @param _rushHours rush-hour start hours, where each value represents the
   *        first minute of a 60-minute rush period when multiplied by 60
   */
  public Simulation(Controller _controller, int _MAXTIME, int[] _rushHours) {
    viewController = _controller;
    MAXTIME = _MAXTIME;
    rushHours = _rushHours;
    System.out.println("created");
  }

  /**
   * Builds the service tree, generates customer arrivals, advances simulation
   * phases until the configured maximum time, records UI metrics, and saves the
   * final performance data.
   */
  @Override
  public void run() {
    try {
      addDetails("Started~!");
      // loaded here in order to not lag the app
      // (this creates the entire tree with a lot of objects)
      int[][] rootBranchOdds = { { 33, 0 }, { 66, 1 }, { 100, 2 } }; // three languages each
      root = new ServicePointTree(
          new ServicePoint(this, 1, rootBranchOdds), 1, this);

      // viewController.addDetails(Integer.toString(root.serviceTotalCount()));
      root.printTree(1);
      customerGenerator();

      while (time < MAXTIME) {
        time = advanceTime();
        addDetails("Time advanced to (" + Integer.toString(time) + ")");
        addDetails("BQueue passed with " + BPhase.activate(serviceStartOrEndEvents, time) + " iterations");

        while (CPhase.activate(routingEvents, this, time))
          System.out.print(""); // Just in case the optimizer skips this loop

        addDetails("CQueues passed");

        if (viewController != null) {
          viewController.addData(time, getCustomersInSystem(), root.getAverageServingTime());
        }
        try {
          Thread.sleep(delay);
        } catch (InterruptedException e) {
          addDetails("INTERRUPTED: " + e);
        }
      }
      savePerformanceData();
      addDetails("DONE!!");
    } finally {
      if (viewController != null) {
        viewController.enableButton();
      }
    }
  }

  /**
   * Advances to the next scheduled simulation time.
   *
   * @return next scheduled time, or {@code MAXTIME} when no more events are
   *         scheduled
   */
  private synchronized int advanceTime() {
    if (timeAdvanceSet.isEmpty())
      return MAXTIME;
    sortedTimeAdvanceSet = new ArrayList<>(timeAdvanceSet);
    Collections.sort(sortedTimeAdvanceSet);

    int nextTime = APhase.activate(sortedTimeAdvanceSet, time);
    timeAdvanceSet.removeIf(scheduledTime -> scheduledTime <= nextTime);
    sortedTimeAdvanceSet = new ArrayList<>(timeAdvanceSet);
    Collections.sort(sortedTimeAdvanceSet);
    return nextTime;
  }

  /**
   * Prevents an event from being scheduled before the current simulation time.
   *
   * @param scheduledTime requested event time
   * @return the requested time or the current simulation time, whichever is
   *         later
   */
  private synchronized int normalizeScheduledTime(int scheduledTime) {
    return Math.max(scheduledTime, this.time);
  }

  /**
   * Adds a time to the phase-A schedule.
   *
   * @param time simulation time to schedule
   */
  private synchronized void scheduleA(int time) {
    timeAdvanceSet.add(normalizeScheduledTime(time));
    sortedTimeAdvanceSet = new ArrayList<>(timeAdvanceSet);
    Collections.sort(sortedTimeAdvanceSet);
  }

  /**
   * Schedules a start-service or end-service event.
   *
   * @param event event to normalize, enqueue, and expose to phase A
   */
  public synchronized void scheduleB(Event event) {
    event.setTime(normalizeScheduledTime(event.getTime()));
    scheduleA(event.getTime());
    serviceStartOrEndEvents.addToQueue(event);
  }

  /**
   * Schedules a routing event.
   *
   * @param event event to normalize, enqueue, and expose to phase A
   */
  public synchronized void scheduleC(Event event) {
    event.setTime(normalizeScheduledTime(event.getTime()));
    scheduleA(event.getTime());
    routingEvents.addToQueue(event);
  }

  /**
   * Writes a detail message to the UI when a controller is attached.
   *
   * @param text message to append to the simulation details view
   */
  public synchronized void addDetails(String text) {
    if (viewController != null) {
      viewController.addDetails(text);
    }
  }

  /**
   * Gets the current simulation time.
   *
   * @return current time step in minutes
   */
  public synchronized int getTime() {
    return time;
  }

  /**
   * Gets the root of the service-point tree.
   *
   * @return root service-point tree, or {@code null} before the simulation is
   *         started
   */
  public synchronized ServicePointTree getServiceRoot() {
    return root;
  }

  /**
   * Generates customer routing events across the simulation time span, using a
   * larger exponential distribution during rush hours.
   */
  private void customerGenerator() {
    ExponentialDistribution expDist = new ExponentialDistribution(1.0);
    ExponentialDistribution expDistRush = new ExponentialDistribution(1.5);

    for (int i = 0; i < MAXTIME; i++) {
      while (expDist.sample() > 1 && !isRushHour(i)) {
        scheduleC(new Event(i, root.getSelf()));
      }
      while (expDistRush.sample() > 1 && isRushHour(i)) {
        scheduleC(new Event(i, root.getSelf()));
      }
    }
  }

  /**
   * Records a completed customer's total response time.
   *
   * @param responseTime elapsed time from customer arrival to final service
   *        completion
   */
  public synchronized void recordCustomerResponseTime(int responseTime) {
    customerResponseTimes.add(responseTime);
  }

  /**
   * Saves service point and customer response-time data through the repository,
   * reporting success or failure to the UI details log.
   */
  private void savePerformanceData() {
    try {
      resultRepository.save(root.getAllServicePoints(), new ArrayList<>(customerResponseTimes));
      addDetails("Saved performance data to the database.");
    } catch (Exception e) {
      addDetails("Could not save performance data to the database: " + e.getMessage());
    }
  }

  /**
   * Counts customers currently being served or waiting in scheduled queues.
   *
   * @return number of customers still in the system
   */
  public synchronized int getCustomersInSystem() {
    if (root == null) {
      return 0;
    }
    return root.getCustomersInSystem()
        + routingEvents.countRoutingsInSystem(time)
        + serviceStartOrEndEvents.countByType(Event.Type.START_SERVICE);
  }

  /**
   * Checks whether a simulation time falls within one of the configured rush
   * hours.
   *
   * @param _time simulation time in minutes
   * @return {@code true} when the time is in any configured rush-hour window;
   *         otherwise {@code false}
   */
  public synchronized boolean isRushHour(int _time) {
    for (int i : rushHours) {
      if (_time >= i * 60 && _time < (i + 1) * 60)
        return true;
    }
    return false;
  }

  /**
   * Increases the UI animation delay by ten percent.
   */
  public synchronized void incDelay() {
    delay *= 1.1;
  }

  /**
   * Decreases the UI animation delay by ten percent.
   */
  public synchronized void decDelay() {
    delay *= 0.9;
  }

  /**
   * Sets the UI animation delay between simulation ticks.
   *
   * @param _delay delay in milliseconds
   */
  public void setDelay(int _delay) {
    delay = _delay;
  }
}
