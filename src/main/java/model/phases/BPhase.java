package model.phases;

import model.Event;
import model.EventQueue;
import model.ServicePoint;

/**
 * Phase B of the simulation, responsible for processing service start and end
 * events scheduled for the current time.
 */
public class BPhase {
  /**
   * Processes all eligible phase-B events at the supplied simulation time.
   *
   * @param BQueue queue containing start-service and end-service events
   * @param time current simulation time
   * @return number of events processed, formatted as a string for the detail log
   */
  public static String activate(EventQueue BQueue, int time) {
    Event event = BQueue.peek();
    int count = 0;
    while (event != null && event.getTime() <= time) {
      BQueue.progress();
      if (event.getTime() < time) {
        event = BQueue.peek();
        continue;
      }
      ServicePoint service = event.getService();
      service.processBEvent(event);
      count++;
      event = BQueue.peek();
    }
    return Integer.toString(count);
  }
}
