package com.napier.sem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class US02_view_all_countries_in_a_continent {

    static class Country {
        String name;
        String continent;
        String region;
        long population;

        Country(String name, String continent,
                String region, long population) {
            this.name = name;
            this.continent = continent;
            this.region = region;
            this.population = population;
        }
    }

    public static void main(String[] args) {

        List<Country> countries = new ArrayList<>();

        // Sample country data
        countries.add(new Country(
                "China", "Asia", "Eastern Asia", 1400000000L));
        countries.add(new Country(
                "India", "Asia", "Southern and Central Asia", 1380000000L));
        countries.add(new Country(
                "Indonesia", "Asia", "Southeast Asia", 273000000L));
        countries.add(new Country(
                "Myanmar", "Asia", "Southeast Asia", 54000000L));

        countries.add(new Country(
                "United States", "North America",
                "North America", 331000000L));
        countries.add(new Country(
                "Canada", "North America",
                "North America", 38000000L));

        countries.add(new Country(
                "Brazil", "South America",
                "South America", 212000000L));
        countries.add(new Country(
                "Argentina", "South America",
                "South America", 45000000L));

        countries.add(new Country(
                "United Kingdom", "Europe",
                "British Islands", 67000000L));
        countries.add(new Country(
                "Germany", "Europe",
                "Western Europe", 83000000L));

        countries.add(new Country(
                "Nigeria", "Africa",
                "Western Africa", 206000000L));
        countries.add(new Country(
                "Egypt", "Africa",
                "Northern Africa", 102000000L));

        countries.add(new Country(
                "Australia", "Oceania",
                "Australia and New Zealand", 25600000L));
        countries.add(new Country(
                "New Zealand", "Oceania",
                "Australia and New Zealand", 5000000L));

        // Group countries by continent
        Map<String, List<Country>> continentMap = new TreeMap<>();

        for (Country country : countries) {
            continentMap.computeIfAbsent(
                    country.continent,
                    key -> new ArrayList<>()
            ).add(country);
        }

        // Sort countries within each continent
        for (List<Country> continentCountries : continentMap.values()) {
            continentCountries.sort(
                    Comparator.comparingLong(
                            (Country country) -> country.population
                    ).reversed()
            );
        }

        // Display countries for every continent
        String line =
                "|---------------------------------------------------------------------------------|";

        for (Map.Entry<String, List<Country>> entry
                : continentMap.entrySet()) {

            System.out.println();
            System.out.println("Continent: " + entry.getKey());
            System.out.println(line);

            System.out.printf(
                    "| %-20s | %-18s | %-26s | %15s |%n",
                    "Country Name",
                    "Continent",
                    "Region",
                    "Population"
            );

            System.out.println(line);

            for (Country country : entry.getValue()) {
                System.out.printf(
                        "| %-20s | %-18s | %-26s | %,15d |%n",
                        country.name,
                        country.continent,
                        country.region,
                        country.population
                );
            }

            System.out.println(line);
        }

        if (countries.isEmpty()) {
            System.out.println("No country data is available.");
        }
    }
}