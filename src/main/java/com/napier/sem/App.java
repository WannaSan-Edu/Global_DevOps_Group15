
package com.napier.sem;

import java.sql.*;

public class App
{
    public static void main(String[] args)
    {
        try
        {
            // Load Database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        // Connection to the database
        Connection con = null;
        int retries = 100;

        for (int i = 0; i < retries; ++i)
        {
            System.out.println("Connecting to database...");

            try
            {
                // Wait a bit for db to start
                Thread.sleep(1000);

                // Connect to database
                con = DriverManager.getConnection(
                        "jdbc:mysql://db:3306/world?useSSL=false&allowPublicKeyRetrieval=true",
                        "root",
                        "example"
                );

                System.out.println("Successfully connected");
                Thread.sleep(1000);
                break;
            }
            catch (SQLException sqle)
            {
                System.out.println(
                        "Failed to connect to database attempt " + (i + 1)
                );
                System.out.println(sqle.getMessage());
            }
            catch (InterruptedException ie)
            {
                Thread.currentThread().interrupt();
                System.out.println("Thread interrupted.");
                return;
            }
        }

        if (con != null)
        {
            try
            {
                // Call Top Cities in Country report
                TopCitiesInCountry.generateReport(con, "Japan", 10);
            }
            finally
            {
                try
                {
                    // Close connection
                    con.close();
                }
                catch (SQLException e)
                {
                    System.out.println(
                            "Error closing connection to database"
                    );
                }
            }
        }
        else
        {
            System.out.println("Could not connect to database.");
        }
    }
}
