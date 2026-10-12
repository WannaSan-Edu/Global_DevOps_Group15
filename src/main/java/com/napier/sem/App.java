package com.napier.sem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;

public class App {

    public static void main(String[] args) {

        // Load MySQL database driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Could not load MySQL JDBC driver.");
            return;
        }

        // Usage: [country|district] [name...]
        // No arguments -> country report for all countries.
        String mode = "country";
        String name = "";

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("district")
                    || args[0].equalsIgnoreCase("country")) {
                mode = args[0].toLowerCase();
                name = String.join(" ",
                        Arrays.copyOfRange(args, 1, args.length)).trim();
            } else {
                name = String.join(" ", args).trim();
            }
        }

        // Use environment variables when provided.
        // Defaults are for running directly from IntelliJ.
        String dbUrl = System.getenv().getOrDefault(
                "DB_URL",
                "jdbc:mysql://localhost:33060/world?useSSL=false&allowPublicKeyRetrieval=true"
        );
        String dbUser = System.getenv().getOrDefault("DB_USER", "root");
        String dbPassword = System.getenv().getOrDefault("DB_PASSWORD", "example");

        System.out.println("Database URL: " + dbUrl);
        System.out.println("Database user: " + dbUser);

        // Connect to the database, retrying while it starts
        Connection con = null;

        for (int i = 0; i < 100; i++) {
            System.out.println("Connecting to database...");

            try {
                con = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
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

        // Run the selected report
        try (Connection connection = con) {

            if (mode.equals("district")) {
                new DistrictPopulationReport(connection).run(name);
            } else {
                new DistrictPopulationReport(connection).run(name);
            }

        } catch (SQLException e) {
            System.err.println("Error running report or closing connection.");
            e.printStackTrace();
        }
    }
}