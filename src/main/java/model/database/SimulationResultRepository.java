package model.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import model.ServicePoint;

/**
 * Persists the finished simulation performance data using the schema in
 * Queries/Project.sql.
 */
public class SimulationResultRepository {
  private static final String URL = "jdbc:mariadb://localhost:3306/simulation";
  private static final String USER = "appuser";
  private static final String PASSWORD = "password";

  private static final String INSERT_SERVICE_POINT = """
      INSERT INTO service_point (arrived, serviced, active_time, total_time)
      VALUES (?, ?, ?, ?)
      """;

  private static final String INSERT_CUSTOMER = """
      INSERT INTO customer (response_time)
      VALUES (?)
      """;

  public void save(List<ServicePoint> servicePoints, List<Integer> customerResponseTimes) throws SQLException {
    try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
      boolean previousAutoCommit = connection.getAutoCommit();
      connection.setAutoCommit(false);
      try {
        saveServicePoints(connection, servicePoints);
        saveCustomers(connection, customerResponseTimes);
        connection.commit();
      } catch (SQLException e) {
        connection.rollback();
        throw e;
      } finally {
        connection.setAutoCommit(previousAutoCommit);
      }
    }
  }

  private void saveServicePoints(Connection connection, List<ServicePoint> servicePoints) throws SQLException {
    try (PreparedStatement statement = connection.prepareStatement(INSERT_SERVICE_POINT)) {
      for (ServicePoint servicePoint : servicePoints) {
        statement.setInt(1, servicePoint.getArrived());
        statement.setInt(2, servicePoint.getServed());
        statement.setInt(3, servicePoint.getActiveTime());
        statement.setInt(4, servicePoint.getTotalTime());
        statement.addBatch();
      }
      statement.executeBatch();
    }
  }

  private void saveCustomers(Connection connection, List<Integer> customerResponseTimes) throws SQLException {
    try (PreparedStatement statement = connection.prepareStatement(INSERT_CUSTOMER)) {
      for (int responseTime : customerResponseTimes) {
        statement.setInt(1, responseTime);
        statement.addBatch();
      }
      statement.executeBatch();
    }
  }
}
