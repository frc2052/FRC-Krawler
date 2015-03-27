package com.team2052.frckrawler.core.database;

import com.team2052.frckrawler.core.tba.TBA;
import com.team2052.frckrawler.core.util.Utilities;
import com.team2052.frckrawler.db.DaoSession;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.db.MatchComment;
import com.team2052.frckrawler.db.MatchCommentDao;
import com.team2052.frckrawler.db.MatchData;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.MetricDao;
import com.team2052.frckrawler.db.Robot;
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
    public static File exportEventDataToCSV(Event event, File location, DBManager dbManager, float compileWeight) {
        Game game = dbManager.getGame(event);
        List<Metric> metrics = game.getMetricList();
        List<Robot> robots = dbManager.getRobots(event);
        List<MatchData> matchDataList = event.getMatchDataList();

        Map<Long, List<CompiledMetricValue>> compiledMap = new TreeMap<>();
        for (Robot robot : robots) {
            compiledMap.put(robot.getTeamId(), Utilities.MetricCompiler.getCompiledRobot(event, robot, dbManager, compileWeight));
        }

        try {
            CSVWriter writer = new CSVWriter(new FileWriter(location), ',');

            List<String> header = new ArrayList<>();

            header.add("Team Number");
            header.add("Team Name");
            header.add("Comments");

            for (Metric metric : metrics) {
                header.add(metric.getName());
            }
            header.add("TBA Link");


            writer.writeNext(Arrays.copyOf(header.toArray(), header.size(), String[].class));

            boolean provideTBAURL = event.getFmsid() != null;
            for (Map.Entry<Long, List<CompiledMetricValue>> entry : compiledMap.entrySet()) {
                List<String> record = new ArrayList<>();
                record.add(String.valueOf(entry.getKey()));
                record.add(dbManager.getTeam(entry.getKey()).getName());


                //Comments
                QueryBuilder<MatchComment> matchCommentQueryBuilder = dbManager.getDaoSession().getMatchCommentDao().queryBuilder();
                matchCommentQueryBuilder.where(MatchCommentDao.Properties.EventId.eq(event.getId()));
                String comments = "";

                for (MatchComment matchComment : matchCommentQueryBuilder.list()) {
                    comments += "Match " + dbManager.getMatch(matchComment).getNumber() + ": " + matchComment.getComment() + ", ";
                }

                record.add(comments);

                //Compiled values
                for (CompiledMetricValue metricValue : entry.getValue()) {
                    record.add(metricValue.getCompiledValue());
                }
                if (provideTBAURL) {
                    record.add(TBA.BASE_TBA_URL + "/team/" + entry.getKey() + "/" + event.getFmsid().substring(0, 4));
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
