package model.serviceObjects;

import model.ServicePoint;

/// Sorry for the bad naming scheme, but I don't want to waste any more time refactoring rn.
public class ServicePointType {

  private static int roll(int[][] numberDistribution) {
    int generatedAges[] = new int[numberDistribution.length + 1];

    // Generate ages according to the distribution:
    int x = (int) (Math.random() * 100) + 1; // generate a random number 1..100 -> we get the row which gives the age
    int j = 0;
    while (x > numberDistribution[j][0])
      j = generatedAges[numberDistribution[j][1]]; // search for the correct row to get the matching age
    return j;
  }

  /**
   * Router tool to get the availible parallel
   * 
   * @return ServicePoint to queue to, or null if everything is busy
   */
  public static synchronized ServicePoint getNextParallel(ServicePoint service, ServicePointTree root) {
    System.out.println("starting rc find");
    ServicePoint result = root.find(service.getSPId()).getChild(roll(service.getServiceCount()));
    System.out.println("ending rc find");
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
   * Lighter version of the parallel lookup
   * This one is to get the next router to go to
   * 
   * @return the branching service point for the next queue
   */
  public static synchronized ServicePoint getNextService(ServicePoint service, ServicePointTree root) {
    // TODO: route nextpoint with tree lookup

    return root.find(service.getSPId()).getChild(roll(service.getServiceCount()));
  }
}
