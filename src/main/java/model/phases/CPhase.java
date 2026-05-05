package model.phases;

import model.Event;
import model.EventQueue;
import model.ServicePoint;
import model.Simulation;
import model.serviceObjects.ServicePointTree;
import model.serviceObjects.ServicePointType;

public class CPhase {
  public static boolean activate(EventQueue CQueue, Simulation simulation, int time) {
    CQueue.readQueue(time);
    boolean touched = false;
    System.out.println("entering loop");
    for (Event event = CQueue.peek(); event != null && event.getTime() == time; event = CQueue.peek()) {
      CQueue.progress();
      touched = true;
      System.out.println("finding parallel");
      ServicePoint service = ServicePointType.getNextParallel(event.getService(), simulation.getServiceRoot());
      System.out.println("found parallel");
      event.setTime(event.getTime() + 1);
      System.out.println("entering null check");
      if (service == null) {
        CQueue.addToQueue(event);
        System.out.println("was null");
        continue;
      }
      System.out.println("B scheduled");
      simulation.scheduleB(event);
    }
    return touched;
  }
}
