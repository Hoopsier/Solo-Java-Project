package model;

import java.util.ArrayList;
import java.util.List;

import model.serviceObjects.ServicePointType;

/// This thread is started by instance.startThread(); instead of instance.start();
public class ServicePoint extends Thread {
  private int arrived = 0;
  private int served = 0;
  private int customerWaitTime = -1;
  private List<Integer> customerWait = new ArrayList<>();
  /// delta time time active
  private int activeTime = 0;
  private final int STARTTIME;
  private int continueTime = 0;
  /// how long the service will take
  private final int SERVICETIME;

  private Simulation simulation;
  private ServicePointType nextService;

  public ServicePoint(Simulation simulation, ServicePointType _nextService) {
    STARTTIME = simulation.getTime();
    SERVICETIME = 5;
    nextService = _nextService;
    constructionHelper(simulation);
  }

  public ServicePoint(Simulation simulation, int _serviceTime) {
    STARTTIME = simulation.getTime();
    SERVICETIME = _serviceTime;
    constructionHelper(simulation);
  }

  private void constructionHelper(Simulation _simulation) {
    continueTime = _simulation.getTime();
    simulation = _simulation;
  }

  /// Please call startThread instead.
  public void run() {
    System.out.println("Thread started!");
    if (customerWaitTime < 0) {
      return;
    }
    if (isActive() < 0) {
      startService();
      return;
    }
    endService();
  }

  private void startService() {
    arrived++;
    ServicePoint nextPoint = nextService.getNextService();
    int nextTime = simulation.getTime() + SERVICETIME;
    Simulation.addToAQueue(nextTime);
    Simulation.addToBQueue(new Event(nextTime, EventType.BE, this));
  }

  /// This is called at the end of serving a customer
  public void endService() {
    served++;
    activeTime += SERVICETIME; // second 5 - second 2 = 3 seconds active
    customerWait.add(customerWaitTime + SERVICETIME);
    // TODO: nextPoint is set up, so go to its router's queue
  }

  /// For isActive check, use == 0
  private int isActive() {
    return simulation.getTime() - (continueTime + SERVICETIME); // current time 5, continuetime 0, service time 5,
                                                                // returns zero (which means it's free)
                                                                // while returning more than zero if something is wrong
  }

  public int getArrived() {
    return arrived;
  }

  public int getServed() {
    return served;
  }

  public int getTotalTime() {
    return simulation.getTime() - STARTTIME;
  }

  public int getActiveTime() {
    return activeTime;
  }

  public void setWaitTime(int _waitTime) {
    customerWaitTime = _waitTime;
  }

}
