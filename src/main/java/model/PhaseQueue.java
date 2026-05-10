package model;

import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Stores customers in separate priority queues for each simulation phase.
 */
public class PhaseQueue {
  private Queue<Customer>[] phaseQueue = new Queue[4];

  /**
   * Creates the four phase queues used by the simulation.
   */
  public PhaseQueue() {
    for (int i = 0; i < 4; i++) {
      phaseQueue[i] = new PriorityQueue<Customer>();
    }
  }

  /**
   * Adds a customer to a phase-specific queue.
   *
   * @param customer customer to enqueue
   * @param phase phase index from 0 to 3
   */
  public void addToPhaseQueue(Customer customer, int phase) {
    phaseQueue[phase].add(customer);
  }

  /**
   * Checks whether a phase queue has no customers.
   *
   * @param phase phase index from 0 to 3
   * @return {@code true} when the selected queue is empty
   */
  public boolean isEmpty(int phase) {
    return phaseQueue[phase].isEmpty();
  }

  /**
   * Removes and returns the next customer from a phase queue.
   *
   * @param phase phase index from 0 to 3
   * @return next customer, or {@code null} if the selected queue is empty
   */
  public Customer poll(int phase) {
    return phaseQueue[phase].poll();
  }
}
