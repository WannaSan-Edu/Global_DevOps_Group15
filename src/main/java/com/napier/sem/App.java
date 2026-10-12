        // After the connection retry loop:
        if (con == null) {
            System.err.println(
                    "Could not connect to the database after 100 attempts."
            );
            return;
        }

        try {
            // Specify the continent you want to query
            String continent = "Europe";

            // US18: Capital cities in a continent by population
            ArrayList<CapitalCity> capitalCities =
                    CapitalCitiesInAcontinent.getCapitalCitiesByContinent(con, continent);
            CapitalCitiesInAcontinent.printReport(capitalCities, continent);

            // Country population report (uses same connection)
            CountryPopulationReport report =
                    new CountryPopulationReport(con);
            report.run(countryName);

        } catch (Exception e) {
            System.out.println("Error executing reports");
            e.printStackTrace();
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                    System.out.println("Successfully disconnected from database.");
                }
            } catch (SQLException e) {
                System.err.println("Error closing database connection.");
                e.printStackTrace();
            }
        }
