
package com.napier.sem;

import java.sql.*;

public class ViewAllCitiesinaRegion {

    private Connection con;

    public ViewAllCitiesinaRegion(Connection con) {
        this.con = con;
    }

    public void getCitiesInRegion(String region) {
        String sql = "SELECT city.Name AS CityName, "
                + "country.Name AS CountryName, "
                + "city.District, city.Population "
                + "FROM city "
                + "JOIN country ON city.CountryCode = country.Code "
                + "WHERE country.Region = ? "
                + "ORDER BY city.Population DESC";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, region);

            try (ResultSet rs = stmt.executeQuery()) {
                String line = "|---------------------------------------------------------------------------------|";

                System.out.println("\nUS09 - View all cities in a region: " + region);
                System.out.println(line);
                System.out.printf("| %-18s | %-14s | %-20s | %15s |%n",
                        "City Name", "Country", "District", "Population");
                System.out.println(line);

                while (rs.next()) {
                    System.out.printf("| %-18s | %-14s | %-20s | %,15d |%n",
                            rs.getString("CityName"),
                            rs.getString("CountryName"),
                            rs.getString("District"),
                            rs.getLong("Population"));
                }

                System.out.println(line);
            }

        } catch (SQLException e) {
            System.out.println("Error running US09 report: " + e.getMessage());
        }
    }
}
