
package com.napier.sem;

import java.sql.*;

public class ViewAllCitiesInTheWorld {

    private Connection con;

    public ViewAllCitiesInTheWorld(Connection con) {
        this.con = con;
    }

    public void getAllCitiesInWorld() {
        String sql = "SELECT city.Name AS CityName, "
                + "country.Name AS CountryName, "
                + "city.District, city.Population "
                + "FROM city "
                + "JOIN country ON city.CountryCode = country.Code "
                + "ORDER BY city.Population DESC";

        try (PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            String line = "|---------------------------------------------------------------------------------|";

            System.out.println("\nUS07 - View all cities in the world");
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

        } catch (SQLException e) {
            System.out.println("Error running US07 report: " + e.getMessage());
        }
    }
}
