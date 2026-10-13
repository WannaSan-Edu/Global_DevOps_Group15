package com.napier.sem;

import java.sql.*;
import java.util.ArrayList;
import java.util.Locale;

/**
 * US17: View all capital cities in the world,
 * ordered by population from largest to smallest.
 */
public class AllCapitalsCitiesInTheWorld_US17
{
    public static ArrayList<CapitalCity> getAll(Connection con)
    {
        /*
         * JUSTIFICATION (SQL):
         * - Joins city and country on capital ID to fetch capital name, country name, and population.
         * - ORDER BY Population DESC meets the core requirement of US17.
         */
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

        if (capitalCities == null || capitalCities.isEmpty())
        {
            System.out.println("No data available");
            return;
        }

        /*
         * JUSTIFICATION (Table Formatting):
         * - 110-character border line matches the unified project layout.
         * - Left-aligned text columns with truncation and right-aligned Locale.US population numbers.
         */
        String line = "-".repeat(110);
        int count = 0;

        System.out.println(line);
        System.out.printf(
                "| %-45s | %-45s | %12s |%n",
                "Capital City", "Country", "Population"
        );
        System.out.println(line);

        for (CapitalCity city : capitalCities)
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
