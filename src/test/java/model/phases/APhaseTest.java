package model.phases;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link APhase} time-advance behavior.
 */
class APhaseTest {
  /**
   * Verifies that scheduled times before the current simulation time are
   * ignored.
   */
  @Test
  void skipsTimesBeforeTheCurrentSimulationTime() {
    List<Integer> scheduledTimes = new ArrayList<>(List.of(1, 2, 5));

    int nextTime = APhase.activate(scheduledTimes, 3);

    assertEquals(5, nextTime);
  }

  /**
   * Verifies that the current simulation time can still be processed.
   */
  @Test
  void stillAllowsTheCurrentSimulationTime() {
    List<Integer> scheduledTimes = new ArrayList<>(List.of(3, 4));

    int nextTime = APhase.activate(scheduledTimes, 3);

    assertEquals(3, nextTime);
  }
}
