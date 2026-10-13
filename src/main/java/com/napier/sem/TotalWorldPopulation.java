
package com.napier.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * US26 - View Total World Population.
 *
 * Calculates and displays the total population of the world
 * by adding the population of all countries in the database.
 */
public class TotalWorldPopulation
{
    /**
     * Retrieves and prints the total world population.
     *
     * @param con an active database connection
     */
    public static void printReport(Connection con)
    {
        if (con == null)
        {
            System.out.println("Unable to generate report: database connection is unavailable.");
            return;
        }

        String sql =
                "SELECT COUNT(*) AS total_countries, " +
                        "COUNT(Population) AS available_populations, " +
                        "SUM(Population) AS total_population " +
                        "FROM country";

        try (PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery())
        {
            if (rs.next())
            {
                long totalCountries = rs.getLong("total_countries");
                long availablePopulations = rs.getLong("available_populations");

                // No country records or no available population data.
                if (totalCountries == 0 || availablePopulations == 0)
                {
                    System.out.println(
                            "Total World Population Report: Population data is unavailable.");
                    return;
                }

                // Do not report an incomplete total as the world population.
                if (availablePopulations != totalCountries)
                {
                    System.out.println(
                            "Total World Population Report: Population data is incomplete. " +
                                    "A reliable world population total cannot be calculated.");
                    return;
                }

                long totalPopulation = rs.getLong("total_population");

                if (rs.wasNull())
                {
                    System.out.println(
                            "Total World Population Report: Population data is unavailable.");
                    return;
                }

                NumberFormat formatter =
                        NumberFormat.getIntegerInstance(Locale.US);

                System.out.println();
                System.out.println("========================================");
                System.out.println("      TOTAL WORLD POPULATION REPORT");
                System.out.println("========================================");
                System.out.println(
                        "Total World Population: " +
                                formatter.format(totalPopulation));
                System.out.println("Countries included: " + totalCountries);
                System.out.println("========================================");
                System.out.println();
            }
            else
            {
                System.out.println(
                        "Total World Population Report: Population data is unavailable.");
            }
        }
        catch (SQLException e)
        {
            System.out.println(
                    "Unable to generate Total World Population Report: " +
                            e.getMessage());
        }
    }
}
