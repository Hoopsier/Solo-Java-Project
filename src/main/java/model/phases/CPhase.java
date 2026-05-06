package model.phases;

import model.Event;
import model.EventQueue;
import model.ServicePoint;
import model.Simulation;
import model.serviceObjects.ServicePointTree;
import model.serviceObjects.ServicePointType;

public class CPhase {
  public static synchronized boolean activate(EventQueue CQueue, Simulation simulation, int time) {
    CQueue.readQueue(time);
    boolean touched = false;
    for (Event event = CQueue.peek(); event != null && event.getTime() == time; event = CQueue.peek()) {
      CQueue.progress();
      touched = true;
      ServicePoint service = ServicePointType.getCurrentParallel(event.getService());
      if (service == null) {
        continue;
      }
      event.setTime(event.getTime() + 1);
      simulation.scheduleB(event);
    }
    return touched;
  }
}
