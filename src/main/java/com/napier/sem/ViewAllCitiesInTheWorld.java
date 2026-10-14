package com.napier.sem;

import java.sql.*;
import java.util.Locale;

/**
 * US07 - View all cities in the world ordered by population largest to smallest.
 */
public class ViewAllCitiesInTheWorld {

    private final Connection con;

    public ViewAllCitiesInTheWorld(Connection con) {
        this.con = con;
    }

    public void getAllCitiesInWorld() {
        /*
         * JUSTIFICATION (SQL):
         * - Joins city and country tables to retrieve city name, country name, district, and population.
         * - ORDER BY city.Population DESC satisfies the core US07 ordering requirement.
         */
        String sql = "SELECT city.Name AS CityName, "
                + "country.Name AS CountryName, "
                + "city.District, city.Population "
                + "FROM city "
                + "JOIN country ON city.CountryCode = country.Code "
                + "ORDER BY city.Population DESC";

        try (PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            /*
             * JUSTIFICATION (Table Formatting):
             * - 126-character border width aligns precisely with the headers and rows.
             */
            String line = "-".repeat(126);
            int count = 0;

            System.out.println("\nUS07 - View all cities in the world");
            System.out.println(line);
            System.out.printf(
                    "| %-35s | %-32s | %-25s | %15s |%n",
                    "City Name", "Country", "District", "Population"
            );
            System.out.println(line);

            while (rs.next()) {
                count++;
                /*
                 * JUSTIFICATION (Formatting & Truncation):
                 * - Truncation bounds (e.g., %-35.35s) prevent long city or country names from breaking borders.
                 * - Locale.US ensures comma separators for population.
                 */
                System.out.printf(
                        Locale.US,
                        "| %-35.35s | %-32.32s | %-25.25s | %,15d |%n",
                        rs.getString("CityName"),
                        rs.getString("CountryName"),
                        rs.getString("District") == null ? "" : rs.getString("District"),
                        rs.getLong("Population")
                );
            }

            System.out.println(line);
            if (count == 0) {
                System.out.println("No city records found.");
            } else {
                System.out.println("Cities displayed: " + count);
            }

        } catch (SQLException e) {
            System.out.println("Error running US07 report: " + e.getMessage());
        }
    }
}
