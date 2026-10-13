package com.napier.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

/**
 * US03 - View all countries in each region, ordered by population descending.
 */
public class US03_view_all_countries_in_a_region {

    public static void printReport(Connection connection) {
        String sql = "SELECT c.Code, c.Name, c.Continent, c.Region, "
                + "c.Population, capital.Name AS Capital "
                + "FROM country c "
                + "LEFT JOIN city capital ON c.Capital = capital.ID "
                + "ORDER BY c.Region ASC, c.Population DESC, c.Name ASC";

        // Exactly 130 characters to match the table row format length precisely
        String line = "-".repeat(130);
        String currentRegion = null;
        int count = 0;

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                String region = rs.getString("Region");
                if (region == null) {
                    region = "Unknown";
                }

                if (!region.equals(currentRegion)) {
                    if (currentRegion != null) {
                        System.out.println(line);
                        System.out.println();
                    }
                    currentRegion = region;
                    System.out.println("Region: " + currentRegion);
                    System.out.println(line);
                    System.out.printf(
                            "| %-6s | %-24s | %-18s | %-28s | %15s | %-20s |%n",
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
                        "| %-6s | %-24.24s | %-18.18s | %-28.28s | %,15d | %-20.20s |%n",
                        rs.getString("Code"),
                        rs.getString("Name"),
                        rs.getString("Continent"),
                        region,
                        population,
                        capital
                );
                count++;
            }

            if (count > 0) {
                System.out.println(line);
            } else {
                System.out.println("No country records found.");
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving countries by region: " + e.getMessage());
        }
    }
}
