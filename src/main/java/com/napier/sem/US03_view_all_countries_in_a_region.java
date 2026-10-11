package com.napier.sem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class US03_view_all_countries_in_a_region {

    static class Country {
        String code;
        String name;
        String continent;
        String region;
        long population;
        String capital;

        Country(String code, String name, String continent,
                String region, long population, String capital) {
            this.code = code;
            this.name = name;
            this.continent = continent;
            this.region = region;
            this.population = population;
            this.capital = capital;
        }
    }

    public static void main(String[] args) {

        List<Country> countries = new ArrayList<>();

        // Sample country data
        countries.add(new Country(
                "CHN", "China", "Asia",
                "Eastern Asia", 1400000000L, "Peking"));

        countries.add(new Country(
                "JPN", "Japan", "Asia",
                "Eastern Asia", 125000000L, "Tokyo"));

        countries.add(new Country(
                "MNG", "Mongolia", "Asia",
                "Eastern Asia", 3400000L, "Ulaanbaatar"));

        countries.add(new Country(
                "IND", "India", "Asia",
                "Southern and Central Asia", 1380000000L, "New Delhi"));

        countries.add(new Country(
                "PAK", "Pakistan", "Asia",
                "Southern and Central Asia", 220000000L, "Islamabad"));

        countries.add(new Country(
                "MMR", "Myanmar", "Asia",
                "Southeast Asia", 54000000L, "Rangoon"));

        countries.add(new Country(
                "IDN", "Indonesia", "Asia",
                "Southeast Asia", 273000000L, "Jakarta"));

        countries.add(new Country(
                "THA", "Thailand", "Asia",
                "Southeast Asia", 70000000L, "Bangkok"));

        Scanner scanner = new Scanner(System.in);

        // Display available regions
        System.out.println("Available Regions:");
        System.out.println("1. Eastern Asia");
        System.out.println("2. Southern and Central Asia");
        System.out.println("3. Southeast Asia");

        System.out.print("Enter a region: ");
        String selectedRegion = scanner.nextLine().trim();

        // Filter countries by selected region
        List<Country> selectedCountries = new ArrayList<>();

        for (Country country : countries) {
            if (country.region.equalsIgnoreCase(selectedRegion)) {
                selectedCountries.add(country);
            }
        }

        // Sort by population from largest to smallest
        selectedCountries.sort(
                Comparator.comparingLong(
                        (Country country) -> country.population
                ).reversed()
        );

        // Display results
        String line =
                "|--------------------------------------------------------------------------------------------------------------------|";

        if (selectedCountries.isEmpty()) {
            System.out.println(
                    "No countries found for region: " + selectedRegion);
        } else {
            System.out.println();
            System.out.println(
                    "Countries in " + selectedRegion
                            + " sorted by population (largest to smallest)");

            System.out.println(line);

            System.out.printf(
                    "| %-8s | %-18s | %-12s | %-28s | %15s | %-18s |%n",
                    "Code",
                    "Name",
                    "Continent",
                    "Region",
                    "Population",
                    "Capital"
            );

            System.out.println(line);

            for (Country country : selectedCountries) {
                System.out.printf(
                        "| %-8s | %-18s | %-12s | %-28s | %,15d | %-18s |%n",
                        country.code,
                        country.name,
                        country.continent,
                        country.region,
                        country.population,
                        country.capital
                );
            }

            System.out.println(line);
        }

        scanner.close();
    }
}