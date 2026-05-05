package model;

import java.util.PriorityQueue;
import java.util.Queue;

public class EventQueue {
  private Queue<Event> eventQueue = new PriorityQueue<>();

  public synchronized void readQueue(int time) {
    Queue<Event> tempQueue = new PriorityQueue<>();
    tempQueue.addAll(eventQueue.stream().filter(e -> e.getTime() == time).toList());
    while (!tempQueue.isEmpty()) {
      Event event = tempQueue.poll();
    }
  }

  /// returns null if event queue is empty
  /// otherwise returns the first in queue and removes it from the queue
  public synchronized Event progress() {
    return eventQueue.poll();
  }

  public synchronized Event peek() {
    return eventQueue.peek();
  }

  public synchronized void addToQueue(Event event) {
    eventQueue.add(event);
  }
}
