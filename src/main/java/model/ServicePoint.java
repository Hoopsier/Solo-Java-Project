package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import model.serviceObjects.ServicePointType;

/// This thread is started by instance.startThread(); instead of instance.start();
public class ServicePoint {
  private static int _id;
  private int id;
  private boolean busy;
  private int arrived = 0;
  private int served = 0;
  /// delta time time active
  private int activeTime = 0;
  private final int STARTTIME;
  /// how long the service will take
  private int serviceTime;
  private final int SERVICETIMEBASE;
  private Set<Integer> reservedTimes = new HashSet<>();
  private Simulation simulation;
  private int[][] branchOdds;
  private ServicePoint nextPoint;
  private List<ServicePoint> parallelPoints = new ArrayList<>();
  private int busyUntil = 0;
  private int currentCustomerArrivalTime = -1;
  private boolean isFourth = false;

  public ServicePoint(Simulation _simulation, int _serviceTime, int[][] _branchOdds) {
    STARTTIME = _simulation.getTime();
    serviceTime = _serviceTime;
    SERVICETIMEBASE = _serviceTime;
    branchOdds = _branchOdds;
    setId();
    simulation = _simulation;
  }

  public synchronized void setParallels(int quantity) {
    Random random = new Random();
    for (int i = 0; i < quantity; i++) {
      int parallelServiceTime = Math.max(1, serviceTime + random.nextInt(-2, 2));
      parallelPoints
          .add(new ServicePoint(simulation, parallelServiceTime, branchOdds).setParallelId(id));
    }
  }

  public synchronized List<ServicePoint> getParallels() {
    return parallelPoints;
  }

  private synchronized void setId() {
    id = _id++;
  }

  /** for parallels only */
  private synchronized ServicePoint setParallelId(int id) {
    this.id = id;
    return this;
  }

  public void setFourth() {
    isFourth = true;
  }

  public int getSPId() {
    return id;
  }

  public synchronized boolean isBusy() {
    return busy;
  }

  public synchronized void processBEvent(Event event) {
    if (event.getType() == Event.Type.START_SERVICE) {
      if (simulation.isRushHour(event.getTime())) {
        serviceTime++;
        if (serviceTime > SERVICETIMEBASE + 6) {
          serviceTime = SERVICETIMEBASE + 6;
        }
      } else {
        serviceTime--;
        if (serviceTime < SERVICETIMEBASE) {
          serviceTime = SERVICETIMEBASE;
        }
      }

      startService(event);
      return;
    }

    if (event.getType() == Event.Type.END_SERVICE) {
      endService();
    }
  }

  /** B1, 2 or 3 (start activity) */
  private void startService(Event event) {
    arrived++;
    busy = true;
    reservedTimes.remove(simulation.getTime());
    busyUntil = simulation.getTime() + serviceTime;
    currentCustomerArrivalTime = event.getArrivalTime();
    nextPoint = ServicePointType.getNextService(simulation.getServiceRoot().find(id).getSelf(),
        simulation.getServiceRoot());
    simulation.scheduleB(new Event(busyUntil, this, Event.Type.END_SERVICE));
  }

  /** B5,6,7 (finish activity) */
  public void endService() {
    served++;
    busy = false;
    reservedTimes.removeIf(reservedTime -> reservedTime <= simulation.getTime());
    activeTime += serviceTime; // second 5 - second 2 = 3 seconds active
    if (isFourth) {
      simulation.addDetails("Phase 4 completed at service point " + id + " at time " + simulation.getTime()
          + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }
    // TODO: nextPoint is set up, so go to its router's queue
    // sim.addC nextPoint
    if (nextPoint != null) {
      simulation.scheduleC(new Event(busyUntil, nextPoint, Event.Type.ROUTING, currentCustomerArrivalTime));
    } else {
      simulation.recordCustomerResponseTime(simulation.getTime() - currentCustomerArrivalTime);
    }
    currentCustomerArrivalTime = -1;
  }

  /** For isActive check, use == 0 */
  public synchronized int isActive() {
    return isAvailableAt(simulation.getTime()) ? 0 : -1;
  }

  public synchronized boolean isAvailableAt(int time) {
    return !busy && !reservedTimes.contains(time);
  }

  public synchronized boolean reserveTime(int time) {
    if (!isAvailableAt(time)) {
      return false;
    }

    for (int reservedTime = time; reservedTime < time + serviceTime; reservedTime++) {
      reservedTimes.add(reservedTime);
    }
    return true;
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

  public int getCustomersInSystem() {
    return arrived - served;
  }

  public int getCompletedServingTime() {
    return activeTime;
  }

  public int getCompletedCustomerCount() {
    return served;
  }

  public int[][] getServiceCount() {
    return branchOdds;
  }

}
