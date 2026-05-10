package model.phases;

import model.Event;
import model.EventQueue;
import model.ServicePoint;
import model.Simulation;
import model.serviceObjects.ServicePointType;

/**
 * Phase C of the simulation, responsible for routing waiting customers to an
 * available service point or rescheduling them when all parallels are busy.
 */
public class CPhase {
  /**
   * Processes routing events that are due at or before the supplied time.
   *
   * @param CQueue queue containing routing events
   * @param simulation simulation used to reschedule events and enqueue service
   *        starts
   * @param time current simulation time
   * @return {@code true} if at least one routing event was touched; otherwise
   *         {@code false}
   */
  public static synchronized boolean activate(EventQueue CQueue, Simulation simulation, int time) {
    CQueue.readQueue(time);
    boolean touched = false;
    for (Event event = CQueue.peek(); event != null && event.getTime() <= time; event = CQueue.peek()) {
      CQueue.progress();
      if (event.getTime() < time) {
        event.setTime(time);
      }
      touched = true;
      event.setInSystem(true);
      int startTime = event.getTime() + 1;
      ServicePoint service = ServicePointType.getCurrentParallel(event.getService(), startTime);
      if (service == null || !service.reserveTime(startTime)) {
        event.setTime(startTime);
        simulation.scheduleC(event);
        continue;
      }
      simulation.scheduleB(new Event(startTime, service, Event.Type.START_SERVICE));
    }
    return touched;
  }
}
