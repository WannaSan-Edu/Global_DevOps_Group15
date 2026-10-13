
package com.napier.sem;

import java.sql.*;

public class TopCitiesInContinent {

    public static void generateReport(
            Connection con, String continent, int n) {

        if (con == null) {
            System.out.println("Database connection is not available.");
            return;
        }

        if (continent == null || continent.trim().isEmpty() || n <= 0) {
            System.out.println(
                    "Please provide a valid continent and positive N."
            );
            return;
        }

        String sql =
                "SELECT ci.Name AS city_name, " +
                        "co.Name AS country_name, " +
                        "ci.District AS district_name, " +
                        "ci.Population AS city_population " +
                        "FROM city ci " +
                        "JOIN country co ON ci.CountryCode = co.Code " +
                        "WHERE co.Continent = ? " +
                        "ORDER BY ci.Population DESC, ci.Name ASC " +
                        "LIMIT ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, continent.trim());
            ps.setInt(2, n);

            try (ResultSet rs = ps.executeQuery()) {

                System.out.println(
                        "\nTOP " + n +
                                " MOST POPULATED CITIES IN " +
                                continent.toUpperCase()
                );

                System.out.printf(
                        "%-30s | %-25s | %-25s | %15s%n",
                        "Name", "Country", "District", "Population"
                );

                System.out.println("-".repeat(105));

                int count = 0;

                while (rs.next()) {
                    System.out.printf(
                            "%-30s | %-25s | %-25s | %,15d%n",
                            rs.getString("city_name"),
                            rs.getString("country_name"),
                            rs.getString("district_name"),
                            rs.getLong("city_population")
                    );

                    count++;
                }

                if (count == 0) {
                    System.out.println(
                            "No cities found for this continent."
                    );
                } else {
                    System.out.println("\nRows returned: " + count);
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Database/report error: " + e.getMessage()
            );
        }
    }
}
