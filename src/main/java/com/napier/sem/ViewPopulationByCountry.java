package com.napier.sem;

import java.sql.*;
import java.util.ArrayList;

/**
 * US25: Population of each country, split into city and non-city residents.
 */
public class ViewPopulationByCountry
{
    /**
     * Retrieves population data by country from the database.
     *
     * @param con The active database connection
     * @return List of Population objects, or null if connection is invalid
     */
    public static ArrayList<Population> getPopulationByCountry(Connection con)
    {
        if (con == null)
        {
            return null;
        }

        ArrayList<Population> populations = new ArrayList<>();
        String sql = "SELECT country.Name AS name, "
                + "country.Population AS total, "
                + "COALESCE(SUM(city.Population), 0) AS city_population "
                + "FROM country "
                + "LEFT JOIN city ON city.CountryCode = country.Code "
                + "GROUP BY country.Code, country.Name, country.Population "
                + "ORDER BY country.Population DESC";

        try (Statement stmt = con.createStatement();
             ResultSet rset = stmt.executeQuery(sql))
        {
            while (rset.next())
            {
                long total = rset.getLong("total");
                long cityPopulation = rset.getLong("city_population");
                if (cityPopulation > total)
                {
                    cityPopulation = total;
                }

                Population pop = new Population();
                pop.setName(rset.getString("name"));
                pop.setTotal(total);
                pop.setCityPopulation(cityPopulation);

                populations.add(pop);
            }
        }
        catch (SQLException e)
        {
            System.out.println("Failed to get population by country.");
            e.printStackTrace();
            return null;
        }

        return populations;
    }

    /**
     * Prints the population report split into city and non-city residents.
     *
     * @param populations List of population data rows
     */
    public static void printReport(ArrayList<Population> populations)
    {
        System.out.println("\nUS25: Population of each country, split into city and non-city residents");

        if (populations == null)
        {
            System.out.println("No data available");
            return;
        }

        System.out.printf("%-35s %-20s %-20s %-20s%n",
                "Country", "Total Population", "City Population", "Non-City Population");

        for (Population p : populations)
        {
            long total = p.getTotal();
            long cityPop = p.getCityPopulation();
            long nonCityPop = total - cityPop;

            System.out.printf("%-35s %-20d %-20d %-20d%n",
                    p.getName(),
                    total,
                    cityPop,
                    nonCityPop);
        }
    }
}