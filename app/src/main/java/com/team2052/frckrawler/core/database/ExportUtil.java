package com.team2052.frckrawler.core.database;

import android.util.Log;

import com.team2052.frckrawler.core.util.LogHelper;
import com.team2052.frckrawler.core.util.Utilities;
import com.team2052.frckrawler.db.DaoSession;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.MatchComment;
import com.team2052.frckrawler.db.MatchCommentDao;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.MetricDao;
import com.team2052.frckrawler.db.RobotEvent;
import com.team2052.frckrawler.db.RobotEventDao;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import au.com.bytecode.opencsv.CSVWriter;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * @author Adam
 * @since 10/4/2014
 */
public class ExportUtil {
    public static File exportEventDataToCSV(Event event, File location, DaoSession daoSession, float compileWeight) {
        final List<Metric> metrics = daoSession.getMetricDao().queryBuilder().where(MetricDao.Properties.GameId.eq(event.getGameId())).list();
        List<RobotEvent> robotEvents = daoSession.getRobotEventDao().queryBuilder().where(RobotEventDao.Properties.EventId.eq(event.getId())).list();

        Map<Long, List<CompiledMetricValue>> robots = new TreeMap<>();

        for (RobotEvent robotEvent : robotEvents) {
            robots.put(robotEvent.getRobot().getTeam().getNumber(), Utilities.MetricCompiler.getCompiledRobot(event, robotEvent.getRobot(), daoSession, compileWeight));
        }

        try {
            CSVWriter writer = new CSVWriter(new FileWriter(location), ',');

            List<String> header = new ArrayList<>();

            header.add("Team Number");
            header.add("Comments");

            for (Metric metric : metrics) {
                header.add(metric.getName());
            }

            writer.writeNext(Arrays.copyOf(header.toArray(), header.size(), String[].class));

            for (Map.Entry<Long, List<CompiledMetricValue>> entry : robots.entrySet()) {
                List<String> record = new ArrayList<>();
                record.add(String.valueOf(entry.getKey()));


                //Comments
                QueryBuilder<MatchComment> matchCommentQueryBuilder = daoSession.getMatchCommentDao().queryBuilder();
                matchCommentQueryBuilder.where(MatchCommentDao.Properties.EventId.eq(event.getId()));
                matchCommentQueryBuilder.where(MatchCommentDao.Properties.TeamId.eq(entry.getKey()));
                String comments = "";

                for (MatchComment matchComment : matchCommentQueryBuilder.list()) {
                    comments += "Match " + matchComment.getMatch().getNumber() + ": " + matchComment.getComment() + ", ";
                }

                record.add(comments);

                //Compiled values
                for (CompiledMetricValue metricValue : entry.getValue()) {
                    record.add(metricValue.getCompiledValue());
                }

                writer.writeNext(Arrays.copyOf(record.toArray(), record.size(), String[].class));
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return location;
    }
}
