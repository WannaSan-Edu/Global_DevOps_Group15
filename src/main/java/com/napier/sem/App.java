package com.napier.sem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Application entry point for the report classes present in this project.
 *
 * One database connection is shared by every report and is closed only after
 * all reports have finished. Report methods should close their own Statements
 * and ResultSets, but must not close the shared Connection.
 */
public class App {

    private static final int MAX_ATTEMPTS = 100;
    private static final long RETRY_DELAY_MS = 3000;

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: MySQL JDBC driver was not found. Check pom.xml.");
            return;
        }

        // Docker Compose: DB_HOST=db:3306
        // Running on the host machine: default localhost:33060
        String dbHost = System.getenv().getOrDefault("DB_HOST", "localhost:33060");
        String dbUrl = System.getenv("DB_URL");

        if (dbUrl == null || dbUrl.isBlank()) {
            dbUrl = "jdbc:mysql://" + dbHost + "/world"
                    + "?useSSL=false"
                    + "&allowPublicKeyRetrieval=true"
                    + "&connectTimeout=5000"
                    + "&socketTimeout=30000";
        }

        String dbUser = System.getenv().getOrDefault("DB_USER", "root");
        String dbPassword = System.getenv().getOrDefault("DB_PASSWORD", "example");

        System.out.println("Starting Global DevOps reports...");
        System.out.println("Database URL: " + dbUrl);

        Connection connection = connectToDatabase(dbUrl, dbUser, dbPassword);
        if (connection == null) {
            System.err.println("ERROR: Could not connect to MySQL. Check the database "
                    + "service, host/port, credentials, and world database.");
            return;
        }

        // The shared connection stays open for the entire report sequence.
        try (Connection con = connection) {
            System.out.println("Database connection established.");

            if (!isDatabaseReady(con)) {
                System.err.println("ERROR: Required world database tables are unavailable.");
                return;
            }

            // Optional arguments:
            // args[0] = country name for the country population report
            // args[1] = district name for the district population report
            // If omitted, those reports display all available records.
            String countryArg = args.length > 0 ? args[0].trim() : "";
            String districtArg = args.length > 1 ? args[1].trim() : "";

            // US02: Countries grouped by continent.
            runReport("US02 - Countries Grouped by Continent",
                    () -> US02_view_all_countries_in_a_continent.printReport(con));

            // US03: View all countries in each region, ordered by population.
            runReport("US03 - View All Countries in a Region",
                    () -> printCountriesByRegion(con));

            // US07: View all cities in the world.
            runReport("US07 - View All Cities in the World",
                    () -> printAllCities(con));

            // US13: Top Populated Cities in a Continent
            runReport("US13 - Top Populated Cities in a Continent",
                    () -> TopCitiesInContinent.generateReport(con, "Asia", 10));

            // US14: Top Populated Cities in a Region
            runReport("US14 - Top Populated Cities in a Region",
                    () -> TopCitiesInRegion.generateReport(con, "Southeast Asia", 10));

            // US15: Top Populated Cities in a Country
            runReport("US15 - Top Populated Cities in a Country",
                    () -> TopCitiesInCountry.generateReport(con, "Japan", 10));

            // US17: View all capital cities in the world.
            runReport("US17 - All Capital Cities in the World", () -> {
                ArrayList<CapitalCity> capitals = AllCapitalsCitiesInTheWorld_US17.getAll(con);
                AllCapitalsCitiesInTheWorld_US17.printReport(capitals);
            });

            // US18: Capital cities by continent.
            runReport("US18 - Capital Cities by Continent", () -> {
                String continent = "Europe";
                ArrayList<CapitalCity> capitals = CapitalCitiesInAcontinent
                        .getCapitalCitiesByContinent(con, continent);
                CapitalCitiesInAcontinent.printReport(capitals, continent);
            });

            // US19: Capital cities by region.
            runReport("US19 - Capital Cities by Region", () -> {
                String region = "Caribbean";
                ArrayList<CapitalCity> capitals = CapitalsCitiesInAregion
                        .getCapitalCitiesByRegion(con, region);
                CapitalsCitiesInAregion.printReport(capitals, region);
            });

            // US25: Total, city, and non-city population for each country.
            runReport("US25 - Population by Country", () -> {
                ArrayList<Population> populations = ViewPopulationByCountry.getPopulationByCountry(con);
                ViewPopulationByCountry.printReport(populations);
            });

            // US26: Total world population.
            runReport("US26 - Total World Population",
                    () -> TotalWorldPopulation.printReport(con));

            // Country population report; blank argument displays all countries.
            runReport("Country Population Report", () -> {
                CountryPopulationReport report = new CountryPopulationReport(con);
                report.run(countryArg);
            });

            // District population report; blank argument displays all districts.
            runReport("District Population Report", () -> {
                DistrictPopulationReport report = new DistrictPopulationReport(con);
                report.run(districtArg);
            });

            System.out.println("\nAll reports available in this project have finished.");

        } catch (SQLException e) {
            System.err.println("ERROR while closing the shared database connection: " + e.getMessage());
        }
    }

    private static Connection connectToDatabase(String dbUrl, String dbUser, String dbPassword) {
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            System.out.println("Connecting to MySQL (attempt " + attempt + "/" + MAX_ATTEMPTS + ")...");

            try {
                Connection con = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
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
                String sql = "SELECT COUNT(*) FROM information_schema.tables "
                        + "WHERE table_schema = DATABASE() "
                        + "AND table_name = ?";

                try (PreparedStatement statement = con.prepareStatement(sql)) {
                    statement.setString(1, table);
                    try (ResultSet result = statement.executeQuery()) {
                        if (!result.next() || result.getInt(1) == 0) {
                            System.err.println("Missing required table: " + table);
                            return false;
                        }
                    }
                }
            }

            System.out.println("Database check passed.");
            return true;
        } catch (SQLException e) {
            System.err.println("Could not validate database tables: " + e.getMessage());
            return false;
        }
    }

    /**
     * US03: Query real country records, grouped by region and ordered by
     * population descending within each region.
     */
    private static void printCountriesByRegion(Connection con) throws SQLException {
        String sql = "SELECT c.Code, c.Name, c.Continent, c.Region, "
                + "c.Population, capital.Name AS Capital "
                + "FROM country c "
                + "LEFT JOIN city capital ON c.Capital = capital.ID "
                + "ORDER BY c.Region ASC, c.Population DESC, c.Name ASC";

        String currentRegion = null;
        String line = "-".repeat(125);
        int count = 0;

        try (PreparedStatement statement = con.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                String region = rs.getString("Region");
                if (region == null) {
                    region = "Unknown";
                }

                if (!region.equals(currentRegion)) {
                    if (currentRegion != null) {
                        System.out.println(line);
                    }
                    currentRegion = region;
                    System.out.println("\nRegion: " + currentRegion);
                    System.out.println(line);
                    System.out.printf(
                            "| %-5s | %-24s | %-18s | %-28s | %15s | %-20s |%n",
                            "Code", "Country", "Continent", "Region",
                            "Population", "Capital"
                    );
                    System.out.println(line);
                }

                long population = rs.getLong("Population");
                String capital = rs.getString("Capital");
                if (capital == null) {
                    capital = "Unknown";
                }

                System.out.printf(
                        Locale.US,
                        "| %-5s | %-24.24s | %-18.18s | %-28.28s | %,15d | %-20.20s |%n",
                        rs.getString("Code"),
                        rs.getString("Name"),
                        rs.getString("Continent"),
                        region,
                        population,
                        capital
                );
                count++;
            }
        }

        if (count == 0) {
            System.out.println("No country records found.");
        } else {
            System.out.println(line);
            System.out.println("Countries displayed: " + count);
        }
    }

    /**
     * US07: Query and display every city in the database.
     */
    private static void printAllCities(Connection con) throws SQLException {
        String sql = "SELECT city.Name AS CityName, country.Name AS CountryName, "
                + "city.District AS DistrictName, "
                + "city.Population AS CityPopulation "
                + "FROM city "
                + "JOIN country ON city.CountryCode = country.Code "
                + "ORDER BY city.Population DESC, city.Name ASC";

        int count = 0;
        System.out.println("\nUS07 - All Cities in the World");
        System.out.printf(
                "%-35s %-45s %-30s %18s%n",
                "City", "Country", "District", "Population"
        );
        System.out.println("-".repeat(135));

        try (PreparedStatement statement = con.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                City city = new City();
                city.setName(rs.getString("CityName"));
                city.setCountry(rs.getString("CountryName"));
                city.setDistrict(rs.getString("DistrictName"));
                city.setPopulation(rs.getLong("CityPopulation"));

                System.out.printf(
                        Locale.US,
                        "%-35.35s %-45.45s %-30.30s %,18d%n",
                        city.getName(),
                        city.getCountry(),
                        city.getDistrict() == null ? "" : city.getDistrict(),
                        city.getPopulation()
                );
                count++;
            }
        }

        if (count == 0) {
            System.out.println("No city records found.");
        } else {
            System.out.println("Cities displayed: " + count);
        }
    }

    @FunctionalInterface
    private interface SqlReport {
        void run() throws SQLException;
    }

    /**
     * A report failure is logged, and the next report still runs. This method
     * never closes the shared database connection.
     */
    private static void runReport(String name, SqlReport report) {
        System.out.println("\n==================================================");
        System.out.println("Running: " + name);
        System.out.println("==================================================");

        try {
            report.run();
        } catch (SQLException | RuntimeException e) {
            System.err.println("Report failed: " + name + " - " + e.getMessage());
        }
    }
}
