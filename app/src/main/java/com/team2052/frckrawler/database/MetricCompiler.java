package com.team2052.frckrawler.database;

import com.activeandroid.query.Select;
import com.team2052.frckrawler.database.models.Event;
import com.team2052.frckrawler.database.models.Metric;
import com.team2052.frckrawler.database.models.MetricMatchData;
import com.team2052.frckrawler.database.models.RobotEvents;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam
 */
public class MetricCompiler
{
    public static List<CompiledMetricValue> compileMetricDataInEvent(Event event, Metric metric)
    {
        List<RobotEvents> robotEventses = new Select().from(RobotEvents.class).where("Event = ?", event.getId()).execute();
        List<CompiledMetricValue> compiledMetricValues = new ArrayList<>();

        for (RobotEvents robotEvents : robotEventses) {
            List<MetricMatchData> metricData = new Select().from(MetricMatchData.class).where("Robot = ?", robotEvents.robot.getId()).and("Metric = ?", metric.getId()).execute();
            compiledMetricValues.add(new CompiledMetricValue(robotEvents.robot, metric, metricData, metric.type, 1.0F));
        }

        return compiledMetricValues;
    }
}
