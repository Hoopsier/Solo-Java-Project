package model;

import controller.Controller;

public class Simulation extends Thread {
  private Controller controller;
  private int time = 0;
  private final int MAXTIME;

  // AQueue.addToQueue(new Event(getTime, EventType.A, ServiceType.LANGUAGE));
  // works, but AQueue = new EventQueue(); does not
  // making this fine to be public
  public final EventQueue AQueue = new EventQueue();
  public final EventQueue BQueue = new EventQueue();
  public final EventQueue CQueue = new EventQueue();

  public Simulation(Controller _controller, int _MAXTIME) {
    controller = _controller;
    MAXTIME = _MAXTIME;
  }

  public void run() {
    System.out.println("Started!~");
    while (time < MAXTIME) {

    }
  }

  public int getTime() {
    return time;
  }
}
