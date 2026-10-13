package com.napier.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CountryPopulationReport {

    private final Connection con;

    public CountryPopulationReport(Connection con) {
        if (con == null) {
            throw new IllegalArgumentException(
                    "Database connection cannot be null."
            );
        }
        this.con = con;
    }

    /**
     * Retrieves the total population of the selected country.
     *
     * @param countryName the country selected by the user
     * @return a Population object, or null if the country
     *         or its population data is unavailable
     * @throws SQLException if a database error occurs
     */
    public Population getCountryPopulation(String countryName)
            throws SQLException {

        if (countryName == null || countryName.isBlank()) {
            return null;
        }

        String sql =
                "SELECT Name, Population FROM country WHERE Name = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, countryName.trim());

            try (ResultSet rset = pstmt.executeQuery()) {

                if (!rset.next()) {
                    return null;
                }

                // Distinguish SQL NULL from a numeric population.
                Long total = rset.getObject("Population", Long.class);

                if (total == null) {
                    return null;
                }

                Population pop = new Population();
                pop.setName(rset.getString("Name"));
                pop.setTotal(total);

                return pop;
            }
        }
    }

    /**
     * Retrieves every country with population data,
     * ordered from largest to smallest population.
     */
    public List<Population> getAllCountryPopulations() throws SQLException {

        String sql =
                "SELECT Name, Population FROM country "
                        + "WHERE Population IS NOT NULL "
                        + "ORDER BY Population DESC";

        List<Population> list = new ArrayList<>();

        try (PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rset = pstmt.executeQuery()) {

            while (rset.next()) {
                Population pop = new Population();
                pop.setName(rset.getString("Name"));
                pop.setTotal(rset.getLong("Population"));
                list.add(pop);
            }
        }

        return list;
    }

    /**
     * Displays the report for a single country.
     */
    public void display(Population pop, String requestedCountry) {

        if (pop == null) {
            System.out.println(
                    "No population data available for country: "
                            + requestedCountry
            );
            return;
        }

        String line = "-".repeat(80);

        System.out.println("\nCountry Population Report");
        System.out.println(line);
        System.out.printf(
                "| %-50s | %20s |%n",
                "Country", "Total Population"
        );
        System.out.println(line);
        System.out.printf(
                Locale.US,
                "| %-50.50s | %,20d |%n",
                pop.getName(),
                pop.getTotal()
        );
        System.out.println(line);
    }

    /**
     * Displays the report for all countries.
     */
    public void displayAll(List<Population> pops) {

        if (pops == null || pops.isEmpty()) {
            System.out.println("No population data available.");
            return;
        }

        String line = "-".repeat(80);
        int count = 0;

        System.out.println("\nAll Countries Population Report");
        System.out.println(line);
        System.out.printf(
                "| %-50s | %20s |%n",
                "Country", "Total Population"
        );
        System.out.println(line);

        for (Population pop : pops) {
            count++;
            System.out.printf(
                    Locale.US,
                    "| %-50.50s | %,20d |%n",
                    pop.getName(),
                    pop.getTotal()
            );
        }

        System.out.println(line);
        System.out.println("Countries displayed: " + count);
    }

    /**
     * Retrieves and displays the report.
     * Blank country name -> all countries.
     * Otherwise -> that single country.
     */
    public void run(String countryName) {

        try {
            if (countryName == null || countryName.isBlank()) {
                displayAll(getAllCountryPopulations());
                return;
            }

            Population pop = getCountryPopulation(countryName);
            display(pop, countryName.trim());

        } catch (SQLException e) {
            System.err.println(
                    "Unable to retrieve country population due to a database error."
            );
            System.err.println("Details: " + e.getMessage());
        }
    }
}
