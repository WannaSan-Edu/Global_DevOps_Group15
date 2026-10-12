
package com.napier.sem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class App
{
    private Connection con = null;

    public static void main(String[] args)
    {
        App app = new App();

        // Default host port for a locally running application.
        String location = args.length > 0
                ? args[0]
                : "localhost:33060";

        try
        {
            app.connect(location);

            if (app.con == null)
            {
                System.err.println(
                        "US25 failed: Could not connect to the database.");
                return;
            }

            ArrayList<Population> populations =
                    ViewPopulationByCountry.getPopulationByCountry(app.con);

            ViewPopulationByCountry.printReport(populations);
        }
        finally
        {
            app.disconnect();
        }
    }

    public void connect(String location)
    {
        String url = "jdbc:mysql://" + location
                + "/world?allowPublicKeyRetrieval=true&useSSL=false";

        String username = "root";
        String password = "example";

        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");

            int maxAttempts = 10;

            for (int attempt = 1; attempt <= maxAttempts; attempt++)
            {
                try
                {
                    System.out.println(
                            "Connecting to database (attempt "
                                    + attempt + "/" + maxAttempts + ")...");

                    con = DriverManager.getConnection(
                            url, username, password);

                    System.out.println(
                            "Database connected successfully.");

                    return;
                }
                catch (SQLException e)
                {
                    System.err.println(
                            "Connection attempt failed: " + e.getMessage());

                    if (attempt < maxAttempts)
                    {
                        Thread.sleep(1000);
                    }
                }
            }

            System.err.println(
                    "Unable to connect after " + maxAttempts + " attempts.");
        }
        catch (ClassNotFoundException e)
        {
            System.err.println(
                    "MySQL JDBC driver is missing. Check pom.xml.");
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            System.err.println("Database connection interrupted.");
        }
    }

    public void disconnect()
    {
        if (con != null)
        {
            try
            {
                con.close();
                System.out.println("Database connection closed.");
            }
            catch (SQLException e)
            {
                System.err.println(
                        "Error closing database connection: "
                                + e.getMessage());
            }
            finally
            {
                con = null;
            }
        }
    }
}
