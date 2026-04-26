package model;

import java.util.ArrayList;
import java.util.List;

import model.serviceObjects.ServicePointTree;
import model.serviceObjects.ServicePointType;

/// This thread is started by instance.startThread(); instead of instance.start();
public class ServicePoint extends Thread {
  private static int _id;
  private int id;
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
  private int nextServiceCount;
  private ServicePoint nextPoint;

  public ServicePoint(Simulation _simulation, int _serviceTime, int _nextServiceCount) {
    STARTTIME = simulation.getTime();
    SERVICETIME = _serviceTime;
    nextServiceCount = _nextServiceCount;
    setId();
    continueTime = _simulation.getTime();
    simulation = _simulation;
  }

  private synchronized void setId() {
    id = _id++;
  }

  public int getSPId() {
    return id;
  }

  public void run() {
    System.out.println("Thread started!");
    if (customerWaitTime < 0) {
      return;
    }
    // this means it is active
    if (isActive() < 0) {
      startService();
      return;
    }
    endService();
  }

  /** B1, 2 or 3 (start activity) */
  private void startService() {
    arrived++;
    nextPoint = ServicePointType.getNextService(this, simulation.getServiceRoot());
    int nextTime = simulation.getTime() + SERVICETIME;
    Simulation.scheduleA(nextTime);
    Simulation.scheduleB(new Event(nextTime, this));
  }

  /** B5,6,7 (finish activity) */
  public void endService() {
    served++;
    activeTime += SERVICETIME; // second 5 - second 2 = 3 seconds active
    customerWait.add(customerWaitTime + SERVICETIME);
    // TODO: nextPoint is set up, so go to its router's queue
    // sim.addC nextPoint
  }

  /** For isActive check, use == 0 */
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

  public int getServiceCount() {
    return nextServiceCount;
  }

}
