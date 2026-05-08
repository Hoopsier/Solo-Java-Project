package model;

public class Event implements Comparable<Event> {
  public enum Type {
    ROUTING,
    START_SERVICE,
    END_SERVICE
  }

  private int time;
  /// for ending service
  private ServicePoint servicePoint;
  private Type type;
  private boolean inSystem;

  public Event(int _time, ServicePoint _servicePoint) {
    this(_time, _servicePoint, Type.ROUTING);
  }

  public Event(int _time, ServicePoint _servicePoint, Type _type) {
    time = _time;
    servicePoint = _servicePoint;
    type = _type;
  }

  public synchronized int getTime() {
    return time;
  }

  public synchronized void setTime(int _time) {
    time = _time;
  }

  public ServicePoint getService() {
    return servicePoint;
  }

  public Type getType() {
    return type;
  }

  public boolean isInSystem(int currentTime) {
    return inSystem || time <= currentTime;
  }

  public void setInSystem(boolean _inSystem) {
    inSystem = _inSystem;
  }

  public int compareTo(Event event) {
    return time - event.time;
  }
}
