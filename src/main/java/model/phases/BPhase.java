package model.phases;

import model.Event;
import model.EventQueue;
import model.EventType;
import model.ServicePoint;

public class BPhase {
  public static void activate(EventQueue BQueue) {
    Event event = BQueue.progress();
    if (event.getEventType() == EventType.BS) {
      ServicePoint service = event.getService();
    }
  }
}
