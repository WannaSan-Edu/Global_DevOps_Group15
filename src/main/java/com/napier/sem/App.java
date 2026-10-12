package com.napier.sem;

import java.sql.*;
import java.util.ArrayList;

public class App {

    public static void main(String[] args) {

        // Load MySQL JDBC driver.
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Could not load MySQL JDBC driver.");
            return;
        }

        // District name is optional.
        // No arguments: show the district report for all districts.
        // Example: Ontario
        String districtName =
                args.length > 0 ? String.join(" ", args).trim() : "";

        // Database configuration.
        // Docker uses DB_URL, DB_USER and DB_PASSWORD.
        // Defaults are for running directly from IntelliJ.
        String dbUrl = System.getenv().getOrDefault(
                "DB_URL",
                "jdbc:mysql://localhost:33060/world"
                        + "?useSSL=false&allowPublicKeyRetrieval=true"
        );

        String dbUser = System.getenv().getOrDefault(
                "DB_USER", "root"
        );

        String dbPassword = System.getenv().getOrDefault(
                "DB_PASSWORD", "example"
        );

        Connection con = null;

        // Retry the database connection while MySQL starts.
        for (int i = 0; i < 100; i++) {
            System.out.println("Connecting to database...");

            try {
                con = DriverManager.getConnection(
                        dbUrl, dbUser, dbPassword
                );

                System.out.println("Successfully connected.");
                break;

            } catch (SQLException e) {
                System.err.println(
                        "Database connection attempt " + (i + 1)
                                + " failed: " + e.getMessage()
                );

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    System.err.println(
                            "Database connection was interrupted."
                    );
                    return;
                }
            }
        }

        if (con == null) {
            System.err.println(
                    "Could not connect to the database after 100 attempts."
            );
            return;
        }

        try {
            // Extract all capital cities in the world using US17 implementation
            ArrayList<CapitalCity> capitalCities = AllCapitalsCitiesInTheWorld_US17.getAll(con);

            // Print out the report
            AllCapitalsCitiesInTheWorld_US17.printReport(capitalCities);
        } catch (Exception e) {
            System.out.println("Error executing capital cities report");
            e.printStackTrace();
        }

        // Run the district population report only.
        try (Connection connection = con) {
            DistrictPopulationReport report =
                    new DistrictPopulationReport(connection);

            report.run(districtName);

        } catch (Exception e) {
            System.err.println(
                    "Error executing district population report."
            );
            e.printStackTrace();
        }
    }
}