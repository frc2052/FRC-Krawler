package com.team2052.frckrawler.bluetooth.server;

import com.team2052.frckrawler.bluetooth.RobotComment;
import com.team2052.frckrawler.database.RxDBManager;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.MatchComment;
import com.team2052.frckrawler.db.MatchData;
import com.team2052.frckrawler.db.PitData;
import com.team2052.frckrawler.db.Robot;

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


    public ServerPackage(RxDBManager manager, Event scoutEvent) {
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
    public void save(final RxDBManager rxDbManager) {
        rxDbManager.runInTx(() -> {
            //Save all the data
            for (int i = 0; i < metricMatchData.size(); i++) {
                rxDbManager.getMatchDataTable().insertMatchData(metricMatchData.get(i));
            }

            for (int i = 0; i < metricPitData.size(); i++) {
                rxDbManager.getPitDataTable().insert(metricPitData.get(i));
            }

            for (int i = 0; i < matchComments.size(); i++) {
                rxDbManager.getMatchComments().insertMatchComment(matchComments.get(i));
            }

            for (int i = 0; i < robotComments.size(); i++) {
                RobotComment robotComment = robotComments.get(i);
                Robot robot = rxDbManager.getRobotsTable().load(robotComment.getRobotId());

                if (robot.getLast_updated() == null) {
                    robot.setLast_updated(new Date());
                }

                if (robot.getLast_updated().getTime() <= new Date().getTime()) {
                    robot.setLast_updated(new Date());
                    robot.setComments(robotComment.getComment());
                    robot.update();
                    rxDbManager.getRobotsTable().update(robot);
                }
            }
        });
    }

    public Event getScoutEvent() {
        return scoutEvent;
    }
}
