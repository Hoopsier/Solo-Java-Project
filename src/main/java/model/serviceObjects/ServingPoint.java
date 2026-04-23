package model.serviceObjects;

public final class ServingPoint extends ServicePointType {
  /// Points per previous point.
  protected int maxValue = 3;
  protected int[][] numberDistribution = { { 100, maxValue } }; // 100% chance of max value
}
