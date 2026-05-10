package model;

import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Thread-safe priority queue for scheduled simulation events.
 */
public class EventQueue {
  private Queue<Event> eventQueue = new PriorityQueue<>();

  /**
   * Reads all events scheduled at a specific time without mutating the primary
   * queue.
   *
   * @param time simulation time to inspect
   */
  public synchronized void readQueue(int time) {
    Queue<Event> tempQueue = new PriorityQueue<>();
    tempQueue.addAll(eventQueue.stream().filter(e -> e.getTime() == time).toList());
    while (!tempQueue.isEmpty()) {
      Event event = tempQueue.poll();
    }
  }

  /**
   * Removes and returns the earliest event in the queue.
   *
   * @return next event, or {@code null} when the queue is empty
   */
  public synchronized Event progress() {
    return eventQueue.poll();
  }

  /**
   * Returns the earliest event without removing it.
   *
   * @return next event, or {@code null} when the queue is empty
   */
  public synchronized Event peek() {
    return eventQueue.peek();
  }

  /**
   * Adds a scheduled event to the queue.
   *
   * @param event event to enqueue
   */
  public synchronized void addToQueue(Event event) {
    eventQueue.add(event);
  }

  /**
   * Counts queued events of a specific type.
   *
   * @param type event type to count
   * @return number of queued events with the requested type
   */
  public synchronized int countByType(Event.Type type) {
    int count = 0;
    for (Event event : eventQueue) {
      if (event.getType() == type) {
        count++;
      }
    }
    return count;
  }

  /**
   * Counts routing events whose customers have already entered the system.
   *
   * @param currentTime current simulation time
   * @return number of routing events that represent customers in the system
   */
  public synchronized int countRoutingsInSystem(int currentTime) {
    int count = 0;
    for (Event event : eventQueue) {
      if (event.getType() == Event.Type.ROUTING && event.isInSystem(currentTime)) {
        count++;
      }
    }
    return count;
  }
}
