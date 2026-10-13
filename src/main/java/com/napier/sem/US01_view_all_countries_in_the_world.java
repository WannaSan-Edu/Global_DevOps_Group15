package com.napier.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

/**
 * US01 - View all the countries in the world organised by largest population to smallest.
 */
public class US01_view_all_countries_in_the_world {

    public static void printReport(Connection connection) {
        String sql = "SELECT Name, Continent, Region, Population " +
                     "FROM `country` " +
                     "ORDER BY Population DESC";

        String line = "-------------------------------------------------------------------------------------------------";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {

            boolean hasCountries = false;

            System.out.println(line);
            System.out.printf("| %-25s | %-16s | %-30s | %15s |%n",
                    "Country Name", "Continent", "Region", "Population");
            System.out.println(line);

            while (results.next()) {
                hasCountries = true;
                System.out.printf(
                        Locale.US,
                        "| %-25s | %-16s | %-30s | %,15d |%n",
                        results.getString("Name"),
                        results.getString("Continent"),
                        results.getString("Region"),
                        results.getLong("Population")
                );
            }

            System.out.println(line);

            if (!hasCountries) {
                System.out.println("No country data is available.");
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving countries of the world: " + e.getMessage());
        }
    }
}
