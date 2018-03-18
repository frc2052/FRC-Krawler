package com.team2052.frckrawler.bluetooth.syncable;

import android.content.Context;

import com.team2052.frckrawler.data.RxDBManager;
import com.team2052.frckrawler.models.MatchComment;
import com.team2052.frckrawler.models.MatchDatum;
import com.team2052.frckrawler.models.PitDatum;

import java.util.List;

public class ServerDataSyncable extends ServerSyncable {

    private final List<MatchDatum> metricMatchDatum;
    private final List<PitDatum> metricPitDatum;
    private final List<MatchComment> matchComments;

    public ServerDataSyncable(Context context) {
        RxDBManager dbManager = RxDBManager.Companion.getInstance(context);
        metricMatchDatum = dbManager.getMatchDataTable().loadAll();
        metricPitDatum = dbManager.getPitDataTable().loadAll();
        matchComments = dbManager.getMatchCommentsTable().loadAll();
        //TODO COMMENTS
        //robotComments = dbManager.getTeamsTable().getComm
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

            /*for (int i = 0; i < robotComments.size(); i++) {
                RobotComment robotComment = robotComments.get(i);
                Robot team = dbManager.getRobotsTable().load(robotComment.getRobotId());

                //If team is null ignore
                if (team == null) {
                    continue;
                }

                if (team.getLast_updated() == null || team.getLast_updated().getTime() <= new Date().getTime()) {
                    team.setLast_updated(new Date());
                    team.setComments(robotComment.getComment());
                    team.update();
                    dbManager.getRobotsTable().update(team);
                }
            }*/
        });
    }
}
