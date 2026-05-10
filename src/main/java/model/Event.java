package model;

/**
 * Represents a scheduled simulation event and the service point it targets.
 */
public class Event implements Comparable<Event> {
  /**
   * Supported event types in the simulation pipeline.
   */
  public enum Type {
    /** Routes a customer to a service point. */
    ROUTING,
    /** Starts service for a customer at a service point. */
    START_SERVICE,
    /** Ends service for a customer at a service point. */
    END_SERVICE
  }

  private int time;
  /** Service point associated with this event. */
  private ServicePoint servicePoint;
  private Type type;
  private boolean inSystem;
  private final int arrivalTime;

  /**
   * Creates a routing event whose arrival time matches its event time.
   *
   * @param _time simulation time for the event
   * @param _servicePoint target service point
   */
  public Event(int _time, ServicePoint _servicePoint) {
    this(_time, _servicePoint, Type.ROUTING);
  }

  /**
   * Creates an event whose arrival time matches its event time.
   *
   * @param _time simulation time for the event
   * @param _servicePoint target service point
   * @param _type type of event to process
   */
  public Event(int _time, ServicePoint _servicePoint, Type _type) {
    this(_time, _servicePoint, _type, _time);
  }

  /**
   * Creates an event with an explicit original customer arrival time.
   *
   * @param _time simulation time for the event
   * @param _servicePoint target service point
   * @param _type type of event to process
   * @param _arrivalTime original customer arrival time used for response-time
   *        calculations
   */
  public Event(int _time, ServicePoint _servicePoint, Type _type, int _arrivalTime) {
    time = _time;
    servicePoint = _servicePoint;
    type = _type;
    arrivalTime = _arrivalTime;
  }

  /**
   * Gets the scheduled event time.
   *
   * @return simulation time for this event
   */
  public synchronized int getTime() {
    return time;
  }

  /**
   * Updates the scheduled event time.
   *
   * @param _time new simulation time for this event
   */
  public synchronized void setTime(int _time) {
    time = _time;
  }

  /**
   * Gets the target service point.
   *
   * @return service point associated with this event
   */
  public ServicePoint getService() {
    return servicePoint;
  }

  /**
   * Gets the original arrival time of the customer represented by this event.
   *
   * @return original customer arrival time
   */
  public int getArrivalTime() {
    return arrivalTime;
  }

  /**
   * Gets the event type.
   *
   * @return event type
   */
  public Type getType() {
    return type;
  }

  /**
   * Determines whether this event's customer should be counted as in the system.
   *
   * @param currentTime current simulation time
   * @return {@code true} if the customer has already arrived or the event was
   *         explicitly marked in-system; otherwise {@code false}
   */
  public boolean isInSystem(int currentTime) {
    return inSystem || time <= currentTime;
  }

  /**
   * Marks whether this event's customer is already in the system.
   *
   * @param _inSystem in-system flag to assign
   */
  public void setInSystem(boolean _inSystem) {
    inSystem = _inSystem;
  }

  /**
   * Orders events by scheduled time for priority-queue processing.
   *
   * @param event other event to compare against
   * @return negative, zero, or positive value based on this event's time relative
   *         to the other event
   */
  @Override
  public int compareTo(Event event) {
    return time - event.time;
  }
}
