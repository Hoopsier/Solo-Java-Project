package model.phases;

import model.Event;
import model.EventQueue;
import model.ServicePoint;

public class BPhase {
  public static String activate(EventQueue BQueue, int time) {
    Event event = BQueue.peek();
    int count = 0;
    while (event != null && event.getTime() == time) {
      BQueue.progress();
      ServicePoint service = event.getService();
      service.processBEvent(event);
      count++;
      event = BQueue.peek();
    }
    return Integer.toString(count);
  }
}
