package com.napier.sem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class App {

    private static final int MAX_ATTEMPTS = 100;
    private static final long RETRY_DELAY_MS = 3000;

    public static void main(String[] args) {

        // Load the MySQL JDBC driver.
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println(
                    "ERROR: MySQL JDBC driver was not found. " +
                            "Check your pom.xml dependencies."
            );
            return;
        }

        // Configure the database connection.
        // Docker Compose sets DB_HOST=db:3306.
        // For running directly on Windows, the default is localhost:33060.
        String dbHost = System.getenv().getOrDefault(
                "DB_HOST", "localhost:33060"
        );

        String dbUrl = System.getenv("DB_URL");

        if (dbUrl == null || dbUrl.isBlank()) {
            dbUrl = "jdbc:mysql://" + dbHost + "/world"
                    + "?useSSL=false"
                    + "&allowPublicKeyRetrieval=true"
                    + "&connectTimeout=5000"
                    + "&socketTimeout=30000";
        }

        String dbUser = System.getenv().getOrDefault(
                "DB_USER", "root"
        );

        String dbPassword = System.getenv().getOrDefault(
                "DB_PASSWORD", "example"
        );

        System.out.println("Starting Global DevOps application...");
        System.out.println("Database URL: " + dbUrl);

        // Connect to MySQL with retries.
        Connection con = connectToDatabase(
                dbUrl, dbUser, dbPassword
        );

        if (con == null) {
            System.err.println(
                    "ERROR: Could not connect to MySQL. " +
                            "Check the database container, credentials, " +
                            "database initialization and network configuration."
            );
            return;
        }

        try (Connection connection = con) {

            System.out.println(
                    "Database connection established successfully."
            );

            // Check that the expected database and tables are available.
            if (!isDatabaseReady(connection)) {
                System.err.println(
                        "ERROR: The world database or its required " +
                                "tables are unavailable."
                );
                return;
            }

            // Optional argument used by the country and district reports.
            String queryArg = args.length > 0
                    ? String.join(" ", args).trim()
                    : "";

            // US02: Countries grouped by continent.
            runReport("US02 - Countries Grouped by Continent",
                    () -> US02_view_all_countries_in_a_continent
                            .printReport(connection));

            // US26: Total world population.
            runReport("US26 - Total World Population",
                    () -> TotalWorldPopulation.printReport(connection));

            // US17: Capital cities throughout the world.
            runReport("US17 - All Capital Cities",
                    () -> {
                        ArrayList<CapitalCity> capitals =
                                AllCapitalsCitiesInTheWorld_US17
                                        .getAll(connection);

                        AllCapitalsCitiesInTheWorld_US17
                                .printReport(capitals);
                    });

            // US18: Capital cities in Europe.
            runReport("US18 - Capital Cities by Continent",
                    () -> {
                        String continent = "Europe";

                        ArrayList<CapitalCity> capitals =
                                CapitalCitiesInAcontinent
                                        .getCapitalCitiesByContinent(
                                                connection, continent
                                        );

                        CapitalCitiesInAcontinent
                                .printReport(capitals, continent);
                    });

            // US19: Capital cities in the Caribbean region.
            runReport("US19 - Capital Cities by Region",
                    () -> {
                        String region = "Caribbean";

                        ArrayList<CapitalCity> capitals =
                                CapitalsCitiesInAregion
                                        .getCapitalCitiesByRegion(
                                                connection, region
                                        );

                        CapitalsCitiesInAregion
                                .printReport(capitals, region);
                    });

            // US25: Population distribution by country.
            runReport("US25 - Population by Country",
                    () -> {
                        ArrayList<Population> populations =
                                ViewPopulationByCountry
                                        .getPopulationByCountry(connection);

                        ViewPopulationByCountry
                                .printReport(populations);
                    });

            // Country population report.
            runReport("Country Population Report",
                    () -> {
                        CountryPopulationReport report =
                                new CountryPopulationReport(connection);

                        report.run(queryArg);
                    });

            // District population report.
            runReport("District Population Report",
                    () -> {
                        DistrictPopulationReport report =
                                new DistrictPopulationReport(connection);

                        report.run(queryArg);
                    });

            System.out.println(
                    "\nAll configured reports have finished."
            );

        } catch (SQLException e) {
            System.err.println(
                    "ERROR while closing the database connection: "
                            + e.getMessage()
            );
        }
    }

    /**
     * Establishes a database connection, retrying while MySQL starts.
     */
    private static Connection connectToDatabase(
            String dbUrl,
            String dbUser,
            String dbPassword) {

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {

            System.out.println(
                    "Connecting to MySQL (attempt "
                            + attempt + "/" + MAX_ATTEMPTS + ")..."
            );

            try {
                Connection connection = DriverManager.getConnection(
                        dbUrl, dbUser, dbPassword
                );

                System.out.println("Connected to MySQL.");
                return connection;

            } catch (SQLException e) {

                System.err.println(
                        "Connection failed: " + e.getMessage()
                );

                if (attempt == MAX_ATTEMPTS) {
                    break;
                }

                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();

                    System.err.println(
                            "Database connection retries were interrupted."
                    );
                    return null;
                }
            }
        }

        return null;
    }

    /**
     * Checks whether the world database contains the required tables.
     */
    private static boolean isDatabaseReady(Connection connection) {

        String[] requiredTables = {
                "country", "city", "countrylanguage"
        };

        try {
            for (String table : requiredTables) {
                if (!tableExists(connection, table)) {
                    System.err.println(
                            "Missing required table: " + table
                    );
                    return false;
                }
            }

            System.out.println(
                    "Database check passed: required tables exist."
            );
            return true;

        } catch (SQLException e) {
            System.err.println(
                    "Could not validate database tables: "
                            + e.getMessage()
            );
            return false;
        }
    }

    private static boolean tableExists(
            Connection connection, String tableName)
            throws SQLException {

        String sql =
                "SELECT COUNT(*) FROM information_schema.tables "
                        + "WHERE table_schema = DATABASE() "
                        + "AND table_name = ?";

        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, tableName);

            try (var results = statement.executeQuery()) {
                return results.next() && results.getInt(1) > 0;
            }
        }
    }

    /**
     * Runs one report without preventing later reports from running
     * if an unexpected runtime exception occurs.
     */
    private static void runReport(String name, Runnable report) {

        System.out.println("\n========================================");
        System.out.println("Running: " + name);
        System.out.println("========================================");

        try {
            report.run();
        } catch (RuntimeException e) {
            System.err.println(
                    "Report failed: " + name
                            + " - " + e.getMessage()
            );
            e.printStackTrace();
        }
    }
}