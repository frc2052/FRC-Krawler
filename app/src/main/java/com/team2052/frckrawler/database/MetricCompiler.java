package com.team2052.frckrawler.database;

import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.MatchData;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.PitData;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.db.RobotEvent;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Adam on 5/2/2015.
 */
public class MetricCompiler {
    /**
     * Used to compile data based on ATTENDING TEAMS and METRIC
     *
     * @param event  the event for attending teams
     * @param metric the metric that you want to compile
     * @return the compiled data from attending teams and metric
     */
    public static List<CompiledMetricValue> getCompiledMetric(Event event, Metric metric, DBManager dbManager, float compileWeight) {
        List<RobotEvent> robotEventses = dbManager.getEventsTable().getRobotEvents(event);
        List<CompiledMetricValue> compiledMetricValues = new ArrayList<>();
        for (RobotEvent robotEvents : robotEventses) {
            List<MetricValue> metricData = new ArrayList<>();
            Robot robot = dbManager.getRobotEvents().getRobot(robotEvents);
            if (metric.getCategory() == MetricHelper.MetricCategory.MATCH_PERF_METRICS.id) {
                QueryBuilder<MatchData> queryBuilder = dbManager.getMatchDataTable().query(robot.getId(), metric.getId(), null, null, event.getId(), null);

                for (MatchData matchData : queryBuilder.list()) {
                    metricData.add(new MetricValue(dbManager.getMatchDataTable().getMetric(matchData), matchData.getData()));
                }

            } else if (metric.getCategory() == MetricHelper.MetricCategory.ROBOT_METRICS.id) {
                QueryBuilder<PitData> queryBuilder = dbManager.getPitDataTable().query(robot.getId(), metric.getId(), event.getId(), null);

                for (PitData pitData : queryBuilder.list()) {
                    metricData.add(new MetricValue(dbManager.getPitDataTable().getMetric(pitData), pitData.getData()));
                }
            }
            compiledMetricValues.add(new CompiledMetricValue(robot, metric, metricData, MetricHelper.MetricType.values()[metric.getType()], compileWeight));
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
    public static List<CompiledMetricValue> getCompiledRobot(Event event, Robot robot, DBManager dbManager, float compileWeight) {
        //Load all the metrics
        final List<Metric> metrics = dbManager.getMetricsTable().query(null, null, event.getGame_id()).list();
        final List<CompiledMetricValue> compiledMetricValues = new ArrayList<>();
        for (Metric metric : metrics) {
            List<MetricValue> metricData = new ArrayList<>();
            if (metric.getCategory() == MetricHelper.MetricCategory.MATCH_PERF_METRICS.id) {

                QueryBuilder<MatchData> queryBuilder = dbManager.getMatchDataTable().query(robot.getId(), metric.getId(), null, null, event.getId(), null);

                for (MatchData matchData : queryBuilder.list()) {
                    if (matchData.getEvent_id() == event.getId()) {
                        metricData.add(new MetricValue(dbManager.getMatchDataTable().getMetric(matchData), matchData.getData()));
                    }
                }

            } else if (metric.getCategory() == MetricHelper.MetricCategory.ROBOT_METRICS.id) {
                QueryBuilder<PitData> queryBuilder = dbManager.getPitDataTable().query(robot.getId(), metric.getId(), event.getId(), null);

                for (PitData pitData : queryBuilder.list()) {
                    metricData.add(new MetricValue(dbManager.getPitDataTable().getMetric(pitData), pitData.getData()));
                }
            }
            compiledMetricValues.add(new CompiledMetricValue(robot, metric, metricData, MetricHelper.MetricType.values()[metric.getType()], compileWeight));
        }
        return compiledMetricValues;
    }
}
