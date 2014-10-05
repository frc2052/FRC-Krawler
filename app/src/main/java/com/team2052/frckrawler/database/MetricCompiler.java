package com.team2052.frckrawler.database;

import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.team2052.frckrawler.activity.MetricsActivity;
import com.team2052.frckrawler.database.models.Event;
import com.team2052.frckrawler.database.models.Robot;
import com.team2052.frckrawler.database.models.RobotEvents;
import com.team2052.frckrawler.database.models.metric.Metric;
import com.team2052.frckrawler.database.models.metric.MetricMatchData;
import com.team2052.frckrawler.database.models.metric.MetricPitData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam
 */
public class MetricCompiler
{
    /**
     * Used to compile data based on ATTENDING TEAMS and METRIC
     *
     * @param event  the event for attending teams
     * @param metric the metric that you want to compile
     * @return the compiled data from attending teams and metric
     */
    public static List<CompiledMetricValue> getCompiledMetric(Event event, Metric metric)
    {
        List<RobotEvents> robotEventses = new Select().from(RobotEvents.class).where("Event = ?", event.getId()).execute();
        List<CompiledMetricValue> compiledMetricValues = new ArrayList<>();
        for (RobotEvents robotEvents : robotEventses) {
            List<MetricValue> metricData = new ArrayList<>();
            if (metric.category == MetricsActivity.MetricType.MATCH_PERF_METRICS.ordinal()) {
                for (Model matchData : new Select().from(MetricMatchData.class).where("Robot = ?", robotEvents.robot.getId()).and("Metric = ?", metric.getId()).execute()) {
                    if (((MetricMatchData) matchData).match.event.getId().equals(event.getId())) {
                        metricData.add(((MetricMatchData) matchData).getMetricValue());
                    }
                }

            } else if (metric.category == MetricsActivity.MetricType.ROBOT_METRICS.ordinal()) {
                for (Model matchData : new Select().from(MetricPitData.class).where("Robot = ?", robotEvents.robot.getId()).and("Metric = ?", metric.getId()).and("Event = ?", event.getId()).execute()) {
                    metricData.add(((MetricPitData) matchData).getMetricValue());
                }
            }
            compiledMetricValues.add(new CompiledMetricValue(robotEvents.robot, metric, metricData, metric.category, 1.0F));
        }
        return compiledMetricValues;
    }

    /**
     * Used to export to CSV by ROW based on PER ROBOT
     *
     * @param event
     * @param robot
     * @return
     */
    public static List<CompiledMetricValue> getCompiledRobot(Event event, Robot robot)
    {
        //Load all the metrics
        final List<Metric> metrics = new Select().from(Metric.class).where("Game = ?", event.game.getId()).execute();
        final List<CompiledMetricValue> compiledMetricValues = new ArrayList<>();
        for (Metric metric : metrics) {
            List<MetricValue> metricData = new ArrayList<>();
            if (metric.category == MetricsActivity.MetricType.MATCH_PERF_METRICS.ordinal()) {
                for (Model matchData : new Select().from(MetricMatchData.class).where("Robot = ?", robot.getId()).and("Metric = ?", metric.getId()).execute()) {
                    if (((MetricMatchData) matchData).match.event.getId().equals(event.getId())) {
                        metricData.add(((MetricMatchData) matchData).getMetricValue());
                    }
                }

            } else if (metric.category == MetricsActivity.MetricType.ROBOT_METRICS.ordinal()) {
                for (Model matchData : new Select().from(MetricPitData.class).where("Robot = ?", robot.getId()).and("Metric = ?", metric.getId()).and("Event = ?", event.getId()).execute()) {
                    metricData.add(((MetricPitData) matchData).getMetricValue());
                }
            }
            compiledMetricValues.add(new CompiledMetricValue(robot, metric, metricData, metric.category, 1.0F));
        }
        return compiledMetricValues;
    }
}
