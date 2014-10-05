package com.team2052.frckrawler.database;


import com.team2052.frckrawler.database.models.metric.Metric;

public class MetricValue
{

    private Metric metric;
    private String value; //Array only used for


    public MetricValue(Metric metric, String value) throws MetricTypeMismatchException
    {
        this.metric = metric;
        this.value = value;
    }

    public Metric getMetric()
    {
        return metric;
    }

    public String getValue()
    {
        return value;
    }

    public class MetricTypeMismatchException extends Exception
    {
    }
}
