package model;

public class Event implements Comparable<Event> {
  private int time;
  /// for ending service
  private ServicePoint servicePoint;

  public Event(int _time, ServicePoint _servicePoint) {
    time = _time;
    servicePoint = _servicePoint;
  }

  public int getTime() {
    return time;
  }

  public void callEndService() {
    servicePoint.endService();
  }

  public ServicePoint getService() {
    return servicePoint;
  }

  public int compareTo(Event event) {
    return time - event.time;
  }
}
