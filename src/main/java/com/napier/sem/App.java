package com.napier.sem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Entry point for Group 15 population reports.
 */
public class App
{
    /**
     * Connection to the MySQL world database.
     */
    private Connection con = null;

    public static void main(String[] args)
    {
        App app = new App();

        // Docker Compose uses host "db"; local IntelliJ uses localhost:33060.
        if (args.length < 1)
        {
            app.connect("localhost:33060");
        }
        else
        {
            app.connect(args[0]);
        }

        // Call the separate class to fetch data and print the report
        ArrayList<Population> countryPopulations = ViewPopulationByCountry.getPopulationByCountry(app.con);
        ViewPopulationByCountry.printReport(countryPopulations);

        app.disconnect();
    }

    /**
     * Connect to the world database.
     *
     * @param location Host and port, for example localhost:33060 or db:3306
     */
    public void connect(String location)
    {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 0; i < retries; ++i)
        {
            System.out.println("Connecting to database...");
            try
            {
                Thread.sleep(1000);
                con = DriverManager.getConnection(
                        "jdbc:mysql://" + location + "/world?allowPublicKeyRetrieval=true&useSSL=false",
                        "root",
                        "example");
                System.out.println("Successfully connected");
                return;
            }
            catch (SQLException sqle)
            {
                System.out.println("Failed to connect to database attempt " + i);
                System.out.println(sqle.getMessage());
            }
            catch (InterruptedException ie)
            {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    /**
     * Close the database connection.
     */
    public void disconnect()
    {
        if (con != null)
        {

            ArrayList<Population> countryPopulations = ViewPopulationByCountry.getPopulationByCountry(con);
            ViewPopulationByCountry.printReport(countryPopulations);
            try
            {
                con.close();
                System.out.println("Successfully disconnected from database.");
            }
            catch (Exception e)
            {
                System.out.println("Error closing connection to database");
            }
        }
    }
}