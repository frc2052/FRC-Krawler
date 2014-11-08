package com.team2052.frckrawler.database;

import com.team2052.frckrawler.activity.MetricsActivity;
import com.team2052.frckrawler.db.DaoSession;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.MatchData;
import com.team2052.frckrawler.db.MatchDataDao;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.MetricDao;
import com.team2052.frckrawler.db.PitData;
import com.team2052.frckrawler.db.PitDataDao;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.db.RobotEvent;
import com.team2052.frckrawler.db.RobotEventDao;
import com.team2052.frckrawler.util.LogHelper;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

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
    public static List<CompiledMetricValue> getCompiledMetric(Event event, Metric metric, DaoSession daoSession)
    {
        List<RobotEvent> robotEventses = daoSession.getRobotEventDao().queryBuilder().where(RobotEventDao.Properties.EventId.eq(event.getId())).list();
        List<CompiledMetricValue> compiledMetricValues = new ArrayList<>();
        for (RobotEvent robotEvents : robotEventses) {
            List<MetricValue> metricData = new ArrayList<>();
            if (metric.getCategory() == MetricsActivity.MetricType.MATCH_PERF_METRICS.ordinal()) {

                QueryBuilder<MatchData> queryBuilder = daoSession.getMatchDataDao().queryBuilder();
                queryBuilder.where(MatchDataDao.Properties.MetricId.eq(metric.getId()));
                queryBuilder.where(MatchDataDao.Properties.RobotId.eq(robotEvents.getRobotId()));

                for (MatchData matchData : queryBuilder.list()) {
                    if (matchData.getMatch().getEvent().getId().equals(event.getId())) {
                        LogHelper.debug(matchData.getData() + matchData.getRobotId());
                        metricData.add(new MetricValue(matchData));
                    }
                }

            } else if (metric.getCategory() == MetricsActivity.MetricType.ROBOT_METRICS.ordinal()) {
                QueryBuilder<PitData> queryBuilder = daoSession.getPitDataDao().queryBuilder();
                queryBuilder.where(PitDataDao.Properties.EventId.eq(event.getId()));
                queryBuilder.where(PitDataDao.Properties.MetricId.eq(metric.getId()));
                queryBuilder.where(PitDataDao.Properties.RobotId.eq(robotEvents.getRobotId()));

                for (PitData matchData : queryBuilder.list()) {
                    metricData.add(new MetricValue(matchData));
                }
            }
            compiledMetricValues.add(new CompiledMetricValue(robotEvents.getRobot(), metric, metricData, metric.getType(), 1.0F));
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
    public static List<CompiledMetricValue> getCompiledRobot(Event event, Robot robot, DaoSession daoSession)
    {
        //Load all the metrics
        final List<Metric> metrics = daoSession.getMetricDao().queryBuilder().where(MetricDao.Properties.GameId.eq(event.getGameId())).list();
        final List<CompiledMetricValue> compiledMetricValues = new ArrayList<>();
        for (Metric metric : metrics) {
            List<MetricValue> metricData = new ArrayList<>();
            if (metric.getCategory() == MetricsActivity.MetricType.MATCH_PERF_METRICS.ordinal()) {

                QueryBuilder<MatchData> queryBuilder = daoSession.getMatchDataDao().queryBuilder();
                queryBuilder.where(MatchDataDao.Properties.MetricId.eq(metric.getId()));
                queryBuilder.where(MatchDataDao.Properties.RobotId.eq(robot.getId()));

                for (MatchData matchData : queryBuilder.list()) {
                    if (matchData.getMatch().getEvent().getId().equals(event.getId())) {
                        LogHelper.debug(matchData.getData() + matchData.getRobotId());
                        metricData.add(new MetricValue(matchData));
                    }
                }

            } else if (metric.getCategory() == MetricsActivity.MetricType.ROBOT_METRICS.ordinal()) {
                QueryBuilder<PitData> queryBuilder = daoSession.getPitDataDao().queryBuilder();
                queryBuilder.where(PitDataDao.Properties.EventId.eq(event.getId()));
                queryBuilder.where(PitDataDao.Properties.MetricId.eq(metric.getId()));
                queryBuilder.where(PitDataDao.Properties.RobotId.eq(robot.getId()));

                for (PitData matchData : queryBuilder.list()) {
                    metricData.add(new MetricValue(matchData));
                }
            }
            compiledMetricValues.add(new CompiledMetricValue(robot, metric, metricData, metric.getType(), 1.0F));
        }
        return compiledMetricValues;
    }
}
