package com.team2052.frckrawler.bluetooth.server;

import com.google.common.base.Strings;
import com.team2052.frckrawler.bluetooth.RobotComment;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.MatchComment;
import com.team2052.frckrawler.db.MatchData;
import com.team2052.frckrawler.db.PitData;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.util.ScoutUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * A object that contains all the data that is sent to the server
 * easier to manage
 *
 * @author Adam
 * @since 12/24/2014.
 */
public class ServerPackage implements Serializable {
    public static final String LOG_TAG = "ServerPackage";

    private final List<MatchData> metricMatchData;
    private final List<PitData> metricPitData;
    private final List<MatchComment> matchComments;
    private final List<RobotComment> robotComments;
    private Event scoutEvent;


    public ServerPackage(DBManager manager, Event scoutEvent) {
        metricMatchData = manager.getMatchDataTable().loadAll();
        metricPitData = manager.getPitDataTable().loadAll();
        matchComments = manager.getMatchComments().loadAll();
        robotComments = manager.getRobotsTable().getRobotComments();
        this.scoutEvent = scoutEvent;
    }

    /**
     * To save the data that is contained in the object
     * To be only ran on the server instance
     */
    public void save(final DBManager dbManager) {
        dbManager.runInTx(() -> {
            //Save all the data
            for (int i = 0; i < metricMatchData.size(); i++) {
                dbManager.getMatchDataTable().insertMatchData(metricMatchData.get(i));
            }

            for (int i = 0; i < metricPitData.size(); i++) {
                dbManager.getPitDataTable().insert(metricPitData.get(i));
            }

            for (int i = 0; i < matchComments.size(); i++) {
                dbManager.getMatchComments().insertMatchComment(matchComments.get(i));
            }

            for (int i = 0; i < robotComments.size(); i++) {
                RobotComment robotComment = robotComments.get(i);
                Robot robot = dbManager.getRobotsTable().load(robotComment.getRobotId());
                if (robot.getLast_updated().getTime() <= new Date().getTime()) {
                    robot.setLast_updated(new Date());
                    robot.setComments(robotComment.getComment());
                    robot.update();
                    dbManager.getRobotsTable().update(robot);
                }
            }
        });
    }

    public Event getScoutEvent() {
        return scoutEvent;
    }
}
