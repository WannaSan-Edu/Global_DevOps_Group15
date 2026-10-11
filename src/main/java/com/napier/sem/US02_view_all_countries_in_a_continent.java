package com.napier.sem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

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
                "United States", "North America", "North America", 331000000L));
        countries.add(new Country(
                "Indonesia", "Asia", "Southeast Asia", 273000000L));
        countries.add(new Country(
                "Brazil", "South America", "South America", 212000000L));
        countries.add(new Country(
                "United Kingdom", "Europe", "British Islands", 67000000L));
        countries.add(new Country(
                "Myanmar", "Asia", "Southeast Asia", 54000000L));

        Scanner scanner = new Scanner(System.in);

        // Display available continents
        System.out.println("Available Continents:");
        System.out.println("1. Asia");
        System.out.println("2. Europe");
        System.out.println("3. North America");
        System.out.println("4. South America");
        System.out.println("5. Africa");
        System.out.println("6. Oceania");

        System.out.print("Select a continent: ");
        String selectedContinent = scanner.nextLine().trim();

        // Filter countries by selected continent
        List<Country> selectedCountries = new ArrayList<>();

        for (Country country : countries) {
            if (country.continent.equalsIgnoreCase(selectedContinent)) {
                selectedCountries.add(country);
            }
        }

        // Sort population from largest to smallest
        selectedCountries.sort(
                Comparator.comparingLong(
                        (Country country) -> country.population
                ).reversed()
        );

        // Display results
        String line =
                "|---------------------------------------------------------------------------------|";

        if (selectedCountries.isEmpty()) {
            System.out.println(
                    "No country data is available for continent: "
                            + selectedContinent);
        } else {
            System.out.println();
            System.out.println(
                    "Countries in " + selectedContinent
                            + " sorted by population (largest to smallest)");

            System.out.println(line);

            System.out.printf(
                    "| %-20s | %-15s | %-26s | %15s |%n",
                    "Country Name",
                    "Continent",
                    "Region",
                    "Population"
            );

            System.out.println(line);

            for (Country country : selectedCountries) {
                System.out.printf(
                        "| %-20s | %-15s | %-26s | %,15d |%n",
                        country.name,
                        country.continent,
                        country.region,
                        country.population
                );
            }

            System.out.println(line);
        }

        scanner.close();
    }
}