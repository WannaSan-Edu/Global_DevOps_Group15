package com.napier.sem;

import java.sql.*;
import java.util.ArrayList;

public class App {

    public static void main(String[] args) {
        // 1. Load MySQL JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Could not load MySQL JDBC driver.");
            return;
        }

        // 2. Optional command-line argument for filtering (e.g., country or district name)
        String queryArg = args.length > 0 ? String.join(" ", args).trim() : "";

        // 3. Database configuration via environment variables with fallback defaults
        String dbUrl = System.getenv().getOrDefault(
                "DB_URL",
                "jdbc:mysql://localhost:33060/world?useSSL=false&allowPublicKeyRetrieval=true"
        );
        String dbUser = System.getenv().getOrDefault("DB_USER", "root");
        String dbPassword = System.getenv().getOrDefault("DB_PASSWORD", "example");

        Connection con = null;

        // 4. Retry loop to handle database container startup delay
        int maxAttempts = 100;
        for (int i = 0; i < maxAttempts; i++) {
            System.out.println("Connecting to database (attempt " + (i + 1) + "/" + maxAttempts + ")...");
            try {
                con = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                System.out.println("Successfully connected to the database.");
                break;
            } catch (SQLException e) {
                System.err.println("Database connection attempt " + (i + 1) + " failed: " + e.getMessage());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    System.err.println("Database connection was interrupted.");
                    return;
                }
            }
        }

        if (con == null) {
            System.err.println("Could not connect to the database after " + maxAttempts + " attempts.");
            return;
        }

        // 5. Execute all reports sequentially using the shared connection
        try {
            // Report 1: Total World Population (US26)
            System.out.println("\n--- Running Total World Population Report (US26) ---");
            TotalWorldPopulation.printReport(con);

            // Report 2: All Capital Cities in the World (US17)
            System.out.println("\n--- Running All Capital Cities in the World Report (US17) ---");
            ArrayList<CapitalCity> allCapitalCities = AllCapitalsCitiesInTheWorld_US17.getAll(con);
            AllCapitalsCitiesInTheWorld_US17.printReport(allCapitalCities);

            // Report 3: Capital Cities in a Continent by Population (US18)
            System.out.println("\n--- Running Capital Cities in Europe by Population Report (US18) ---");
            String continent = "Europe";
            ArrayList<CapitalCity> continentCapitals = CapitalCitiesInAcontinent.getCapitalCitiesByContinent(con, continent);
            CapitalCitiesInAcontinent.printReport(continentCapitals, continent);

            // Report 4: Capital Cities in a Region by Population (US19)
            System.out.println("\n--- Running Capital Cities in Caribbean by Population Report (US19) ---");
            String region = "Caribbean";
            ArrayList<CapitalCity> regionCapitals = CapitalsCitiesInAregion.getCapitalCitiesByRegion(con, region);
            CapitalsCitiesInAregion.printReport(regionCapitals, region);

            // Report 5: Population by Country (US25)
            System.out.println("\n--- Running Population by Country Report (US25) ---");
            ArrayList<Population> countryPopulations = ViewPopulationByCountry.getPopulationByCountry(con);
            ViewPopulationByCountry.printReport(countryPopulations);

            // Report 6: Country Population Report
            System.out.println("\n--- Running Country Population Report ---");
            CountryPopulationReport countryReport = new CountryPopulationReport(con);
            countryReport.run(queryArg);

            // Report 7: District Population Report
            System.out.println("\n--- Running District Population Report ---");
            DistrictPopulationReport districtReport = new DistrictPopulationReport(con);
            districtReport.run(queryArg);

        } catch (Exception e) {
            System.err.println("An error occurred while executing reports:");
            e.printStackTrace();
        } finally {
            // 6. Gracefully close the shared database connection once ALL reports have finished
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                    System.out.println("\nDatabase connection closed successfully.");
                }
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
}
