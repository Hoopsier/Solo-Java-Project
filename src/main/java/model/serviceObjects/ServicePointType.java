package model.serviceObjects;

import model.ServicePoint;

/**
 * Routing helper methods for choosing child and parallel service points.
 */
public class ServicePointType {

  /**
   * Rolls against a cumulative integer distribution.
   *
   * @param numberDistribution rows of {@code [threshold, returnValue]}
   * @return selected return value, or {@code -1} when the distribution is empty
   */
  private static synchronized int roll(int[][] numberDistribution) {
    if (numberDistribution.length == 0) {
      return -1;
    }

    int x = (int) (Math.random() * 100) + 1; // generate a random number 1..100 -> we get the row which gives the age
    for (int[] row : numberDistribution) {
      if (x <= row[0]) {
        return row[1];
      }
    }
    return numberDistribution[numberDistribution.length - 1][1];
  }

  /**
   * Chooses an available parallel service point under the next routed child.
   *
   * @param service current service point whose routing table is used
   * @param root root of the service-point tree
   * @return service point to queue to, or {@code null} when no routed parallel is
   *         available
   */
  public static synchronized ServicePoint getNextParallel(ServicePoint service, ServicePointTree root) {
    ServicePointTree current = root.find(service.getSPId());
    if (current == null || !current.hasChildren()) {
      return null;
    }

    ServicePoint result = current.getChild(roll(service.getServiceCount()));
    if (result == null) {
      return null;
    }

    if (result.isActive() >= 0) {
      return result;
    }
    ServicePoint parallel = null;
    for (ServicePoint point : result.getParallels()) {
      if (point.isActive() < 0) {
        continue;
      }
      parallel = point;
      break;
    }
    return parallel;
  }

  /**
   * Chooses the current service point or one of its parallels if available now.
   *
   * @param service preferred service point
   * @return available service point, or {@code null} when all parallels are busy
   */
  public static synchronized ServicePoint getCurrentParallel(ServicePoint service) {
    if (service.isActive() >= 0) {
      return service;
    }
    ServicePoint parallel = null;
    for (ServicePoint point : service.getParallels()) {
      if (point.isActive() < 0) {
        continue;
      }
      parallel = point;
      break;
    }
    return parallel;
  }

  /**
   * Chooses the current service point or one of its parallels if available at a
   * future time.
   *
   * @param service preferred service point
   * @param time simulation time to inspect
   * @return available service point, or {@code null} when all parallels are
   *         unavailable at that time
   */
  public static synchronized ServicePoint getCurrentParallel(ServicePoint service, int time) {
    if (service.isAvailableAt(time)) {
      return service;
    }
    ServicePoint parallel = null;
    for (ServicePoint point : service.getParallels()) {
      if (!point.isAvailableAt(time)) {
        continue;
      }
      parallel = point;
      break;
    }
    return parallel;
  }

  /**
   * Chooses the next child router/service point using the current service
   * point's routing table.
   *
   * @param service current service point
   * @param root root of the service-point tree
   * @return branching service point for the next queue, or {@code null} at a
   *         terminal node
   */
  public static synchronized ServicePoint getNextService(ServicePoint service, ServicePointTree root) {
    ServicePointTree current = root.find(service.getSPId());
    if (current == null || !current.hasChildren()) {
      return null;
    }

    return current.getChild(roll(service.getServiceCount()));
  }
}
