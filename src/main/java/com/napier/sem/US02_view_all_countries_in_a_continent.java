package com.napier.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

/**
 * US02 - View all countries in a continent, organised by continent,
 * with countries within each continent listed in descending order
 * of population.
 */
public class US02_view_all_countries_in_a_continent {

    public static void printReport(Connection connection) {
        String sql =
                "SELECT Name, Continent, Region, Population " +
                        "FROM `country` " +
                        "ORDER BY Continent ASC, Population DESC";

        // Exactly 110 characters to match the table row format length precisely
        String line = "-".repeat(110);

        try (PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {

            boolean hasCountries = false;
            String currentContinent = null;

            while (results.next()) {
                String continent = results.getString("Continent");

                if (!continent.equals(currentContinent)) {
                    // Close the previous continent block (if any)
                    if (currentContinent != null) {
                        System.out.println(line);
                        System.out.println();
                    }

                    currentContinent = continent;
                    System.out.println("Continent: " + currentContinent);
                    System.out.println(line);
                    System.out.printf(
                            "| %-32s | %-20s | %-30s | %15s |%n",
                            "Country Name",
                            "Continent",
                            "Region",
                            "Population"
                    );
                    System.out.println(line);
                }

                hasCountries = true;

                System.out.printf(
                        Locale.US,
                        "| %-32.32s | %-20.20s | %-30.30s | %,15d |%n",
                        results.getString("Name"),
                        continent,
                        results.getString("Region"),
                        results.getLong("Population")
                );
            }

            if (hasCountries) {
                System.out.println(line);
            } else {
                System.out.println("No country data is available.");
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error retrieving countries by continent: "
                            + e.getMessage()
            );
        }
    }
}
