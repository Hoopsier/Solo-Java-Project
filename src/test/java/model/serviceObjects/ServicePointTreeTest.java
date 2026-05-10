package model.serviceObjects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import model.ServicePoint;
import model.Simulation;

/**
 * Unit tests for service-point tree construction and routing behavior.
 */
class ServicePointTreeTest {
  /**
   * Verifies that category router nodes expose both category child outcomes.
   */
  @Test
  void categoryRouterHasChildrenForBothCategoryOutcomes() {
    ServicePointTree root = createTree();
    ServicePoint languageRouter = root.getChild(0);
    ServicePointTree languageRouterNode = root.find(languageRouter.getSPId());
    ServicePoint categoryRouter = languageRouterNode.getChild(0);
    ServicePointTree categoryRouterNode = root.find(categoryRouter.getSPId());

    ServicePoint firstCategory = categoryRouterNode.getChild(0);
    ServicePoint secondCategory = categoryRouterNode.getChild(1);

    assertNotNull(firstCategory);
    assertNotNull(secondCategory);
  }

  /**
   * Verifies that terminal service points do not route to another node.
   */
  @Test
  void terminalServiceDoesNotRoutePastTheEndOfTheTree() {
    ServicePointTree root = createTree();
    ServicePoint languageRouter = root.getChild(0);
    ServicePointTree languageRouterNode = root.find(languageRouter.getSPId());
    ServicePoint categoryRouter = languageRouterNode.getChild(0);
    ServicePointTree categoryRouterNode = root.find(categoryRouter.getSPId());
    ServicePoint terminalService = categoryRouterNode.getChild(0);

    assertNull(ServicePointType.getNextService(terminalService, root));
  }

  /**
   * Verifies that the shared tier-four branch is only returned once in the list
   * of all service points.
   */
  @Test
  void allServicePointsOnlyIncludesSharedTierFourServicesOnce() {
    ServicePointTree root = createTree();

    assertEquals(39, root.getAllServicePoints().size());
  }

  /**
   * Verifies that constructing a new tree resets stale tier-four state.
   */
  @Test
  void buildingASecondTreeDoesNotReuseMissingTierFourBranch() {
    ServicePointTree firstTree = createTree();
    ServicePointTree secondTree = createTree();

    assertNotNull(firstTree);
    assertNotNull(secondTree);
    assertNotNull(secondTree.find(secondTree.getChild(0).getSPId()));
    secondTree.serviceTotalCount();
  }

  /**
   * Creates a complete service-point tree for tests.
   *
   * @return root service-point tree
   */
  private ServicePointTree createTree() {
    Simulation simulation = new Simulation(null, 10, new int[] {});
    int[][] rootBranchOdds = { { 33, 0 }, { 66, 1 }, { 100, 2 } };
    return new ServicePointTree(new ServicePoint(simulation, 1, rootBranchOdds), 1, simulation);
  }
}
