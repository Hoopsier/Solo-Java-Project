package model;

import model.serviceObjects.ServicePointType;

public class Event implements Comparable<Event> {
  private int time;
  /// for ending service
  private ServicePoint servicePoint;
  private EventType eventType;

  public Event(int _time, EventType _eventType, ServicePoint _servicePoint) {
    time = _time;
    servicePoint = _servicePoint;
    eventType = _eventType;
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

  public EventType getEventType() {
    return eventType;
  }

  public int compareTo(Event event) {
    return time - event.time;
  }
}
