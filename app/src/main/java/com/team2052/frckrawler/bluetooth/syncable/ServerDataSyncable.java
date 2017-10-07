package com.team2052.frckrawler.bluetooth.syncable;

import android.content.Context;

import com.team2052.frckrawler.bluetooth.model.RobotComment;
import com.team2052.frckrawler.data.RxDBManager;
import com.team2052.frckrawler.helpers.ScoutHelper;
import com.team2052.frckrawler.models.Event;
import com.team2052.frckrawler.models.MatchComment;
import com.team2052.frckrawler.models.MatchDatum;
import com.team2052.frckrawler.models.PitDatum;
import com.team2052.frckrawler.models.Robot;

import java.util.Date;
import java.util.List;

public class ServerDataSyncable extends ServerSyncable {

    private final List<MatchDatum> metricMatchDatum;
    private final List<PitDatum> metricPitDatum;
    private final List<MatchComment> matchComments;
    private final List<RobotComment> robotComments;

    public ServerDataSyncable(Context context) {
        Event scoutEvent = ScoutHelper.getScoutEvent(context);
        if (scoutEvent == null && !ScoutHelper.isDeviceScout(context)) {
            //throw new IllegalStateException("This device isn't a scout!");
        }
        if (scoutEvent != null) {
            setEvent_hash(scoutEvent.getUnique_hash());
        }
        RxDBManager dbManager = RxDBManager.Companion.getInstance(context);
        metricMatchDatum = dbManager.getMatchDataTable().loadAll();
        metricPitDatum = dbManager.getPitDataTable().loadAll();
        matchComments = dbManager.getMatchCommentsTable().loadAll();
        robotComments = dbManager.getRobotsTable().getRobotComments();
    }

    @Override
    public void saveToServer(final Context context) {
        RxDBManager dbManager = RxDBManager.Companion.getInstance(context);

        dbManager.runInTx(() -> {
            //Save all the data
            for (int i = 0; i < metricMatchDatum.size(); i++) {
                dbManager.getMatchDataTable().insertMatchData(metricMatchDatum.get(i));
            }

            for (int i = 0; i < metricPitDatum.size(); i++) {
                dbManager.getPitDataTable().insert(metricPitDatum.get(i));
            }

            for (int i = 0; i < matchComments.size(); i++) {
                dbManager.getMatchCommentsTable().insertMatchComment(matchComments.get(i));
            }

            for (int i = 0; i < robotComments.size(); i++) {
                RobotComment robotComment = robotComments.get(i);
                Robot robot = dbManager.getRobotsTable().load(robotComment.getRobotId());

                //If robot is null ignore
                if (robot == null)
                    continue;

                if (robot.getLast_updated() == null || robot.getLast_updated().getTime() <= new Date().getTime()) {
                    robot.setLast_updated(new Date());
                    robot.setComments(robotComment.getComment());
                    robot.update();
                    dbManager.getRobotsTable().update(robot);
                }
            }
        });
    }
}
