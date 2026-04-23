package model;

import java.util.PriorityQueue;
import java.util.Queue;

import controller.Controller;
import model.phases.BPhase;

public class Simulation extends Thread {
  private Controller controller;
  private int time = 0;
  private final int MAXTIME;

  /// times to go over
  private static Queue<Integer> AQueue = new PriorityQueue<>();
  /// start or end scheduled
  private static EventQueue BQueue = new EventQueue();
  /// where to start, so conditionals
  private static EventQueue CQueue = new EventQueue();

  public Simulation(Controller _controller, int _MAXTIME) {
    controller = _controller;
    MAXTIME = _MAXTIME;
  }

  public void run() {
    System.out.println("Started!~");
    while (time < MAXTIME) {
      int time = AQueue.poll();
      BPhase.activate(BQueue);
      CPhase.activate(CQueue);

    }
  }

  public static void addToAQueue(int time) {
    AQueue.add(time);
  }

  public static void addToBQueue(Event event) {
    BQueue.addToQueue(event);
  }

  public static void addToCQueue(Event event) {
    CQueue.addToQueue(event);
  }

  public int getTime() {
    return time;
  }
}
