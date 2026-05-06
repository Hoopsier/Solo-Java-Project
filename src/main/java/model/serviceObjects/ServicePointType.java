package model.serviceObjects;

import model.ServicePoint;

/// Sorry for the bad naming scheme, but I don't want to waste any more time refactoring rn.
public class ServicePointType {

  private static int roll(int[][] numberDistribution) {
    int x = (int) (Math.random() * 100) + 1; // generate a random number 1..100 -> we get the row which gives the age
    for (int[] row : numberDistribution) {
      if (x <= row[0]) {
        return row[1];
      }
    }
    return numberDistribution[numberDistribution.length - 1][1];
  }

  /**
   * Router tool to get the availible parallel
   * 
   * @return ServicePoint to queue to, or null if everything is busy
   */
  public static synchronized ServicePoint getNextParallel(ServicePoint service, ServicePointTree root) {
    ServicePointTree current = root.find(service.getSPId());
    if (current == null) {
      return null;
    }

    ServicePoint result = current.getChild(roll(service.getServiceCount()));
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
   * Lighter version of the parallel lookup
   * This one is to get the next router to go to
   * 
   * @return the branching service point for the next queue
   */
  public static synchronized ServicePoint getNextService(ServicePoint service, ServicePointTree root) {
    // TODO: route nextpoint with tree lookup
    ServicePointTree current = root.find(service.getSPId());
    if (current == null) {
      return null;
    }

    return current.getChild(roll(service.getServiceCount()));
  }
}
