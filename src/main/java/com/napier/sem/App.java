
package com.napier.sem;

import java.sql.*;

public class App {

    public static void main(String[] args) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load SQL driver");
            return;
        }

        Connection con = null;
        int retries = 100;

        for (int i = 0; i < retries; i++) {
            System.out.println("Connecting to database...");

            try {
                Thread.sleep(1000);

                con = DriverManager.getConnection(
                        "jdbc:mysql://db:3306/world?useSSL=false&allowPublicKeyRetrieval=true",
                        "root",
                        "example"
                );

                System.out.println("Successfully connected");
                break;

            } catch (SQLException e) {
                System.out.println(
                        "Failed to connect to database attempt " + (i + 1)
                );
                System.out.println(e.getMessage());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Database connection interrupted.");
                return;
            }
        }

        if (con == null) {
            System.out.println("Could not connect to database.");
            return;
        }

        try {
            TopCitiesInContinent.generateReport(con, "Asia", 10);

        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("Error closing connection to database");
            }
        }
    }
}
