package com.team2052.frckrawler.bluetooth;

import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.RobotComment;
import com.team2052.frckrawler.db.MatchComment;
import com.team2052.frckrawler.db.MatchData;
import com.team2052.frckrawler.db.PitData;
import com.team2052.frckrawler.db.Robot;

import java.io.Serializable;
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


    public ServerPackage(DBManager manager) {
        metricMatchData = manager.mMatchDatas.loadAll();
        metricPitData = manager.mPitDatas.loadAll();
        matchComments = manager.mMatchComments.loadAll();
        robotComments = manager.mRobots.getRobotComments();
    }

    /**
     * To save the data that is contained in the object
     * To be only ran on the server instance
     */
    public void save(final DBManager dbManager) {
        dbManager.runInTx(() -> {
            long startTime = System.currentTimeMillis();
            //Save all the data
            for (MatchData m : metricMatchData) {
                dbManager.mMatchDatas.insertMatchData(m);
            }

            for (PitData m : metricPitData) {
                dbManager.mPitDatas.insert(m);
            }

            for (MatchComment matchComment : matchComments) {
                dbManager.mMatchComments.insertMatchComment(matchComment);
            }

            for (RobotComment robotComment : robotComments) {
                Robot robot = dbManager.mRobots.load(robotComment.getRobotId());
                robot.setComments(robotComment.getComment());
                robot.update();
                dbManager.mRobots.update(robot);
            }
        });
    }
}
