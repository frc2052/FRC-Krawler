package com.team2052.frckrawler.database;

import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.team2052.frckrawler.database.models.Event;
import com.team2052.frckrawler.database.models.RobotEvents;
import com.team2052.frckrawler.database.models.metric.Metric;
import com.team2052.frckrawler.database.models.metric.MetricMatchData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam
 */
public class MetricCompiler
{
    public static List<CompiledMetricValue> compileMetricMatchData(Event event, Metric metric)
    {
        List<RobotEvents> robotEventses = new Select().from(RobotEvents.class).where("Event = ?", event.getId()).execute();
        List<CompiledMetricValue> compiledMetricValues = new ArrayList<>();
        for (RobotEvents robotEvents : robotEventses) {
            List<MetricValue> metricData = new ArrayList<>();
            for (Model matchData : new Select().from(MetricMatchData.class).where("Robot = ?", robotEvents.robot.getId()).and("Metric = ?", metric.getId()).execute()) {
                metricData.add(((MetricMatchData) matchData).getMetricValue());
            }
            compiledMetricValues.add(new CompiledMetricValue(robotEvents.robot, metric, metricData, metric.type, 1.0F));
        }
        return compiledMetricValues;
    }
}
