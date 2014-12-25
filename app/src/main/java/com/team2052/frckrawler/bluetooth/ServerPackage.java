package com.team2052.frckrawler.bluetooth;

import com.team2052.frckrawler.db.DaoSession;
import com.team2052.frckrawler.db.MatchComment;
import com.team2052.frckrawler.db.MatchCommentDao;
import com.team2052.frckrawler.db.MatchData;
import com.team2052.frckrawler.db.MatchDataDao;
import com.team2052.frckrawler.db.PitData;
import com.team2052.frckrawler.db.PitDataDao;

import java.io.Serializable;
import java.util.List;

/**
 * A object that contains all the data that is sent to the server
 * easier to manage
 *
 * @author Adam
 * @since 12/24/2014.
 */
public class ServerPackage implements Serializable
{
    private final List<MatchData> metricMatchData;
    private final List<PitData> metricPitData;
    private final List<MatchComment> matchComments;


    public ServerPackage(DaoSession session)
    {
        metricMatchData = session.getMatchDataDao().loadAll();
        metricPitData = session.getPitDataDao().loadAll();
        matchComments = session.getMatchCommentDao().loadAll();
    }

    /**
     * To save the data that is contained in the object
     * To be only ran on the server instance
     */
    public void save(final DaoSession session){
        session.runInTx(new Runnable()
        {
            @Override
            public void run()
            {
                //Save all the data
                for (MatchData m : metricMatchData) {
                    //Don't Save data if it already exists.
                    if (session.getMatchDataDao().queryBuilder().where(MatchDataDao.Properties.RobotId.eq(m.getRobotId())).where(MatchDataDao.Properties.MetricId.eq(m.getMetricId())).where(MatchDataDao.Properties.MatchId.eq(m.getMatchId())).list().size() <= 0)
                        session.getMatchDataDao().insertOrReplace(m);
                }

                for (PitData m : metricPitData) {
                    //Don't Save data if it already exists.
                    if (session.getPitDataDao().queryBuilder().where(PitDataDao.Properties.RobotId.eq(m.getRobotId())).where(PitDataDao.Properties.MetricId.eq(m.getMetricId())).list().size() <= 0)
                        session.getPitDataDao().insertOrReplace(m);
                }

                for (MatchComment matchComment : matchComments) {
                    //Don't Save data if it already exists.
                    if (session.getMatchCommentDao().queryBuilder().where(MatchCommentDao.Properties.MatchId.eq(matchComment.getMatchId())).where(MatchCommentDao.Properties.RobotId.eq(matchComment.getRobotId())).list().size() <= 0)
                        session.getMatchCommentDao().insert(matchComment);
                }

            }
        });
    }
}
