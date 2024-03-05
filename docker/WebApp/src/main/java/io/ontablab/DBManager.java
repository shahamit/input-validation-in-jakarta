package io.ontablab;

import java.sql.*;

public class DBManager {
  private static final String URL = System.getenv("DB_URL");
  private Connection connection;

  public DBManager() {
    try {
      // Import sqlite-jdbc JAR
      Class.forName("org.sqlite.JDBC");

      this.connection = DriverManager.getConnection(URL);

      System.out.println("[+] Connection to SQLite has been established");
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public Statement createStatement() throws SQLException {
    return this.connection.createStatement();
  }

  public void close() {
    try {
      this.connection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
