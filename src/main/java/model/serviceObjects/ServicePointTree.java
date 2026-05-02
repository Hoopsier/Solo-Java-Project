package model.serviceObjects;

import java.util.ArrayList;
import java.util.List;

import model.ServicePoint;
import model.Simulation;

public class ServicePointTree {
  private ServicePoint self;
  private List<ServicePointTree> children = new ArrayList<>();
  private int depth;

  public ServicePointTree(ServicePoint servicePoint, int _depth, Simulation simulation) {
    self = servicePoint;
    depth = _depth;

    switch (depth) {
      case 1:
        int[][] odds = { { 33, 0 }, { 66, 1 }, { 100, 2 } }; // this one is here because parser fails at constant
                                                             // matrixes in params apparently
        for (int i = 0; i < 5; i++) {
          children.add(new ServicePointTree(new ServicePoint(simulation, 2, odds), 2, simulation));
        }
        return;
      case 2:
        int[][] odds2 = { { 33, 0 }, { 66, 1 }, { 100, 2 } }; // this one is here because parser fails at constant
                                                              // matrixes in params apparently
        for (int i = 0; i < 2; i++) {
          // TODO: make -1 serviceCount do the 20 predefined sps
          children.add(new ServicePointTree(new ServicePoint(simulation, 6, odds2), 3, simulation));
        }
        return;
      case 3:
        int[][] odds3 = { { 100, 0 } }; // this one is here because parser fails at constant matrixes in params
                                        // apparently
        for (int i = 0; i < 5; i++) {
          children.add(new ServicePointTree(new ServicePoint(simulation, 10, odds3), 4, simulation));
        }
        return;
    }
  }

  public boolean equals(int id) {
    return this.self.getSPId() == id;
  }

  public ServicePointTree find(int target) {
    if (equals(target)) {
      return this;
    }
    for (ServicePointTree child : children) {
      ServicePointTree result = child.find(target);
      if (result != null) {
        return result;
      }
    }
    return null;
  }

  public ServicePoint getSelf() {
    return self;
  }

  public ServicePoint getChild(int index) {
    return children.get(index).getSelf();
  }
}
