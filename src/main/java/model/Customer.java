package model;

public class Customer {
  int serviceTime = 0;
  int arrivalTime;

  Customer(int _arrivalTime) {
    arrivalTime = _arrivalTime;
  }

  public void setServiceTime(int simTime) {
    serviceTime = simTime - arrivalTime;
  }
}
