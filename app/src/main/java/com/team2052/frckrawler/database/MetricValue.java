package com.team2052.frckrawler.database;


import frckrawler.MatchData;
import frckrawler.Metric;
import frckrawler.PitData;

public class MetricValue
{
    private Metric metric;
    private String value;

    public MetricValue(Metric metric, String value)
    {
        this.metric = metric;
        this.value = value;
    }

    public MetricValue(MatchData matchData)
    {
        this(matchData.getMetric(), matchData.getData());
    }

    public MetricValue(PitData matchData)
    {
        this(matchData.getMetric(), matchData.getData());
    }

    public Metric getMetric()
    {
        return metric;
    }

    public String getValue()
    {
        return value;
    }

}
