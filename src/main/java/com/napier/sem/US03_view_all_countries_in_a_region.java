package com.napier.sem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

        countries.add(new Country("CHN", "China", "Asia", "Eastern Asia", 1400000000L, "Peking"));
        countries.add(new Country("JPN", "Japan", "Asia", "Eastern Asia", 125000000L, "Tokyo"));
        countries.add(new Country("MNG", "Mongolia", "Asia", "Eastern Asia", 3400000L, "Ulaanbaatar"));
        countries.add(new Country("IND", "India", "Asia", "Southern and Central Asia", 1380000000L, "New Delhi"));
        countries.add(new Country("PAK", "Pakistan", "Asia", "Southern and Central Asia", 220000000L, "Islamabad"));
        countries.add(new Country("MMR", "Myanmar", "Asia", "Southeast Asia", 54000000L, "Rangoon"));
        countries.add(new Country("IDN", "Indonesia", "Asia", "Southeast Asia", 273000000L, "Jakarta"));
        countries.add(new Country("THA", "Thailand", "Asia", "Southeast Asia", 70000000L, "Bangkok"));
        countries.add(new Country("USA", "United States", "North America", "North America", 331000000L, "Washington"));
        countries.add(new Country("CAN", "Canada", "North America", "North America", 38000000L, "Ottawa"));
        countries.add(new Country("BRA", "Brazil", "South America", "South America", 212000000L, "Brasilia"));
        countries.add(new Country("ARG", "Argentina", "South America", "South America", 45000000L, "Buenos Aires"));
        countries.add(new Country("GBR", "United Kingdom", "Europe", "British Islands", 67000000L, "London"));
        countries.add(new Country("DEU", "Germany", "Europe", "Western Europe", 83000000L, "Berlin"));
        countries.add(new Country("NGA", "Nigeria", "Africa", "Western Africa", 206000000L, "Abuja"));
        countries.add(new Country("EGY", "Egypt", "Africa", "Northern Africa", 102000000L, "Cairo"));
        countries.add(new Country("AUS", "Australia", "Oceania", "Australia and New Zealand", 25600000L, "Canberra"));
        countries.add(new Country("NZL", "New Zealand", "Oceania", "Australia and New Zealand", 5000000L, "Wellington"));

        Map<String, List<Country>> regionMap = new TreeMap<>();

        for (Country country : countries) {
            regionMap.computeIfAbsent(
                    country.region, key -> new ArrayList<>()
            ).add(country);
        }

        for (List<Country> list : regionMap.values()) {
            list.sort(
                    Comparator.comparingLong(
                            (Country country) -> country.population
                    ).reversed()
            );
        }

        String line = "------------------------------------------------------------------------------------------------------------------------------";

        if (countries.isEmpty()) {
            System.out.println("No country data is available.");
            return;
        }

        for (Map.Entry<String, List<Country>> entry : regionMap.entrySet()) {

            System.out.println();
            System.out.println("Region: " + entry.getKey());
            System.out.println(line);

            System.out.printf("| %-6s | %-20s | %-16s | %-30s | %15s | %-20s |%n",
                    "Code", "Name", "Continent", "Region", "Population", "Capital");

            System.out.println(line);

            for (Country country : entry.getValue()) {
                System.out.printf("| %-6s | %-20s | %-16s | %-30s | %,15d | %-20s |%n",
                        country.code,
                        country.name,
                        country.continent,
                        country.region,
                        country.population,
                        country.capital);
            }

            System.out.println(line);
        }
    }
}