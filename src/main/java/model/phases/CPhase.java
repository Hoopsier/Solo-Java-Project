package model.phases;

import model.Event;
import model.EventQueue;
import model.ServicePoint;
import model.Simulation;
import model.serviceObjects.ServicePointType;

public class CPhase {
  public static synchronized boolean activate(EventQueue CQueue, Simulation simulation, int time) {
    CQueue.readQueue(time);
    boolean touched = false;
    for (Event event = CQueue.peek(); event != null && event.getTime() == time; event = CQueue.peek()) {
      CQueue.progress();
      touched = true;
      int startTime = event.getTime() + 1;
      ServicePoint service = ServicePointType.getCurrentParallel(event.getService(), startTime);
      if (service == null || !service.reserveTime(startTime)) {
        continue;
      }
      simulation.scheduleB(new Event(startTime, service, Event.Type.START_SERVICE));
    }
    return touched;
  }
}
