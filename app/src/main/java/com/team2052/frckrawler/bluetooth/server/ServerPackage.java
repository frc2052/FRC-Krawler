package com.team2052.frckrawler.bluetooth.server;

import com.team2052.frckrawler.bluetooth.RobotComment;
import com.team2052.frckrawler.database.RxDBManager;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.MatchComment;
import com.team2052.frckrawler.db.MatchDatum;
import com.team2052.frckrawler.db.PitDatum;
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

    private final List<MatchDatum> metricMatchDatum;
    private final List<PitDatum> metricPitDatum;
    private final List<MatchComment> matchComments;
    private final List<RobotComment> robotComments;
    private Event scoutEvent;


    public ServerPackage(RxDBManager manager, Event scoutEvent) {
        metricMatchDatum = manager.getMatchDataTable().loadAll();
        metricPitDatum = manager.getPitDataTable().loadAll();
        matchComments = manager.getMatchCommentsTable().loadAll();
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
            for (int i = 0; i < metricMatchDatum.size(); i++) {
                rxDbManager.getMatchDataTable().insertMatchData(metricMatchDatum.get(i));
            }

            for (int i = 0; i < metricPitDatum.size(); i++) {
                rxDbManager.getPitDataTable().insert(metricPitDatum.get(i));
            }

            for (int i = 0; i < matchComments.size(); i++) {
                rxDbManager.getMatchCommentsTable().insertMatchComment(matchComments.get(i));
            }

            for (int i = 0; i < robotComments.size(); i++) {
                RobotComment robotComment = robotComments.get(i);
                Robot robot = rxDbManager.getRobotsTable().load(robotComment.getRobotId());

                //If robot is null ignore
                if (robot == null)
                    continue;

                if (robot.getLast_updated() == null || robot.getLast_updated().getTime() <= new Date().getTime()) {
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
