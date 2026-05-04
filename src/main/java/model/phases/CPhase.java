package model.phases;

import model.Event;
import model.EventQueue;
import model.ServicePoint;

public class CPhase {
  public static boolean activate(EventQueue CQueue, int time) {
    CQueue.readQueue(time);
    boolean touched = false;
    for (Event event = CQueue.peek(); event != null && event.getTime() == time; event = CQueue.peek()) {
      CQueue.progress();
      touched = true;
      ServicePoint service = event.getService();
    }
    return touched;
  }
}
