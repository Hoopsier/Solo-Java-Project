package model;

import java.util.ArrayList;
import java.util.List;

/// This thread is started by instance.startThread(); instead of instance.start();
public class ServicePoint extends Thread {
  private int arrived = 0;
  private int served = 0;
  private int customerWaitTime;
  private List<Integer> customerWait = new ArrayList<>();
  /// delta time time active
  private int activeTime = 0;
  private final int STARTTIME;
  private int continueTime = 0;
  /// how long the service will take
  private final int SERVICETIME;

  protected Simulation simulation;
  protected ServiceType serviceType; // TODO: implement usage

  public ServicePoint(Simulation simulation, ServiceType _serviceType) {
    STARTTIME = simulation.getTime();
    SERVICETIME = 5;
    constructionHelper(simulation, _serviceType);
  }

  public ServicePoint(Simulation simulation, ServiceType _serviceType, int _serviceTime) {
    STARTTIME = simulation.getTime();
    SERVICETIME = _serviceTime;
    constructionHelper(simulation, _serviceType);
  }

  private void constructionHelper(Simulation simulation, ServiceType _serviceType) {
    continueTime = simulation.getTime();
    this.simulation = simulation;
    serviceType = _serviceType;
  }

  /// This is called whenever a customer arrives.
  public void run() {
    System.out.println("Thread started!");
    if (isActive() < 0) {
      startService();
      return;
    }
    endService();
  }

  /// This method contains custom variables, that need to be updated every time
  /// the thread starts. This is like this, for code localization reasons.
  public void startThread(int _customerWaitTime) {
    customerWaitTime = _customerWaitTime;
    this.start();
  }

  private void startService() {
    arrived++;
    // TODO: Schedule end time
  }

  /// This is called at the end of serving a customer
  private void endService() {
    served++;
    activeTime += SERVICETIME; // second 5 - second 2 = 3 seconds active
    customerWait.add(customerWaitTime);
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

  public ServiceType getServiceType() {
    return serviceType;
  }
}
