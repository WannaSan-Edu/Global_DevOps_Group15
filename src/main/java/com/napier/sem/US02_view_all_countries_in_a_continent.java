package com.napier.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * US02 - View all countries in a continent, organised by continent,
 * with countries within each continent listed in descending order
 * of population.
 *
 * JUSTIFICATION (class level):
 *  - This class is dedicated to a single report (US02), following the
 *    Single Responsibility Principle.
 *  - Method is static because the report takes a Connection as its
 *    only dependency - no instance state is needed.
 *  - PascalCase class name matches the file name, as required by Java.
 */
public class US02_view_all_countries_in_a_continent {

    public static void printReport(Connection connection) {

        /*
         * JUSTIFICATION (SQL):
         *  - SELECT only the 4 columns the report needs (Name, Continent,
         *    Region, Population). Selecting fewer columns than SELECT *
         *    is faster and more explicit.
         *  - `country` is wrapped in backticks so that:
         *      (a) any reserved-word ambiguity is avoided, and
         *      (b) Linux MySQL's case-sensitive table naming is respected.
         *  - ORDER BY Continent ASC     -> groups rows by continent
         *                                  (Africa, Asia, Europe, ...).
         *  - ORDER BY Population DESC   -> within each continent, sorts
         *                                  countries from largest to
         *                                  smallest population.
         *    Together these two keys are the CORE of US02's requirement.
         *  - The entire ordering is done in SQL, so Java needs no sorting.
         */
        String sql =
                "SELECT Name, Continent, Region, Population " +
                        "FROM `country` " +
                        "ORDER BY Continent ASC, Population DESC";

        /*
         * JUSTIFICATION (separator line):
         *  - A single reusable String keeps all rows visually aligned
         *    and avoids repeated string literals.
         *  - Length chosen to fit the header format below exactly.
         */
        String line =
                "----------------------------------------------------------------------------------------------";

        /*
         * JUSTIFICATION (try-with-resources):
         *  - PreparedStatement and ResultSet are auto-closed, preventing
         *    resource leaks even if an SQLException is thrown.
         *  - PreparedStatement is used (rather than Statement) to follow
         *    best practice for JDBC and to allow future parameterisation.
         */
        try (PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {

            /*
             * JUSTIFICATION (hasCountries flag):
             *  - Tracks whether any rows were printed at all.
             *  - If false at the end, we print a friendly "no data"
             *    message instead of an empty table.
             */
            boolean hasCountries = false;

            /*
             * JUSTIFICATION (currentContinent marker):
             *  - Stores the last continent that was printed.
             *  - Because SQL guarantees rows arrive grouped by continent,
             *    we only need to compare each row's continent with the
             *    previous one - a single O(1) check per row.
             *  - This lets us stream the ResultSet without buffering.
             */
            String currentContinent = null;

            /*
             * JUSTIFICATION (single streaming loop):
             *  - We iterate the ResultSet exactly once. Combined with the
             *    O(1) change-detection above, the whole report is O(n)
             *    in the number of countries - scalable to any size.
             */
            while (results.next()) {

                String continent = results.getString("Continent");

                /*
                 * JUSTIFICATION (group boundary detection):
                 *  - When the continent changes, we are at the start of a
                 *    new group, so we:
                 *      1. close the previous group (separator + blank line),
                 *      2. print the new continent heading,
                 *      3. print a fresh column header for the new group.
                 *  - This directly produces the "grouped by continent"
                 *    layout US02 requires.
                 */
                if (!continent.equals(currentContinent)) {

                    // Close the previous continent block (if any)
                    if (currentContinent != null) {
                        System.out.println(line);
                        System.out.println();
                    }

                    currentContinent = continent;

                    /*
                     * JUSTIFICATION (heading):
                     *  - "Continent: <name>" makes the group immediately
                     *    visible to the reader.
                     */
                    System.out.println("Continent: " + currentContinent);
                    System.out.println(line);

                    /*
                     * JUSTIFICATION (column header):
                     *  - Repeated per continent so a reader landing mid-
                     *    report still knows what each column means.
                     *  - Format spec breakdown:
                     *      %-20s  -> left-aligned text, width 20 (Country Name)
                     *      %-16s  -> left-aligned text, width 16 (Continent)
                     *      %-30s  -> left-aligned text, width 30 (Region)
                     *      %15s   -> right-aligned text, width 15 (Population
                     *                header) so it lines up with the numbers
                     *                below.
                     *      %n     -> platform-independent newline.
                     *  - Text columns are left-aligned and the numeric
                     *    column is right-aligned: the universal convention
                     *    for readable data tables.
                     */
                    System.out.printf(
                            "| %-20s | %-16s | %-30s | %15s |%n",
                            "Country Name",
                            "Continent",
                            "Region",
                            "Population"
                    );

                    System.out.println(line);
                }

                /*
                 * JUSTIFICATION (hasCountries flag set):
                 *  - Set as soon as at least one data row exists, so the
                 *    final "no data" branch is skipped when we do have
                 *    rows.
                 */
                hasCountries = true;

                /*
                 * JUSTIFICATION (data row):
                 *  - Same widths as the header so every column lines up.
                 *  - %,15d right-aligns the population and inserts
                 *    thousands separators (e.g. 1,277,558,000) for
                 *    readability of large numbers.
                 *  - Continent is reprinted on each row so each line is
                 *    self-contained and easy to copy/paste or export.
                 */
                System.out.printf(
                        "| %-20s | %-16s | %-30s | %,15d |%n",
                        results.getString("Name"),
                        continent,
                        results.getString("Region"),
                        results.getLong("Population")
                );
            }

            /*
             * JUSTIFICATION (table closing / empty case):
             *  - If we printed at least one row, we close the table with
             *    a final separator line.
             *  - If we printed nothing, we print a clear "no data"
             *    message instead of an empty, confusing table.
             */
            if (hasCountries) {
                System.out.println(line);
            } else {
                System.out.println("No country data is available.");
            }

        } catch (SQLException e) {
            /*
             * JUSTIFICATION (error handling):
             *  - Any JDBC failure is caught and reported with a clear
             *    prefix, so the operator knows the report (not the DB
             *    connection) failed.
             *  - We do NOT rethrow here because the report is a
             *    best-effort display; App.java already handles
             *    connection-level errors.
             */
            System.err.println(
                    "Error retrieving countries by continent: "
                            + e.getMessage()
            );
        }
    }
}
