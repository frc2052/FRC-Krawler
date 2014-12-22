package com.team2052.frckrawler.util;

import com.team2052.frckrawler.database.CompiledMetricValue;
import com.team2052.frckrawler.database.MetricValue;
import com.team2052.frckrawler.database.serializers.StringArrayDeserializer;
import com.team2052.frckrawler.db.*;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * @author Adam
 * @since 12/21/2014.
 */
public class MetricUtil
{
    public static final int BOOLEAN = 0;
    public static final int COUNTER = 1;
    public static final int SLIDER = 2;
    public static final int CHOOSER = 3;

    public static Metric createBooleanMetric(Game game, MetricUtil.MetricType metricCategory, String name, String description)
    {
        return new Metric(null, name, metricCategory.ordinal(), description, BOOLEAN, "", game.getId());
    }

    public static Metric createCounterMetric(Game game, MetricUtil.MetricType metricCategory, String name, String description, int min, int max, int incrementation)
    {
        return new Metric(null, name, metricCategory.ordinal(), description, COUNTER, StringArrayDeserializer.deserialize(new String[]{Integer.toString(min), Integer.toString(max), Integer.toString(incrementation)}), game.getId());
    }

    public static Metric createSliderMetric(Game game, MetricUtil.MetricType metricCategory, String name, String description, int min, int max)
    {
        return new Metric(null, name, metricCategory.ordinal(), description, SLIDER, StringArrayDeserializer.deserialize(new String[]{Integer.toString(min), Integer.toString(max)}), game.getId());
    }

    public static Metric createChooserMetric(Game game, MetricUtil.MetricType metricCategory, String name, String description, String[] choices)
    {
        return new Metric(null, name, metricCategory.ordinal(), description, CHOOSER, StringArrayDeserializer.deserialize(choices), game.getId());
    }

    public static class MetricCompiler
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
                if (metric.getCategory() == MetricUtil.MetricType.MATCH_PERF_METRICS.ordinal()) {

                    QueryBuilder<MatchData> queryBuilder = daoSession.getMatchDataDao().queryBuilder();
                    queryBuilder.where(MatchDataDao.Properties.MetricId.eq(metric.getId()));
                    queryBuilder.where(MatchDataDao.Properties.RobotId.eq(robotEvents.getRobotId()));

                    for (MatchData matchData : queryBuilder.list()) {
                        if (matchData.getMatch().getEvent().getId().equals(event.getId())) {
                            LogHelper.debug(matchData.getData() + matchData.getRobotId());
                            metricData.add(new MetricValue(matchData));
                        }
                    }

                } else if (metric.getCategory() == MetricUtil.MetricType.ROBOT_METRICS.ordinal()) {
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
                if (metric.getCategory() == MetricUtil.MetricType.MATCH_PERF_METRICS.ordinal()) {

                    QueryBuilder<MatchData> queryBuilder = daoSession.getMatchDataDao().queryBuilder();
                    queryBuilder.where(MatchDataDao.Properties.MetricId.eq(metric.getId()));
                    queryBuilder.where(MatchDataDao.Properties.RobotId.eq(robot.getId()));

                    for (MatchData matchData : queryBuilder.list()) {
                        if (matchData.getMatch().getEvent().getId().equals(event.getId())) {
                            LogHelper.debug(matchData.getData() + matchData.getRobotId());
                            metricData.add(new MetricValue(matchData));
                        }
                    }

                } else if (metric.getCategory() == MetricUtil.MetricType.ROBOT_METRICS.ordinal()) {
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

    public static enum MetricType
    {
        MATCH_PERF_METRICS, ROBOT_METRICS;
        public static final MetricType[] VALID_TYPES = values();
    }

}
