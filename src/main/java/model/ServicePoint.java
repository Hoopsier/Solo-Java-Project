package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import model.serviceObjects.ServicePointType;

/**
 * Represents a service station in the simulation, including its parallel service
 * stations, routing odds, service timing, utilization, and completed-customer
 * metrics.
 */
public class ServicePoint {
  private static int _id;
  private int id;
  private boolean busy;
  private int arrived = 0;
  private int served = 0;
  /** Total time this service point has spent actively serving customers. */
  private int activeTime = 0;
  private final int STARTTIME;
  /** Current duration required to serve a customer. */
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

  /**
   * Creates a service point with a base service duration and routing table.
   *
   * @param _simulation owning simulation
   * @param _serviceTime base service duration in simulation minutes
   * @param _branchOdds cumulative routing odds, where each row is
   *        {@code [threshold, childIndex]}
   */
  public ServicePoint(Simulation _simulation, int _serviceTime, int[][] _branchOdds) {
    STARTTIME = _simulation.getTime();
    serviceTime = _serviceTime;
    SERVICETIMEBASE = _serviceTime;
    branchOdds = _branchOdds;
    setId();
    simulation = _simulation;
  }

  /**
   * Creates parallel service points that share this point's identifier and
   * routing behavior.
   *
   * @param quantity number of parallel service points to create
   */
  public synchronized void setParallels(int quantity) {
    Random random = new Random();
    for (int i = 0; i < quantity; i++) {
      int parallelServiceTime = Math.max(1, serviceTime + random.nextInt(-2, 2));
      parallelPoints
          .add(new ServicePoint(simulation, parallelServiceTime, branchOdds).setParallelId(id));
    }
  }

  /**
   * Gets this service point's parallel alternatives.
   *
   * @return mutable list of parallel service points
   */
  public synchronized List<ServicePoint> getParallels() {
    return parallelPoints;
  }

  /**
   * Assigns a unique service-point identifier.
   */
  private synchronized void setId() {
    id = _id++;
  }

  /**
   * Assigns an existing identifier to a parallel service point.
   *
   * @param id shared service-point identifier
   * @return this service point for fluent construction
   */
  private synchronized ServicePoint setParallelId(int id) {
    this.id = id;
    return this;
  }

  /**
   * Marks this service point as the shared tier-four terminal service point.
   */
  public void setFourth() {
    isFourth = true;
  }

  /**
   * Gets the service-point identifier.
   *
   * @return identifier shared by this point and its parallels
   */
  public int getSPId() {
    return id;
  }

  /**
   * Checks whether this service point is currently busy.
   *
   * @return {@code true} when a customer is being served
   */
  public synchronized boolean isBusy() {
    return busy;
  }

  /**
   * Processes a phase-B event for this service point.
   *
   * @param event start-service or end-service event to apply
   */
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

  /**
   * Starts serving the event's customer and schedules its end-service event.
   *
   * @param event start-service event carrying the customer's arrival time
   */
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

  /**
   * Finishes the current service, records utilization, and routes or completes
   * the customer.
   */
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

  /**
   * Reports whether this service point can accept a customer at the current
   * simulation time.
   *
   * @return {@code 0} when active and available; {@code -1} when unavailable
   */
  public synchronized int isActive() {
    return isAvailableAt(simulation.getTime()) ? 0 : -1;
  }

  /**
   * Checks availability at a specific simulation time.
   *
   * @param time simulation time to inspect
   * @return {@code true} when not busy and not already reserved for that time
   */
  public synchronized boolean isAvailableAt(int time) {
    return !busy && !reservedTimes.contains(time);
  }

  /**
   * Reserves this service point for the full duration of a future service.
   *
   * @param time start time to reserve
   * @return {@code true} when the reservation succeeded; otherwise {@code false}
   */
  public synchronized boolean reserveTime(int time) {
    if (!isAvailableAt(time)) {
      return false;
    }

    for (int reservedTime = time; reservedTime < time + serviceTime; reservedTime++) {
      reservedTimes.add(reservedTime);
    }
    return true;
  }

  /**
   * Gets the number of customers that have started service here.
   *
   * @return arrived customer count
   */
  public int getArrived() {
    return arrived;
  }

  /**
   * Gets the number of customers that have completed service here.
   *
   * @return served customer count
   */
  public int getServed() {
    return served;
  }

  /**
   * Gets the elapsed simulation time since this point was created.
   *
   * @return total lifetime in simulation minutes
   */
  public int getTotalTime() {
    return simulation.getTime() - STARTTIME;
  }

  /**
   * Gets this point's accumulated active serving time.
   *
   * @return active service time in simulation minutes
   */
  public int getActiveTime() {
    return activeTime;
  }

  /**
   * Gets the number of customers currently associated with this service point.
   *
   * @return arrived customers minus served customers
   */
  public int getCustomersInSystem() {
    return arrived - served;
  }

  /**
   * Gets completed serving time used in average-service-time calculations.
   *
   * @return accumulated completed serving time
   */
  public int getCompletedServingTime() {
    return activeTime;
  }

  /**
   * Gets the completed customer count used in average-service-time calculations.
   *
   * @return number of completed services
   */
  public int getCompletedCustomerCount() {
    return served;
  }

  /**
   * Gets the cumulative routing table for this service point.
   *
   * @return branch odds as rows of {@code [threshold, childIndex]}
   */
  public int[][] getServiceCount() {
    return branchOdds;
  }

}
