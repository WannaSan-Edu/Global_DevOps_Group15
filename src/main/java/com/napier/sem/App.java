package com.napier.sem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class App {

    public static void main(String[] args) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Could not load MySQL JDBC driver.");
            return;
        }

        // No argument -> empty string -> show all countries.
        // With argument -> show only that country.
        String countryName =
                args.length > 0 ? String.join(" ", args).trim() : "";

        String host = System.getenv().getOrDefault("DB_HOST", "127.0.0.1:33060");
        String url = "jdbc:mysql://" + host
                + "/world?useSSL=false&allowPublicKeyRetrieval=true";

        String username = "root";
        String password = "example";

        Connection con = null;

        for (int i = 0; i < 100; i++) {
            System.out.println("Connecting to database...");

            try {
                con = DriverManager.getConnection(url, username, password);
                System.out.println("Successfully connected.");
                break;

            } catch (SQLException e) {
                System.err.println(
                        "Database connection attempt " + (i + 1)
                                + " failed: " + e.getMessage()
                );

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    System.err.println("Database connection was interrupted.");
                    return;
                }
            }
        }

        if (con == null) {
            System.err.println(
                    "Could not connect to the database after 100 attempts."
            );
            return;
        }

        try (Connection connection = con) {
            CountryPopulationReport report =
                    new CountryPopulationReport(connection);

            report.run(countryName);

        } catch (SQLException e) {
            System.err.println("Error closing database connection.");
        }
    }
}