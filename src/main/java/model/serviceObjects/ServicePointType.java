package model.serviceObjects;

import model.ServicePoint;

/// Sorry for the bad naming scheme, but I don't want to waste any more time refactoring rn.
public class ServicePointType {
  protected int maxValue;
  protected int numberDistribution[][] = {
      { 16, 20 }, // 16 % // This line matches to random numbers 1..16, gives age 20
      { 34, 21 }, // 18 % // This line matches to random numbers 17..34, gives age 21
      { 52, 22 }, // 18 % // ...
      { 68, 23 }, // 16 %
      { 82, 24 }, // 14 %
      { 89, 25 }, // 7 %
      { 94, 26 }, // 5 %
      { 96, 28 }, // 2 %
      { 98, 30 }, // 2 %
      { 100, maxValue }, // 2 % // This line matches to random numbers 99 ja 100
  };
  protected ServicePoint[] servicePoints;

  private int roll() {
    int generatedAges[] = new int[maxValue + 1];

    // Generate ages according to the distribution:
    int x = (int) (Math.random() * 100) + 1; // generate a random number 1..100 -> we get the row which gives the age
    int j = 0;
    while (x > numberDistribution[j][0])
      j = generatedAges[numberDistribution[j][1]]; // search for the correct row to get the matching age
    return j;
  }

  public ServicePoint getNextService() {
    if (maxValue == 0) {
      return null;
    }
    return servicePoints[roll()];
  }
}
