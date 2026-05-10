package model;

/**
 * Stores timing information for a simulated customer.
 */
public class Customer {
  int serviceTime = 0;
  int arrivalTime;

  /**
   * Creates a customer with the given arrival time.
   *
   * @param _arrivalTime simulation time at which the customer arrived
   */
  Customer(int _arrivalTime) {
    arrivalTime = _arrivalTime;
  }

  /**
   * Calculates and stores the customer's elapsed service time.
   *
   * @param simTime current simulation time used as the completion time
   */
  public void setServiceTime(int simTime) {
    serviceTime = simTime - arrivalTime;
  }
}
