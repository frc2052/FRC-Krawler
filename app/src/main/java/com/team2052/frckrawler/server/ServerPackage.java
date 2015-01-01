package com.team2052.frckrawler.server;

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
    public void save(final DaoSession session) {
        session.runInTx(new Runnable() {
            @Override
            public void run() {
                //Save all the data
                for (MatchData m : metricMatchData) {
                    m.setId(null);
                    QueryBuilder<MatchData> matchDataQueryBuilder = session.getMatchDataDao().queryBuilder();
                    matchDataQueryBuilder.where(MatchDataDao.Properties.RobotId.eq(m.getRobotId()));
                    matchDataQueryBuilder.where(MatchDataDao.Properties.MetricId.eq(m.getMetricId()));
                    matchDataQueryBuilder.where(MatchDataDao.Properties.MatchId.eq(m.getMatchId()));

                    MatchData unique = matchDataQueryBuilder.unique();


                    if (unique != null) {
                        unique.setData(m.getData());
                        unique.update();
                    } else {
                        session.insert(m);
                    }
                }

                for (PitData m : metricPitData) {
                    m.setId(null);
                    QueryBuilder<PitData> pitDataQueryBuilder = session.getPitDataDao().queryBuilder();
                    pitDataQueryBuilder.where(PitDataDao.Properties.RobotId.eq(m.getRobotId()));
                    pitDataQueryBuilder.where(PitDataDao.Properties.MetricId.eq(m.getMetricId()));
                    pitDataQueryBuilder.where(PitDataDao.Properties.EventId.eq(m.getEventId()));
                    PitData unique = pitDataQueryBuilder.unique();

                    if (unique != null) {
                        unique.setData(m.getData());
                        unique.update();
                    } else {
                        session.insert(m);
                    }
                }

                for (MatchComment matchComment : matchComments) {
                    matchComment.setId(null);
                    QueryBuilder<MatchComment> matchCommentQueryBuilder = session.getMatchCommentDao().queryBuilder();
                    matchCommentQueryBuilder.where(MatchCommentDao.Properties.MatchId.eq(matchComment.getMatchId()));
                    matchCommentQueryBuilder.where(MatchCommentDao.Properties.RobotId.eq(matchComment.getRobotId()));
                    matchCommentQueryBuilder.where(MatchCommentDao.Properties.EventId.eq(matchComment.getEventId()));

                    MatchComment unique = matchCommentQueryBuilder.unique();

                    if (unique != null) {
                        unique.setComment(matchComment.getComment());
                        unique.update();
                    } else {
                        session.insert(matchComment);
                    }
                }

            }
        });
    }
}
