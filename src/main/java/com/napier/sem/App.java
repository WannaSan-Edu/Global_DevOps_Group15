package com.napier.sem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Application entry point.
 *
 * All reports receive the same active database connection. Individual report
 * classes must not close this connection; App closes it after all reports run.
 */
public class App {

    private static final int MAX_ATTEMPTS = 100;
    private static final long RETRY_DELAY_MS = 3000;

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println(
                    "ERROR: MySQL JDBC driver was not found. Check pom.xml."
            );
            return;
        }

        // In Docker Compose use DB_HOST=db:3306.
        // When running directly on the host, default to localhost:33060.
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

        String dbUser = System.getenv().getOrDefault("DB_USER", "root");
        String dbPassword = System.getenv().getOrDefault(
                "DB_PASSWORD", "example"
        );

        System.out.println("Starting Global DevOps application.");
        System.out.println("Connecting to database: " + dbUrl);

        Connection connection = connectToDatabase(dbUrl, dbUser, dbPassword);
        if (connection == null) {
            System.err.println(
                    "ERROR: Could not connect to MySQL after "
                            + MAX_ATTEMPTS + " attempts. Check the DB container, "
                            + "host, port, credentials and world.sql initialization."
            );
            return;
        }

        // This is the only place that closes the shared connection.
        try (Connection con = connection) {
            System.out.println("Database connection established.");

            if (!isDatabaseReady(con)) {
                System.err.println(
                        "ERROR: The world database is missing required tables."
                );
                return;
            }

            // Optional argument for the country/district population reports.
            // No argument means those reports display all available records.
            String queryArg = args.length == 0
                    ? ""
                    : String.join(" ", args).trim();

            /*
             * US07 - View all cities in the world.
             * This class must also be present in the merged source tree.
             * It must use the supplied connection and must not close it.
             */
            runReport("US07 - View All Cities in the World", () -> {
                ViewAllCitiesInTheWorld us07 =
                        new ViewAllCitiesInTheWorld(con);
                us07.getAllCitiesInWorld();
            });

            // US17 - All capital cities in the world.
            runReport("US17 - All Capital Cities in the World", () -> {
                ArrayList<CapitalCity> capitals =
                        AllCapitalsCitiesInTheWorld_US17.getAll(con);
                AllCapitalsCitiesInTheWorld_US17.printReport(capitals);
            });

            // US18 - Capital cities by continent (currently Europe).
            runReport("US18 - Capital Cities by Continent", () -> {
                String continent = "Europe";
                ArrayList<CapitalCity> capitals =
                        CapitalCitiesInAcontinent
                                .getCapitalCitiesByContinent(con, continent);
                CapitalCitiesInAcontinent.printReport(capitals, continent);
            });

            // US19 - Capital cities by region (currently Caribbean).
            runReport("US19 - Capital Cities by Region", () -> {
                String region = "Caribbean";
                ArrayList<CapitalCity> capitals =
                        CapitalsCitiesInAregion
                                .getCapitalCitiesByRegion(con, region);
                CapitalsCitiesInAregion.printReport(capitals, region);
            });

            // US25 - Total, city and non-city population by country.
            runReport("US25 - Population by Country", () -> {
                ArrayList<Population> populations =
                        ViewPopulationByCountry.getPopulationByCountry(con);
                ViewPopulationByCountry.printReport(populations);
            });

            // US26 - Total world population.
            runReport("US26 - Total World Population", () ->
                    TotalWorldPopulation.printReport(con));

            // Country population report. A command-line argument selects one
            // country; without an argument, all country totals are displayed.
            runReport("Country Population Report", () -> {
                CountryPopulationReport report =
                        new CountryPopulationReport(con);
                report.run(queryArg);
            });

            // District population report. With no argument, all district
            // totals are displayed. With an argument, it is treated as the
            // requested district name.
            runReport("District Population Report", () -> {
                DistrictPopulationReport report =
                        new DistrictPopulationReport(con);
                report.run(queryArg);
            });

            System.out.println("\nAll configured reports have finished.");

        } catch (SQLException e) {
            System.err.println(
                    "ERROR while closing or using the database connection: "
                            + e.getMessage()
            );
        }
    }

    private static Connection connectToDatabase(
            String dbUrl, String dbUser, String dbPassword) {

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            System.out.println(
                    "Connecting to MySQL (attempt " + attempt
                            + "/" + MAX_ATTEMPTS + ")..."
            );

            try {
                Connection con = DriverManager.getConnection(
                        dbUrl, dbUser, dbPassword
                );
                System.out.println("Connected to MySQL.");
                return con;
            } catch (SQLException e) {
                System.err.println("Connection failed: " + e.getMessage());

                if (attempt == MAX_ATTEMPTS) {
                    break;
                }

                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    System.err.println("Database retry was interrupted.");
                    return null;
                }
            }
        }

        return null;
    }

    private static boolean isDatabaseReady(Connection con) {
        String[] requiredTables = {"country", "city", "countrylanguage"};

        try {
            for (String table : requiredTables) {
                if (!tableExists(con, table)) {
                    System.err.println("Missing required table: " + table);
                    return false;
                }
            }
            System.out.println("Database check passed.");
            return true;
        } catch (SQLException e) {
            System.err.println(
                    "Could not validate database tables: " + e.getMessage()
            );
            return false;
        }
    }

    private static boolean tableExists(Connection con, String tableName)
            throws SQLException {
        String sql =
                "SELECT COUNT(*) FROM information_schema.tables "
                        + "WHERE table_schema = DATABASE() AND table_name = ?";

        try (var statement = con.prepareStatement(sql)) {
            statement.setString(1, tableName);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() && result.getInt(1) > 0;
            }
        }
    }

    private static void runReport(String name, Runnable report) {
        System.out.println("\n========================================");
        System.out.println("Running: " + name);
        System.out.println("========================================");

        try {
            report.run();
        } catch (RuntimeException e) {
            System.err.println(
                    "Report failed: " + name + " - " + e.getMessage()
            );
            e.printStackTrace();
        }
    }
}
