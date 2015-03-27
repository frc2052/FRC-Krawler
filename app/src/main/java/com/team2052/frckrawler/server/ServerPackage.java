package com.team2052.frckrawler.server;

import com.team2052.frckrawler.core.database.DBManager;
import com.team2052.frckrawler.core.util.LogHelper;
import com.team2052.frckrawler.db.DaoSession;
import com.team2052.frckrawler.db.MatchComment;
import com.team2052.frckrawler.db.MatchCommentDao;
import com.team2052.frckrawler.db.MatchData;
import com.team2052.frckrawler.db.MatchDataDao;
import com.team2052.frckrawler.db.PitData;
import com.team2052.frckrawler.db.PitDataDao;

import java.io.Serializable;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

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


    public ServerPackage(DaoSession session) {
        metricMatchData = session.getMatchDataDao().loadAll();
        metricPitData = session.getPitDataDao().loadAll();
        matchComments = session.getMatchCommentDao().loadAll();
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
                    dbManager.insertMatchData(m);
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
