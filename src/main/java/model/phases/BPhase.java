package model.phases;

import model.Event;
import model.EventQueue;
import model.ServicePoint;

public class BPhase {
  public static void activate(EventQueue BQueue, int time) {
    Event event = BQueue.peek();
    while (event != null && event.getTime() == time) {
      BQueue.progress();
      ServicePoint service = event.getService();
      service.start();
      event = BQueue.peek();
    }
  }
}
