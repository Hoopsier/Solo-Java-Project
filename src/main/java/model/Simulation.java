package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.distribution.ExponentialDistribution;

import controller.Controller;
import model.phases.APhase;
import model.phases.BPhase;
import model.phases.CPhase;
import model.serviceObjects.ServicePointTree;

public class Simulation extends Thread {
  private Controller viewController;
  private int time = 0;
  private final int MAXTIME;

  /// times to go over
  private Set<Integer> timeAdvanceSet = new HashSet<>();
  private List<Integer> sortedTimeAdvanceSet;
  /// start or end scheduled
  private EventQueue serviceStartOrEndEvents = new EventQueue();
  /// where to start, so conditionals
  private EventQueue routingEvents = new EventQueue();
  private ServicePointTree root;
  private int[] rushHours;

  public Simulation(Controller _controller, int _MAXTIME, int[] _rushHours) {
    viewController = _controller;
    MAXTIME = _MAXTIME;
    rushHours = _rushHours;
    System.out.println("created");
  }

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
          viewController.addData(time, root.getCustomersInSystem(), root.getAverageServingTime());
        }
        try {
          Thread.sleep(50);
        } catch (InterruptedException e) {
          addDetails("INTERRUPTED: " + e);
        }
      }
      addDetails("DONE!!");
    } finally {
      if (viewController != null) {
        viewController.enableButton();
      }
    }
  }

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

  private synchronized void scheduleA(int time) {
    if (time < this.time) {
      return;
    }

    timeAdvanceSet.add(time);
    sortedTimeAdvanceSet = new ArrayList<>(timeAdvanceSet);
    Collections.sort(sortedTimeAdvanceSet);
  }

  public synchronized void scheduleB(Event event) {
    scheduleA(event.getTime());
    serviceStartOrEndEvents.addToQueue(event);
  }

  public synchronized void scheduleC(Event event) {
    scheduleA(event.getTime());
    routingEvents.addToQueue(event);
  }

  public synchronized void addDetails(String text) {
    if (viewController != null) {
      viewController.addDetails(text);
    }
  }

  public synchronized int getTime() {
    return time;
  }

  public synchronized ServicePointTree getServiceRoot() {
    return root;
  }

  /**
   * This method should take in the rush hours and downtime hours, and mold the
   * chances of a customer calling proporsionally
   */
  private void customerGenerator() {
    ExponentialDistribution expDist = new ExponentialDistribution(1.0);
    ExponentialDistribution expDistRush = new ExponentialDistribution(5.0);

    for (int i = 0; i < MAXTIME; i++) {
      while (expDist.sample() > 1 && !isRushHour(i)) {
        scheduleC(new Event(i, root.getSelf()));
      }
      while (expDistRush.sample() > 1 && isRushHour(i)) {
        scheduleC(new Event(i, root.getSelf()));
      }
    }
  }

  private boolean isRushHour(int _time) {
    for (int i : rushHours) {
      if (_time < i && _time >= i)
        return true;
    }
    return false;
  }
}
