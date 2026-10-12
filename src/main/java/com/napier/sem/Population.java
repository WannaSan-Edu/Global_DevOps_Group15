package com.napier.sem;

/**
 * Population report model for US23–US26 and US29–US30.
 * Breakdown reports fill all fields; simple totals use name and total only.
 */
public class Population
{
    private String name;
    private long total;
    private long cityPopulation;
    private double cityPercentage;
    private long nonCityPopulation;
    private double nonCityPercentage;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public long getTotal()
    {
        return total;
    }

    public void setTotal(long total)
    {
        this.total = total;
    }

    public long getCityPopulation()
    {
        return cityPopulation;
    }

    public void setCityPopulation(long cityPopulation)
    {
        this.cityPopulation = cityPopulation;
    }

    public double getCityPercentage()
    {
        return cityPercentage;
    }

    public void setCityPercentage(double cityPercentage)
    {
        this.cityPercentage = cityPercentage;
    }

    public long getNonCityPopulation()
    {
        return nonCityPopulation;
    }

    public void setNonCityPopulation(long nonCityPopulation)
    {
        this.nonCityPopulation = nonCityPopulation;
    }

    public double getNonCityPercentage()
    {
        return nonCityPercentage;
    }

    public void setNonCityPercentage(double nonCityPercentage)
    {
        this.nonCityPercentage = nonCityPercentage;
    }
}
