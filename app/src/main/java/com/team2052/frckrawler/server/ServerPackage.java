package com.team2052.frckrawler.server;

import android.util.Log;

import com.team2052.frckrawler.core.database.DBManager;
import com.team2052.frckrawler.core.util.LogHelper;
import com.team2052.frckrawler.db.MatchComment;
import com.team2052.frckrawler.db.MatchData;
import com.team2052.frckrawler.db.PitData;

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
    private final List<MatchData> metricMatchData;
    private final List<PitData> metricPitData;
    private final List<MatchComment> matchComments;


    public ServerPackage(DBManager manager) {
        metricMatchData = manager.getDaoSession().getMatchDataDao().loadAll();
        metricPitData = manager.getDaoSession().getPitDataDao().loadAll();
        matchComments = manager.getDaoSession().getMatchCommentDao().loadAll();
    }

    /**
     * To save the data that is contained in the object
     * To be only ran on the server instance
     */
    public void save(final DBManager dbManager) {
        dbManager.getDaoSession().runInTx(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                LogHelper.info("Saving Data");
                //Save all the data
                for (MatchData m : metricMatchData) {
                    Log.i("FRCKrawler", m.getData() + String.valueOf(dbManager.insertMatchData(m)));
                }

                for (PitData m : metricPitData) {
                    dbManager.insertPitData(m);
                }

                for (MatchComment matchComment : matchComments) {
                    dbManager.insertMatchComment(matchComment);
                }
                LogHelper.info("Finished Saving. Took " + (System.currentTimeMillis() - startTime) + "ms");
            }
        });
    }
}
