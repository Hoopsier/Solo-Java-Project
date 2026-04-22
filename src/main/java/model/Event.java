package model;

public class Event implements Comparable<Event> {
  private int time;
  private ServiceType serviceType;
  private EventType eventType;

  public Event(int _time, EventType _eventType, ServiceType _serviceType) {
    time = _time;
    serviceType = _serviceType;
    eventType = _eventType;
  }

  public int getTime() {
    return time;
  }

  public ServiceType getServiceType() {
    return serviceType;
  }

  public EventType getEventType() {
    return eventType;
  }

  public int compareTo(Event event) {
    return time - event.time;
  }
}
