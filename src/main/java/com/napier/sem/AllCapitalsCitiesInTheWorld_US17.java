package com.napier.sem;

import java.sql.*;
import java.util.ArrayList;

/**
 - US17: View all capital cities in the world,
 - ordered by population from largest to smallest.
 */
public class AllCapitalsCitiesInTheWorld_US17
{
    public static ArrayList<CapitalCity> getAll(Connection con)
    {
        ArrayList<CapitalCity> capitalCities = new ArrayList<>();

        String sql =
                "SELECT city.Name AS Name, " +
                        "country.Name AS Country, " +
                        "city.Population AS Population " +
                        "FROM city JOIN country ON city.ID = country.Capital " +
                        "ORDER BY city.Population DESC";

        try (PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rset = stmt.executeQuery())
        {
            while (rset.next())
            {
                CapitalCity capitalCity = new CapitalCity();
                capitalCity.setName(rset.getString("Name"));
                capitalCity.setCountry(rset.getString("Country"));
                capitalCity.setPopulation(rset.getLong("Population"));

                capitalCities.add(capitalCity);
            }
        }
        catch (SQLException e)
        {
            System.out.println("Failed to get all capital cities.");
            e.printStackTrace();
            return null;
        }

        return capitalCities;
    }

    public static void printReport(ArrayList<CapitalCity> capitalCities)
    {
        System.out.println("\nUS17: All Capital Cities in the World");

        if (capitalCities == null)
        {
            System.out.println("No data available");
            return;
        }

        System.out.printf("%-35s %-45s %-12s%n",
                "Name", "Country", "Population");

        for (CapitalCity city : capitalCities)
        {
            System.out.printf("%-35s %-45s %-12d%n",
                    city.getName(),
                    city.getCountry(),
                    city.getPopulation());
        }
    }
}