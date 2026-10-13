package com.napier.sem;

import java.sql.*;
import java.util.ArrayList;

/**
 - US18: View all the capital cities in a continent
 - ordered by population from largest to smallest.
 */
public class CapitalCitiesInAcontinent
{
    public static ArrayList<CapitalCity> getCapitalCitiesByContinent(Connection connection, String continent)
    {
        ArrayList<CapitalCity> capitals = new ArrayList<>();

        String sql = "SELECT city.Name AS CapitalName, " +
                "country.Name AS CountryName, " +
                "city.Population AS Population " +
                "FROM country INNER JOIN city ON country.Capital = city.ID " +
                "WHERE country.Continent = ? " +
                "ORDER BY city.Population DESC, city.Name ASC, country.Name ASC";

        try (PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setString(1, continent);

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
            System.out.println("Failed to get capital cities for continent: " + continent);
            e.printStackTrace();
            return null;
        }

        return capitals;
    }

    public static void printReport(ArrayList<CapitalCity> capitals, String continent)
    {
        System.out.println("\nUS18: All Capital Cities in " + continent + " by population (largest to smallest)");

        if (capitals == null)
        {
            System.out.println("No data available");
            return;
        }

        System.out.printf("%-35s %-45s %-12s%n",
                "Name", "Country", "Population");

        for (CapitalCity city : capitals)
        {
            System.out.printf("%-35s %-45s %-12d%n",
                    city.getName(),
                    city.getCountry(),
                    city.getPopulation());
        }
    }
}