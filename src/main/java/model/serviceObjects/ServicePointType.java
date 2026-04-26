package model.serviceObjects;

import model.ServicePoint;

/// Sorry for the bad naming scheme, but I don't want to waste any more time refactoring rn.
public class ServicePointType {
  private static int numberDistribution[][] = {
      { 33, 1 },
      { 66, 2 },
      { 100, 3 }
  };

  private static int roll(int max) {
    int generatedAges[] = new int[max + 1];

    // Generate ages according to the distribution:
    int x = (int) (Math.random() * 100) + 1; // generate a random number 1..100 -> we get the row which gives the age
    int j = 0;
    while (x > numberDistribution[j][0])
      j = generatedAges[numberDistribution[j][1]]; // search for the correct row to get the matching age
    return j;
  }

  public static ServicePoint getNextService(ServicePoint service, ServicePointTree root) {
    // TODO: route nextpoint with tree lookup

    return root.find(service.getSPId()).getChild(roll(service.getServiceCount()));
  }
}
