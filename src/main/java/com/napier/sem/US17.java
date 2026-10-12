//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.napier.sem;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import java.io.PrintStream;

public class US17 {
    public US17() {
    }

    public List<CapitalCity> getAllCapitalCities(Connection connection) throws SQLException {
        String sql = "SELECT city.Name AS CapitalName, country.Name AS CountryName, city.Population FROM country INNER JOIN city ON country.Capital = city.ID ORDER BY city.Population DESC, city.Name ASC, country.Name ASC";
        List<CapitalCity> capitals = new ArrayList();

        try (
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet results = statement.executeQuery();
        ) {
            while(results.next()) {
                CapitalCity city = new CapitalCity();
                city.setName(results.getString("CapitalName"));
                city.setCountry(results.getString("CountryName"));
                city.setPopulation(results.getLong("Population"));
                capitals.add(city);
            }
        }

        return capitals;
    }

    public void printReport(List<CapitalCity> capitals, PrintStream output) {
        this.printReport(capitals, output, "All capital cities in the world by population (largest to smallest)");
    }

    public void printReport(List<CapitalCity> capitals, PrintStream output, String title) {
        output.println(title);
        if (capitals.isEmpty()) {
            output.println("No capital cities found.");
        } else {
            int nameWidth = "Capital City".length();
            int countryWidth = "Country".length();

            for(CapitalCity city : capitals) {
                nameWidth = Math.max(nameWidth, city.getName().length());
                countryWidth = Math.max(countryWidth, city.getCountry().length());
            }

            String format = "%-" + nameWidth + "s  %-" + countryWidth + "s  %12s%n";
            output.printf(format, "Capital City", "Country", "Population");

            for(CapitalCity city : capitals) {
                output.printf(format, city.getName(), city.getCountry(), city.getPopulation());
            }

        }
    }
}

