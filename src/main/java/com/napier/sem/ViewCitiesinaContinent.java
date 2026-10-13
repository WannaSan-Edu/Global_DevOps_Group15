package com.napier.sem;

import java.sql.*;

public class ViewCitiesinaContinent {

    private Connection con;

    public ViewCitiesinaContinent(Connection con) {
        this.con = con;
    }

    public void getCitiesInContinent(String continent) {
        String sql = "SELECT city.Name AS CityName, "
                + "country.Name AS CountryName, "
                + "city.District, city.Population "
                + "FROM city "
                + "JOIN country ON city.CountryCode = country.Code "
                + "WHERE country.Continent = ? "
                + "ORDER BY city.Population DESC";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, continent);

            try (ResultSet rs = stmt.executeQuery()) {
                String line = "|---------------------------------------------------------------------------------|";

                System.out.println("\nUS08 - View all cities in a continent: " + continent);
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
            System.out.println("Error running US08 report: " + e.getMessage());
        }
    }
}
