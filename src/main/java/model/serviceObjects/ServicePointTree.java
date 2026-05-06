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
    ServicePoint sp;
    boolean hasTierFour = false;
    switch (depth) {
      case 1:
        int[][] odds = { { 33, 0 }, { 66, 1 }, { 100, 2 } }; // three languages
        // i is branch index
        for (int i = 0; i < 3; i++) {
          sp = new ServicePoint(simulation, 2, odds);
          sp.setParallels(5);
          children.add(new ServicePointTree(sp, 2, simulation));
        }
        return;
      case 2:
        int[][] odds2 = { { 70, 0 }, { 30, 1 } }; // 2 categories
        // i is branch index
        for (int i = 0; i < 3; i++) {
          sp = new ServicePoint(simulation, 6, odds2);
          sp.setParallels(2);
          children.add(new ServicePointTree(sp, 3, simulation));
        }
        return;
      case 3:
        int[][] odds3 = { { 100, 0 } }; // one set of 21 service points
        sp = new ServicePoint(simulation, 10, odds3);
        sp.setParallels(2);
        children.add(new ServicePointTree(sp, 4, simulation));
        return;
      case 4:
        hasTierFour = true;
        int[][] odds4 = {}; // doesn't have more children
        sp = new ServicePoint(simulation, 10, odds4);
        sp.setParallels(20);
        return;
    }
  }

  public boolean equals(int id) {
    return this.self.getSPId() == id;
  }

  public ServicePointTree find(int target) {
    if (self == null) {
      return null;
    }

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

  public int serviceTotalCount() {
    int sum = 1 + self.getParallels().size();
    for (ServicePointTree child : children) {
      sum += child.serviceTotalCount();
    }
    return sum;
  }
}
