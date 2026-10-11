
package com.napier.sem;

import java.sql.*;

public class App
{
    public static void main(String[] args)
    {
        // Load MySQL database driver
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Could not load SQL driver");
            e.printStackTrace();
            System.exit(-1);
        }

        // Get district name from command-line arguments
        String districtName =
                args.length > 0 ? String.join(" ", args).trim() : "";

        // Use environment variables when provided.
        // Default URL is for running directly from IntelliJ on Windows.
        String dbUrl = System.getenv().getOrDefault(
                "DB_URL",
                "jdbc:mysql://localhost:33060/world?useSSL=false&allowPublicKeyRetrieval=true"
        );

        String dbUser = System.getenv().getOrDefault(
                "DB_USER",
                "root"
        );

        String dbPassword = System.getenv().getOrDefault(
                "DB_PASSWORD",
                "example"
        );

        System.out.println("Database URL: " + dbUrl);
        System.out.println("Database user: " + dbUser);

        // Connect to the database, retrying while it starts
        Connection con = null;
        int retries = 100;

        for (int i = 0; i < retries; i++)
        {
            System.out.println("Connecting to database...");

            try
            {
                Thread.sleep(1000);

                con = DriverManager.getConnection(
                        dbUrl,
                        dbUser,
                        dbPassword
                );

                System.out.println("Successfully connected");
                break;
            }
            catch (SQLException e)
            {
                System.out.println(
                        "Failed to connect to database attempt "
                                + (i + 1)
                );
                System.out.println(e.getMessage());
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                System.out.println("Database connection interrupted.");
                return;
            }
        }

        if (con == null)
        {
            System.out.println(
                    "Could not connect to the database after "
                            + retries + " attempts."
            );
            return;
        }

        // Run District Population Report
        try (Connection connection = con)
        {
            DistrictPopulationReport report =
                    new DistrictPopulationReport(connection);

            report.run(districtName);
        }
        catch (SQLException e)
        {
            System.out.println("Error running district population report");
            e.printStackTrace();
        }
    }
}