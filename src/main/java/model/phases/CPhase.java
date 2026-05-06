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
    System.out.println("entering loop");
    for (Event event = CQueue.peek(); event != null && event.getTime() == time; event = CQueue.peek()) {
      CQueue.progress();
      touched = true;
      System.out.println("finding parallel");
      ServicePoint service = ServicePointType.getCurrentParallel(event.getService());
      System.out.println("found parallel");
      if (service == null) {
        System.out.println("was null");
        continue;
      }
      event.setTime(event.getTime() + 1);
      System.out.println("B scheduled");
      simulation.scheduleB(event);
    }
    return touched;
  }
}
