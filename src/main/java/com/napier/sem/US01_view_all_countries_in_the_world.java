
package com.napier.sem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class US01_view_all_countries_in_the_world {

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
        countries.add(new Country("China", "Asia", "Eastern Asia", 1400000000L));
        countries.add(new Country("India", "Asia", "Southern and Central Asia", 1380000000L));
        countries.add(new Country("United States", "North America", "North America", 331000000L));
        countries.add(new Country("Indonesia", "Asia", "Southeast Asia", 273000000L));
        countries.add(new Country("Brazil", "South America", "South America", 212000000L));
        countries.add(new Country("United Kingdom", "Europe", "British Islands", 67000000L));
        countries.add(new Country("Myanmar", "Asia", "Southeast Asia", 54000000L));

        // Sort by population, highest first
        countries.sort(
                Comparator.comparingLong(
                        (Country country) -> country.population
                ).reversed()
        );

        // Display table
        String line = "|---------------------------------------------------------------------------------|";

        System.out.println(line);
        System.out.printf("| %-15s | %-14s | %-26s | %15s |%n",
                "Country Name", "Continent", "Region", "Population");
        System.out.println(line);

        for (Country country : countries) {
            System.out.printf("| %-15s | %-14s | %-26s | %,15d |%n",
                    country.name,
                    country.continent,
                    country.region,
                    country.population);
        }

        System.out.println(line);

        if (countries.isEmpty()) {
            System.out.println("No country data is available.");
        }
    }
}