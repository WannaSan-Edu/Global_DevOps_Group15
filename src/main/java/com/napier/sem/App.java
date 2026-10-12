package com.napier.sem;

import java.sql.*;
import java.util.ArrayList;

public class App {

    public static void main(String[] args) {
        try {
            // Load Database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Could not load MySQL JDBC driver.");
            return;
        }

        // No argument -> empty string -> show all countries.
        // With argument -> show only that country.
        String countryName =
                args.length > 0 ? String.join(" ", args).trim() : "";

        String host = System.getenv().getOrDefault("DB_HOST", "127.0.0.1:33060");
        String url = "jdbc:mysql://" + host
                + "/world?useSSL=false&allowPublicKeyRetrieval=true";

        String username = "root";
        String password = "example";

        Connection con = null;

        for (int i = 0; i < 100; i++) {
            System.out.println("Connecting to database...");

            try {
                con = DriverManager.getConnection(url, username, password);
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
                    System.err.println("Database connection was interrupted.");
                    return;
                }
            }
        }

        if (con != null) {
            try {
                // Specify the region you want to query
                String region = "Caribbean";

                // US19: Capital cities in a region by population
                ArrayList<CapitalCity> capitalCities = CapitalsCitiesInAregion.getCapitalCitiesByRegion(con, region);
                CapitalsCitiesInAregion.printReport(capitalCities, region);
            } catch (Exception e) {
                System.out.println("Error executing region capital cities report");
                e.printStackTrace();
            }

            try {
                // Specify the continent you want to query
                String continent = "Europe";

                // US18: Capital cities in a continent by population
                ArrayList<CapitalCity> capitalCities = CapitalCitiesInAcontinent.getCapitalCitiesByContinent(con, continent);
                CapitalCitiesInAcontinent.printReport(capitalCities, continent);
            } catch (Exception e) {
                System.out.println("Error executing continent capital cities report");
                e.printStackTrace();
            }

            try (Connection connection = con) {
                CountryPopulationReport report =
                        new CountryPopulationReport(connection);

                report.run(countryName);
                System.out.println("Successfully disconnected from database.");

            } catch (SQLException e) {
                System.err.println("Error closing database connection.");
            }
        } else {
            System.out.println("Failed to establish a database connection.");
        }
    }
}
