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
  private int customerWaitTime = -1;
  private List<Integer> customerWait = new ArrayList<>();
  /// delta time time active
  private int activeTime = 0;
  private final int STARTTIME;
  /// how long the service will take
  private final int SERVICETIME;
  private Set<Integer> reservedTimes = new HashSet<>();
  private Simulation simulation;
  private int[][] branchOdds;
  private ServicePoint nextPoint;
  private List<ServicePoint> parallelPoints = new ArrayList<>();
  private int busyUntil = 0;
  private boolean isFourth = false;

  public ServicePoint(Simulation _simulation, int _serviceTime, int[][] _branchOdds) {
    STARTTIME = _simulation.getTime();
    SERVICETIME = _serviceTime;
    branchOdds = _branchOdds;
    setId();
    simulation = _simulation;
  }

  public synchronized void setParallels(int quantity) {
    Random random = new Random();
    for (int i = 0; i < quantity; i++) {
      parallelPoints
          .add(new ServicePoint(simulation, SERVICETIME + random.nextInt(-2, 2), branchOdds).setParallelId(id));
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
      startService();
      return;
    }

    if (event.getType() == Event.Type.END_SERVICE) {
      endService();
    }
  }

  /** B1, 2 or 3 (start activity) */
  private void startService() {
    arrived++;
    busy = true;
    reservedTimes.remove(simulation.getTime());
    busyUntil = simulation.getTime() + SERVICETIME;
    nextPoint = ServicePointType.getNextService(simulation.getServiceRoot().find(id).getSelf(),
        simulation.getServiceRoot());
    System.out.println("scheduled");
    simulation.scheduleB(new Event(busyUntil, this, Event.Type.END_SERVICE));
  }

  /** B5,6,7 (finish activity) */
  public void endService() {
    served++;
    busy = false;
    activeTime += SERVICETIME; // second 5 - second 2 = 3 seconds active
    customerWait.add(customerWaitTime + SERVICETIME);
    if (isFourth) {
      simulation.addDetails("Phase 4 completed at service point " + id + " at time " + simulation.getTime()
          + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }
    // TODO: nextPoint is set up, so go to its router's queue
    // sim.addC nextPoint
    if (nextPoint != null) {
      simulation.scheduleC(new Event(busyUntil, nextPoint));
    }
  }

  /** For isActive check, use == 0 */
  public synchronized int isActive() {
    return isAvailableAt(simulation.getTime()) ? 0 : -1;
  }

  public synchronized boolean isAvailableAt(int time) {
    return !busy && !reservedTimes.contains(time);
  }

  public synchronized boolean reserveTime(int time) {
    for (int t : reservedTimes) {
      if (t == time) {
        return false;
      }
    }
    reservedTimes.add(time);
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

  public void setWaitTime(int _waitTime) {
    customerWaitTime = _waitTime;
  }

  public int[][] getServiceCount() {
    return branchOdds;
  }

}
