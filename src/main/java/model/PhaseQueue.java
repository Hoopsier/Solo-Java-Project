package model;

import java.util.PriorityQueue;
import java.util.Queue;

/**
 * The thing to iterate through to advance queues
 */
public class PhaseQueue {
  private Queue<Customer>[] phaseQueue = new Queue[4];

  public PhaseQueue() {
    for (int i = 0; i < 4; i++) {
      phaseQueue[i] = new PriorityQueue<Customer>();
    }
  }

  public void addToPhaseQueue(Customer customer, int phase) {
    phaseQueue[phase].add(customer);
  }

  public boolean isEmpty(int phase) {
    return phaseQueue[phase].isEmpty();
  }

  public Customer poll(int phase) {
    return phaseQueue[phase].poll();
  }
}
