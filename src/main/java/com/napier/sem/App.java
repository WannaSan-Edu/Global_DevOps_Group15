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
                con = DriverManager.getConnection("jdbc:mysql://db:3306/world?useSSL=false&allowPublicKeyRetrieval=true", "root", "example");
                System.out.println("Successfully connected");
                // Wait a bit
                Thread.sleep(1000);
                // Exit for loop
                break;
            }
            catch (SQLException sqle)
            {
                System.out.println("Failed to connect to database attempt " + Integer.toString(i));
                System.out.println(sqle.getMessage());
            }
            catch (InterruptedException ie)
            {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }


        if (con != null)
        {
            try
            {
                // Generate the US26 report.
                TotalWorldPopulation.printReport(con);
            }
            finally
            {
                // Close the database connection after generating the report.
                try
                {
                    con.close();
                    System.out.println("Database connection closed.");
                }
                catch (SQLException e)
                {
                    System.out.println("Error closing connection to database.");
                    System.out.println(e.getMessage());
                }
            }
        }
        else
        {
            System.out.println(
                    "Unable to generate Total World Population Report: " +
                            "could not connect to the database.");
        }
    }
}