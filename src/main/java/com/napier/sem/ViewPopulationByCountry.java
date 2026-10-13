
package com.napier.sem;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Locale;

public class ViewPopulationByCountry
{
    /**
     * US25 - Retrieve the total population, city population,
     * and non-city population data for every country.
     */
    public static ArrayList<Population> getPopulationByCountry(
            Connection con)
    {
        if (con == null)
        {
            System.err.println("Database connection is unavailable.");
            return null;
        }

        ArrayList<Population> populations = new ArrayList<>();

        String sql =
                "SELECT c.Name AS country_name, " +
                        "c.Population AS total_population, " +
                        "COALESCE(SUM(ci.Population), 0) AS city_population " +
                        "FROM country c " +
                        "LEFT JOIN city ci ON ci.CountryCode = c.Code " +
                        "GROUP BY c.Code, c.Name, c.Population " +
                        "ORDER BY c.Population DESC, c.Name ASC";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {
            while (rs.next())
            {
                long total = rs.getLong("total_population");
                long city = rs.getLong("city_population");

                if (rs.wasNull())
                {
                    city = 0;
                }

                // Prevent negative non-city population.
                city = Math.max(0, Math.min(city, total));

                Population population = new Population();
                population.setName(rs.getString("country_name"));
                population.setTotal(total);
                population.setCityPopulation(city);

                populations.add(population);
            }

            return populations;
        }
        catch (SQLException e)
        {
            System.err.println(
                    "Error retrieving country population data: "
                            + e.getMessage());
            return null;
        }
    }

    /**
     * Display the US25 population report in aligned columns.
     */
    public static void printReport(
            ArrayList<Population> populations)
    {
        System.out.println("\nUS25 - View Population by Country");

        if (populations == null || populations.isEmpty())
        {
            System.out.println(
                    "Population data is unavailable or no records were found.");
            return;
        }

        String format =
                "| %-25.25s | %18s | %18s | %10s | %18s | %10s |%n";

        String line = "-".repeat(117);

        System.out.println(line);

        System.out.printf(
                format,
                "Country Name",
                "Total Population",
                "City Population",
                "City %",
                "Non-City Population",
                "Non-City %"
        );

        System.out.println(line);

        for (Population p : populations)
        {
            long total = p.getTotal();
            long city = p.getCityPopulation();
            long nonCity = total - city;

            double cityPercent = 0.0;
            double nonCityPercent = 0.0;

            if (total > 0)
            {
                cityPercent = city * 100.0 / total;
                nonCityPercent = nonCity * 100.0 / total;
            }

            System.out.printf(
                    Locale.US,
                    "| %-25.25s | %,18d | %,18d | %9.2f%% | %,18d | %9.2f%% |%n",
                    p.getName(),
                    total,
                    city,
                    cityPercent,
                    nonCity,
                    nonCityPercent
            );
        }

        System.out.println(line);
        System.out.println("Total countries displayed: "
                + populations.size());
    }
}
