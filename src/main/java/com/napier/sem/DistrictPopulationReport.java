package com.napier.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DistrictPopulationReport {

    private final Connection con;

    public DistrictPopulationReport(Connection con) {
        if (con == null) {
            throw new IllegalArgumentException(
                    "Database connection cannot be null."
            );
        }
        this.con = con;
    }

    /**
     * Retrieves the total population of a selected district.
     *
     * @param districtName the selected district
     * @return a Population object, or null if no population data exists
     * @throws SQLException if a database error occurs
     */
    public Population getDistrictPopulation(String districtName)
            throws SQLException {

        if (districtName == null || districtName.isBlank()) {
            return null;
        }

        String sql =
                "SELECT District, SUM(Population) AS TotalPopulation "
                        + "FROM city "
                        + "WHERE District = ? AND Population IS NOT NULL "
                        + "GROUP BY District";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, districtName.trim());

            try (ResultSet rset = pstmt.executeQuery()) {

                if (!rset.next()) {
                    return null;
                }

                long total = rset.getLong("TotalPopulation");

                Population pop = new Population();
                pop.setName(rset.getString("District"));
                pop.setTotal(total);

                return pop;
            }
        }
    }

    /**
     * Retrieves all districts with population data,
     * ordered from largest to smallest population.
     */
    public List<Population> getAllDistrictPopulations()
            throws SQLException {

        String sql =
                "SELECT District, SUM(Population) AS TotalPopulation "
                        + "FROM city "
                        + "WHERE District IS NOT NULL "
                        + "AND Population IS NOT NULL "
                        + "GROUP BY District "
                        + "ORDER BY TotalPopulation DESC";

        List<Population> list = new ArrayList<>();

        try (PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rset = pstmt.executeQuery()) {

            while (rset.next()) {
                Population pop = new Population();
                pop.setName(rset.getString("District"));
                pop.setTotal(rset.getLong("TotalPopulation"));
                list.add(pop);
            }
        }

        return list;
    }

    /**
     * Displays the report for a selected district.
     */
    public void display(Population pop, String requestedDistrict) {

        if (pop == null) {
            System.out.println(
                    "No population data available for district: "
                            + requestedDistrict
            );
            return;
        }

        System.out.println("\nDistrict Population Report");
        System.out.println("==========================");

        System.out.printf("%-30s %20s%n",
                "District", "Total Population");

        System.out.printf("%-30s %,20d%n",
                pop.getName(), pop.getTotal());
    }

    /**
     * Displays population totals for all districts.
     */
    public void displayAll(List<Population> pops) {

        if (pops == null || pops.isEmpty()) {
            System.out.println("No district population data available.");
            return;
        }

        System.out.println("\nAll Districts Population Report");
        System.out.println("===============================");

        System.out.printf("%-45s %20s%n",
                "District", "Total Population");

        for (Population pop : pops) {
            System.out.printf("%-45s %,20d%n",
                    pop.getName(), pop.getTotal());
        }
    }

    /**
     * Blank district name -> display all districts.
     * Otherwise -> display the selected district.
     */
    public void run(String districtName) {

        try {
            if (districtName == null || districtName.isBlank()) {
                displayAll(getAllDistrictPopulations());
                return;
            }

            Population pop = getDistrictPopulation(districtName);
            display(pop, districtName.trim());

        } catch (SQLException e) {
            System.err.println(
                    "Unable to retrieve district population "
                            + "due to a database error."
            );
            System.err.println("Details: " + e.getMessage());
        }
    }
}