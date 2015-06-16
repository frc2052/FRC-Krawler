package com.team2052.frckrawler.database;

import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.db.MatchComment;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.db.Team;
import com.team2052.frckrawler.tba.TBA;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * @author Adam
 * @since 10/4/2014
 */
public class ExportUtil {
    public static File exportEventDataToCSV(Event event, File location, DBManager dbManager, float compileWeight) {
        Game game = dbManager.getEventsTable().getGame(event);

        List<Metric> metrics = game.getMetricList();
        List<Robot> robots = dbManager.getEventsTable().getRobots(event);
        boolean provideTBAURL = event.getFmsid() != null;

        //Sort Robots
        Collections.sort(robots, (lhs, rhs) -> Double.compare(lhs.getTeam_id(), rhs.getTeam_id()));

        try {
            CSVWriter writer = new CSVWriter(new FileWriter(location), ',');

            List<String> header = new ArrayList<>();
            header.add("Team Number");
            header.add("Team Name");
            header.add("Match Comments");
            header.add("Robot Comments");
            for (Metric metric : metrics) {
                header.add(metric.getName());
            }
            header.add("TBA Link");

            writer.writeNext(Arrays.copyOf(header.toArray(), header.size(), String[].class));
            for (Robot robot : robots) {
                List<String> record = new ArrayList<>();
                Team team = dbManager.getRobotsTable().getTeam(robot);
                record.add(String.valueOf(team.getNumber()));
                record.add(team.getName());

                QueryBuilder<MatchComment> matchCommentQueryBuilder = dbManager.getMatchComments().query(null, null, robot.getId(), event.getId());
                String comments = "";

                for (MatchComment matchComment : matchCommentQueryBuilder.list()) {
                    comments += matchComment.getMatch_number() + ": " + matchComment.getComment() + ", ";
                }

                record.add(comments);
                record.add(robot.getComments());

                List<CompiledMetricValue> compiledRobot = MetricCompiler.getCompiledRobot(event, robot, dbManager, compileWeight);
                for (CompiledMetricValue metricValue : compiledRobot) {
                    record.add(metricValue.getCompiledValue());
                }

                if (provideTBAURL) {
                    record.add(TBA.TBA_URL + "team/" + team.getNumber() + "/" + event.getFmsid().substring(0, 4));
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
