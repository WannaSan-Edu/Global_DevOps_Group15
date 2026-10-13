package com.napier.sem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class App {

    private static final int MAX_ATTEMPTS = 100;
    private static final long RETRY_DELAY_MS = 3000;

    public static void main(String[] args) {

        // Load MySQL JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: MySQL JDBC driver not found.");
            return;
        }

        // Database configuration
        String dbHost = System.getenv().getOrDefault(
                "DB_HOST", "localhost:3306"   // <-- Use 3306
        );

        String dbUrl = System.getenv("DB_URL");

        if (dbUrl == null || dbUrl.isBlank()) {
            dbUrl = "jdbc:mysql://" + dbHost + "/world"
                    + "?useSSL=false"
                    + "&allowPublicKeyRetrieval=true"
                    + "&connectTimeout=5000"
                    + "&socketTimeout=30000";
        }

        String dbUser = System.getenv().getOrDefault(
                "DB_USER", "root"
        );

        String dbPassword = System.getenv().getOrDefault(
                "DB_PASSWORD", "example"
        );

        System.out.println("Starting US02 report...");
        System.out.println("Database URL: " + dbUrl);

        // Connect to MySQL
        Connection con = connectToDatabase(dbUrl, dbUser, dbPassword);

        if (con == null) {
            System.err.println("ERROR: Could not connect to MySQL.");
            return;
        }

        try (Connection connection = con) {

            System.out.println(
                    "Database connection established successfully."
            );

            // Run US02 only
            System.out.println(
                    "\n========================================"
            );
            System.out.println(
                    "US02 - Countries Grouped by Continent"
            );
            System.out.println(
                    "========================================"
            );

            // Call the report class (renamed to PascalCase)
            US02_view_all_countries_in_a_continent.printReport(connection);

        } catch (SQLException e) {
            System.err.println(
                    "Database error: " + e.getMessage()
            );
        }
    }

    private static Connection connectToDatabase(
            String dbUrl,
            String dbUser,
            String dbPassword) {

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {

            System.out.println(
                    "Connecting to MySQL (attempt "
                            + attempt + "/" + MAX_ATTEMPTS + ")..."
            );

            try {
                Connection connection =
                        DriverManager.getConnection(
                                dbUrl, dbUser, dbPassword
                        );

                System.out.println("Connected to MySQL.");
                return connection;

            } catch (SQLException e) {
                System.err.println(
                        "Connection failed: " + e.getMessage()
                );

                if (attempt == MAX_ATTEMPTS) {
                    break;
                }

                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    System.err.println("Connection retries interrupted.");
                    return null;
                }
            }
        }

        return null;
    }
}