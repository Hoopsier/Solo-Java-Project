package model;

import java.util.PriorityQueue;
import java.util.Queue;

public class EventQueue {
  private Queue<Event> eventQueue = new PriorityQueue<>();

  public synchronized void readQueue() {
    Queue<Event> tempQueue = new PriorityQueue<>();
    tempQueue.addAll(eventQueue);
    while (!tempQueue.isEmpty()) {
      Event event = tempQueue.poll();
      System.out.print(event.getTime() + " " + event.getServiceType() + ", ");
    }
    System.out.println();
  }

  /// returns null if event queue is empty
  /// otherwise returns the first in queue and removes it from the queue
  public synchronized Event progress() {
    return eventQueue.poll();
  }

  public synchronized void addToQueue(Event event) {
    eventQueue.add(event);
  }
}
