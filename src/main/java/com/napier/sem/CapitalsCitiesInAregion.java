package com.napier.sem;

import java.sql.*;
import java.util.ArrayList;
import java.util.Locale;

/**
 * US19: View all the capital cities in a region
 * ordered by population from largest to smallest.
 */
public class CapitalsCitiesInAregion
{
    public static ArrayList<CapitalCity> getCapitalCitiesByRegion(Connection connection, String region)
    {
        /*
         * JUSTIFICATION (SQL):
         * - Filters capital cities by region and sorts by population descending.
         */
        ArrayList<CapitalCity> capitals = new ArrayList<>();

        String sql = "SELECT city.Name AS CapitalName, " +
                "country.Name AS CountryName, " +
                "city.Population AS Population " +
                "FROM country INNER JOIN city ON country.Capital = city.ID " +
                "WHERE country.Region = ? " +
                "ORDER BY city.Population DESC, city.Name ASC, country.Name ASC";

        try (PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setString(1, region);

            try (ResultSet results = statement.executeQuery())
            {
                while (results.next())
                {
                    CapitalCity city = new CapitalCity();
                    city.setName(results.getString("CapitalName"));
                    city.setCountry(results.getString("CountryName"));
                    city.setPopulation(results.getLong("Population"));
                    capitals.add(city);
                }
            }
        }
        catch (SQLException e)
        {
            System.out.println("Failed to get capital cities for region: " + region);
            e.printStackTrace();
            return null;
        }

        return capitals;
    }

    public static void printReport(ArrayList<CapitalCity> capitals, String region)
    {
        System.out.println("\nUS19: All Capital Cities in " + region + " by population");

        if (capitals == null || capitals.isEmpty())
        {
            System.out.println("No data available");
            return;
        }

        /*
         * JUSTIFICATION (Table Formatting):
         * - 110-character border line with strict padding and Locale.US formatting.
         */
        String line = "-".repeat(110);
        int count = 0;

        System.out.println(line);
        System.out.printf(
                "| %-45s | %-45s | %12s |%n",
                "Capital City", "Country", "Population"
        );
        System.out.println(line);

        for (CapitalCity city : capitals)
        {
            count++;
            System.out.printf(
                    Locale.US,
                    "| %-45.45s | %-45.45s | %,12d |%n",
                    city.getName(),
                    city.getCountry(),
                    city.getPopulation()
            );
        }

        System.out.println(line);
        System.out.println("Capitals displayed: " + count);
    }
}
