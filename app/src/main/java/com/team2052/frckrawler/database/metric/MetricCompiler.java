package com.team2052.frckrawler.database.metric;

import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.MatchData;
import com.team2052.frckrawler.db.MatchDataDao;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.PitData;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.db.RobotEvent;
import com.team2052.frckrawler.tba.JSON;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

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
            if (metric.getCategory() == MetricHelper.MATCH_PERF_METRICS) {
                QueryBuilder<MatchData> queryBuilder = dbManager.getMatchDataTable().query(robot.getId(), metric.getId(), null, 0, event.getId(), null).orderAsc(MatchDataDao.Properties.Match_number);

                for (MatchData matchData : queryBuilder.list()) {
                    metricData.add(new MetricValue(dbManager.getMatchDataTable().getMetric(matchData), JSON.getAsJsonObject(matchData.getData())));
                }

            } else if (metric.getCategory() == MetricHelper.ROBOT_METRICS) {
                QueryBuilder<PitData> queryBuilder = dbManager.getPitDataTable().query(robot.getId(), metric.getId(), event.getId(), null);

                for (PitData pitData : queryBuilder.list()) {
                    metricData.add(new MetricValue(dbManager.getPitDataTable().getMetric(pitData), JSON.getAsJsonObject(pitData.getData())));
                }
            }
            compiledMetricValues.add(new CompiledMetricValue(robot, metric, metricData, compileWeight));
        }
        return compiledMetricValues;
    }
}
