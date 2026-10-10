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
                Thread.sleep(1000);

                // LOCAL (IntelliJ): localhost:33060
                // DOCKER (compose): db:3306
                con = DriverManager.getConnection(
                        "jdbc:mysql://localhost:33060/world?useSSL=false&allowPublicKeyRetrieval=true",
                        "root",
                        "example"
                );

                System.out.println("Successfully connected");
                Thread.sleep(1000);
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
                // >>> US09 - View all cities in a region
                ViewAllCitiesinaRegion us09 = new ViewAllCitiesinaRegion(con);
                us09.getCitiesInRegion("Southeast Asia");

                // Close connection
                con.close();
            }
            catch (Exception e)
            {
                System.out.println("Error closing connection to database");
            }
        }
    }
}