package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import controller.Controller;
import model.phases.APhase;
import model.phases.BPhase;
import model.phases.CPhase;
import model.serviceObjects.ServicePointTree;

public class Simulation extends Thread {
  private Controller viewController;
  private int time = 0;
  private final int MAXTIME;

  /// times to go over
  private static Set<Integer> ASet = new HashSet<>();
  private static List<Integer> sortedASet;
  /// start or end scheduled
  private static EventQueue BQueue = new EventQueue();
  /// where to start, so conditionals
  private static EventQueue CQueue = new EventQueue();
  private ServicePointTree root;

  public Simulation(Controller _controller, int _MAXTIME) {
    viewController = _controller;
    MAXTIME = _MAXTIME;
  }

  public void run() {

    viewController.addDetails("Started~!");
    scheduleA(5);
    // loaded here in order to not lag the app (this creates the entire tree of
    // around 1000-2000 objects)
    root = new ServicePointTree(
        new ServicePoint(this, 1, 5), 1, this);
    scheduleB(new Event(5, root.getSelf()));

    while (time < MAXTIME) {

      time = APhase.activate(sortedASet);
      viewController.addDetails("Time advanced to (" + Integer.toString(time) + ")");
      BPhase.activate(BQueue, time);
      viewController.addDetails("BQueue passed");

      while (CPhase.activate(CQueue, time))
        System.out.print(""); // Just in case the optimizer skips this loop

      viewController.addDetails("CQueues passed");

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        viewController.addDetails("INTERRUPTED: " + e);
      }
    }
    viewController.addDetails("DONE!!");
  }

  public synchronized static void scheduleA(int time) {
    ASet.add(time);
    sortedASet = new ArrayList<>(ASet);
    Collections.sort(sortedASet);
  }

  public synchronized static void scheduleB(Event event) {
    BQueue.addToQueue(event);
  }

  public synchronized static void scheduleC(Event event) {
    CQueue.addToQueue(event);
  }

  public int getTime() {
    return time;
  }

  public ServicePointTree getServiceRoot() {
    return root;
  }
}
