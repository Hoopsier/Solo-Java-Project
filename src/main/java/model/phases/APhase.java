package model.phases;

import java.util.List;

/**
 * Phase A of the simulation, responsible for selecting the next scheduled time
 * that should be processed.
 */
public class APhase {

  /**
   * Removes obsolete times, consumes the earliest remaining scheduled time, and
   * returns it as the next simulation time.
   *
   * @param aSet sorted list of candidate times
   * @param currentTime current simulation time
   * @return next simulation time, or {@code currentTime} when no time is
   *         scheduled
   */
  public static int activate(List<Integer> aSet, int currentTime) {
    if (aSet.isEmpty())
      return currentTime;
    aSet.removeIf(time -> time < currentTime);
    int time = aSet.get(0);
    aSet.remove(0);
    return time;
  }
}
