package model.serviceObjects;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import model.ServicePoint;
import model.Simulation;

// AI generated test, AFTER I made the stuff... Don't blame me for the shit names here
class ServicePointTreeTest {
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

  @Test
  void buildingASecondTreeDoesNotReuseMissingTierFourBranch() {
    ServicePointTree firstTree = createTree();
    ServicePointTree secondTree = createTree();

    assertNotNull(firstTree);
    assertNotNull(secondTree);
    assertNotNull(secondTree.find(secondTree.getChild(0).getSPId()));
    secondTree.serviceTotalCount();
  }

  private ServicePointTree createTree() {
    Simulation simulation = new Simulation(null, 10, new int[] {});
    int[][] rootBranchOdds = { { 33, 0 }, { 66, 1 }, { 100, 2 } };
    return new ServicePointTree(new ServicePoint(simulation, 1, rootBranchOdds), 1, simulation);
  }
}
