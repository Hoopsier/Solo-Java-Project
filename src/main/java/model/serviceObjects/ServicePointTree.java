package model.serviceObjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import model.ServicePoint;
import model.Simulation;

/**
 * Tree structure that models the service-point routing hierarchy used by the
 * simulation.
 */
public class ServicePointTree {
  private ServicePoint self;
  private List<ServicePointTree> children = new ArrayList<>();
  private int depth;
  private static ServicePointTree root;

  private static int hasTierFour = -1;

  /**
   * Creates a service-point tree node and recursively builds the expected child
   * structure for the supplied depth.
   *
   * @param servicePoint service point represented by this node
   * @param _depth tier depth of this node in the routing tree
   * @param simulation owning simulation used when constructing child service
   *        points
   */
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

  /**
   * Creates or reuses the shared tier-four branch.
   *
   * @param simulation owning simulation for newly created service points
   * @param odds routing odds assigned to the tier-four service point
   * @return tier-four tree node
   */
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

  /**
   * Checks whether this node's service point has the requested identifier.
   *
   * @param id service-point identifier to compare
   * @return {@code true} when the identifiers match
   */
  public boolean equals(int id) {
    return this.self.getSPId() == id;
  }

  /**
   * Searches this subtree for a service point identifier.
   *
   * @param target service-point identifier to find
   * @return matching tree node, or {@code null} if no node matches
   */
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

  /**
   * Gets the service point represented by this node.
   *
   * @return current node's service point
   */
  public ServicePoint getSelf() {
    return self;
  }

  /**
   * Gets a child node's service point by index.
   *
   * @param index zero-based child index
   * @return child service point, or {@code null} when the index is invalid
   */
  public ServicePoint getChild(int index) {
    if (index < 0 || index >= children.size()) {
      return null;
    }
    return children.get(index).getSelf();
  }

  /**
   * Checks whether this node has child routes.
   *
   * @return {@code true} when this node has at least one child
   */
  public boolean hasChildren() {
    return !children.isEmpty();
  }

  /**
   * Counts all service points represented by this subtree, including parallels.
   *
   * @return total service-point count in this subtree
   */
  public int serviceTotalCount() {
    int sum = 1 + self.getParallels().size();
    for (ServicePointTree child : children) {
      sum += child.serviceTotalCount();
    }
    return sum;
  }

  /**
   * Prints this subtree to standard output using hyphens for indentation.
   *
   * @param _depth indentation depth to print before child branches
   */
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

  /**
   * Builds a formatted description of this tree node.
   *
   * @return multiline string describing depth, child count, and service point
   */
  public String getToString() {
    return String.format("Depth: %d\nChild Count: %d\nPointer: %s", depth, children.size(), self);
  }

  /**
   * Counts busy service points across this subtree.
   *
   * @return number of active services, including parallel points
   */
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

  /**
   * Counts customers currently being served in this subtree.
   *
   * @return number of busy service points across this subtree and parallels
   */
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

  /**
   * Gets every unique service point in this subtree, including shared tier-four
   * services only once.
   *
   * @return list of unique service points
   */
  public List<ServicePoint> getAllServicePoints() {
    List<ServicePoint> servicePoints = new ArrayList<>();
    Set<ServicePoint> seen = Collections.newSetFromMap(new IdentityHashMap<>());
    collectServicePoints(servicePoints, seen);
    return servicePoints;
  }

  /**
   * Recursively collects unique service points from this subtree.
   *
   * @param servicePoints output list receiving service points
   * @param seen identity set used to avoid duplicate shared nodes
   */
  private void collectServicePoints(List<ServicePoint> servicePoints, Set<ServicePoint> seen) {
    addServicePoint(servicePoints, seen, self);
    for (ServicePoint point : self.getParallels()) {
      addServicePoint(servicePoints, seen, point);
    }
    for (ServicePointTree child : children) {
      child.collectServicePoints(servicePoints, seen);
    }
  }

  /**
   * Adds a service point to the output list if it has not already been seen.
   *
   * @param servicePoints output list receiving unique service points
   * @param seen identity set used to track collected service points
   * @param servicePoint service point to add
   */
  private void addServicePoint(List<ServicePoint> servicePoints, Set<ServicePoint> seen, ServicePoint servicePoint) {
    if (seen.add(servicePoint)) {
      servicePoints.add(servicePoint);
    }
  }

  /**
   * Calculates average serving time for completed customers across the subtree.
   *
   * @return average serving time, or {@code 0} when no customers are complete
   */
  public double getAverageServingTime() {
    int[] data = getServingTimeTotals();
    if (data[1] == 0) {
      return 0;
    }
    return (double) data[0] / data[1];
  }

  /**
   * Recursively sums completed serving time and completed customer count.
   *
   * @return two-element array of {@code [servingTimeSum, completedCount]}
   */
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
