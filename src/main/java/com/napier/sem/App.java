package com.napier.sem;

import java.sql.*;
import java.util.ArrayList;

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
                // Specify the continent you want to query
                String continent = "Europe";

                // US18: Capital cities in a continent by population
                ArrayList<CapitalCity> capitalCities = CapitalCitiesInAcontinent.getCapitalCitiesByContinent(con, continent);
                CapitalCitiesInAcontinent.printReport(capitalCities, continent);

                // Close connection
                con.close();
                System.out.println("Successfully disconnected from database.");
            }
            catch (Exception e)
            {
                System.out.println("Error closing connection to database or executing report");
                e.printStackTrace();
            }
        }
        else
        {
            System.out.println("Failed to establish a database connection.");
        }
    }
}