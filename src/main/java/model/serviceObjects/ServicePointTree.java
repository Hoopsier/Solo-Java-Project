package model.serviceObjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import model.ServicePoint;
import model.Simulation;

public class ServicePointTree {
  private ServicePoint self;
  private List<ServicePointTree> children = new ArrayList<>();
  private int depth;
  private static ServicePointTree root;

  private static int hasTierFour = -1;

  public ServicePointTree(ServicePoint servicePoint, int _depth, Simulation simulation) {
    self = servicePoint;
    depth = _depth;
    ServicePoint sp;
    switch (depth) {
      case 1:
        root = this;
        hasTierFour = -1;
        int[][] odds = { { 33, 0 }, { 100, 1 } }; // two categories each
        self.setParallels(5); // 6 total
        // i is branch index
        for (int i = 0; i < 3; i++) { // three Languages
          sp = new ServicePoint(simulation, 2, odds);
          sp.setParallels(1); // 2 total
          children.add(new ServicePointTree(sp, 2, simulation));
        }
        return;
      case 2:
        int[][] odds2 = { { 100, 0 } }; // 1 details each
        // i is branch index
        sp = new ServicePoint(simulation, 6, odds2);
        sp.setParallels(1); // 2 total
        children.add(new ServicePointTree(sp, 3, simulation));
        return;
      case 3:
        int[][] odds3 = { { 100, 0 } }; // one set of 21 service points
        children.add(addFourth(simulation, odds3));
        return;
      case 4:
        self.setParallels(20);
        self.setFourth();
        hasTierFour = self.getSPId();
        return;
    }
  }

  private synchronized ServicePointTree addFourth(Simulation simulation, int[][] odds) {
    if (hasTierFour > -1) {
      ServicePointTree existingTierFour = root.find(hasTierFour);
      if (existingTierFour != null) {
        return existingTierFour;
      }
      hasTierFour = -1;
    }

    ServicePoint sp = new ServicePoint(simulation, 10, odds);
    return new ServicePointTree(sp, 4, simulation);
  }

  public boolean equals(int id) {
    return this.self.getSPId() == id;
  }

  public synchronized ServicePointTree find(int target) {
    if (self == null) {
      return null;
    }

    if (equals(target)) {
      return this;
    }

    for (ServicePointTree child : children) {
      ServicePointTree result = child.find(target);
      if (result != null) {
        return result;
      }
    }

    return null;
  }

  public ServicePoint getSelf() {
    return self;
  }

  public ServicePoint getChild(int index) {
    if (index < 0 || index >= children.size()) {
      return null;
    }
    return children.get(index).getSelf();
  }

  public boolean hasChildren() {
    return !children.isEmpty();
  }

  public int serviceTotalCount() {
    int sum = 1 + self.getParallels().size();
    for (ServicePointTree child : children) {
      sum += child.serviceTotalCount();
    }
    return sum;
  }

  public void printTree(int _depth) {
    System.out.println(self.toString());
    for (ServicePointTree branch : children) {
      while (_depth > 0) {
        System.out.print("-");
        _depth--;
      }
      branch.printTree(_depth + 1);
    }
  }

  // renamed to get just in case it overlaps with the self call for pointer field
  public String getToString() {
    return String.format("Depth: %d\nChild Count: %d\nPointer: %s", depth, children.size(), self);
  }

  public int getActiveServices() {
    int count = self.isBusy() ? 1 : 0;
    for (ServicePoint point : self.getParallels()) {
      count += point.isBusy() ? 1 : 0;
    }
    for (ServicePointTree child : children) {
      count += child.getActiveServices();
    }
    return count;
  }

  public int getCustomersInSystem() {
    int count = self.isBusy() ? 1 : 0;
    for (ServicePoint point : self.getParallels()) {
      count += point.isBusy() ? 1 : 0;
    }
    for (ServicePointTree child : children) {
      count += child.getCustomersInSystem();
    }
    return count;
  }

  public List<ServicePoint> getAllServicePoints() {
    List<ServicePoint> servicePoints = new ArrayList<>();
    Set<ServicePoint> seen = Collections.newSetFromMap(new IdentityHashMap<>());
    collectServicePoints(servicePoints, seen);
    return servicePoints;
  }

  private void collectServicePoints(List<ServicePoint> servicePoints, Set<ServicePoint> seen) {
    addServicePoint(servicePoints, seen, self);
    for (ServicePoint point : self.getParallels()) {
      addServicePoint(servicePoints, seen, point);
    }
    for (ServicePointTree child : children) {
      child.collectServicePoints(servicePoints, seen);
    }
  }

  private void addServicePoint(List<ServicePoint> servicePoints, Set<ServicePoint> seen, ServicePoint servicePoint) {
    if (seen.add(servicePoint)) {
      servicePoints.add(servicePoint);
    }
  }

  public double getAverageServingTime() {
    int[] data = getServingTimeTotals();
    if (data[1] == 0) {
      return 0;
    }
    return (double) data[0] / data[1];
  }

  private int[] getServingTimeTotals() {
    int sum = self.getCompletedServingTime();
    int count = self.getCompletedCustomerCount();
    for (ServicePoint point : self.getParallels()) {
      sum += point.getCompletedServingTime();
      count += point.getCompletedCustomerCount();
    }
    for (ServicePointTree child : children) {
      int[] data = child.getServingTimeTotals();
      sum += data[0];
      count += data[1];
    }
    return new int[] { sum, count };
  }
}
