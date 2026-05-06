package model.phases;

import java.util.List;

public class APhase {

  public static int activate(List<Integer> aSet, int currentTime) {
    aSet.removeIf(time -> time < currentTime);
    int time = aSet.get(0);
    aSet.remove(0);
    return time;
  }
}
