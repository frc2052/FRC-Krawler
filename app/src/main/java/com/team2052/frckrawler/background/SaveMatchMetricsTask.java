package com.team2052.frckrawler.background;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.team2052.frckrawler.FRCKrawler;
import com.team2052.frckrawler.bluetooth.scout.LoginHandler;
import com.team2052.frckrawler.database.MetricValue;
import com.team2052.frckrawler.db.DaoSession;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Match;
import com.team2052.frckrawler.db.MatchComment;
import com.team2052.frckrawler.db.MatchCommentDao;
import com.team2052.frckrawler.db.MatchData;
import com.team2052.frckrawler.db.MatchDataDao;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.db.RobotDao;
import com.team2052.frckrawler.db.Team;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * @author Adam
 * @since 12/28/2014.
 */
public class SaveMatchMetricsTask extends AsyncTask<Void, Void, Void> {
    private final Context context;
    private final Event mEvent;
    private final Team mTeam;
    private final Match mMatch;
    private final List<MetricValue> mMetricValues;
    private final String mComment;
    private final DaoSession mDaoSession;

    public SaveMatchMetricsTask(Context context, Event event, Team team, Match match, List<MetricValue> metricValues, String comment) {
        this.context = context;
        this.mDaoSession = ((FRCKrawler) context.getApplicationContext()).getDaoSession();
        this.mEvent = event;
        this.mTeam = team;
        this.mMatch = match;
        this.mMetricValues = metricValues;
        this.mComment = comment;


    }

    @Override
    protected Void doInBackground(Void... params) {
        Robot robot = mDaoSession.getRobotDao().queryBuilder().where(RobotDao.Properties.TeamId.eq(mTeam.getNumber())).where(RobotDao.Properties.GameId.eq(mEvent.getGameId())).unique();

        //Insert Metric Data
        for (MetricValue metricValue : mMetricValues) {
            QueryBuilder<MatchData> matchDataQueryBuilder = mDaoSession.getMatchDataDao().queryBuilder();
            matchDataQueryBuilder.where(MatchDataDao.Properties.RobotId.eq(robot.getId()));
            matchDataQueryBuilder.where(MatchDataDao.Properties.MetricId.eq(metricValue.getMetric().getId()));
            matchDataQueryBuilder.where(MatchDataDao.Properties.MatchId.eq(mMatch.getId()));
            matchDataQueryBuilder.where(MatchDataDao.Properties.EventId.eq(mEvent.getId()));

            MatchData currentData = matchDataQueryBuilder.unique();
            MatchData matchData = new MatchData(null, metricValue.getValue(), robot.getId(), metricValue.getMetric().getId(), mMatch.getId(), LoginHandler.getInstance(context, mDaoSession).getLoggedOnUser().getId(), mEvent.getId());

            if (currentData != null) {
                currentData.setData(matchData.getData());
                currentData.update();
            } else {
                mDaoSession.insert(matchData);
            }
        }


        if (mComment.length() > 0) {
            QueryBuilder<MatchComment> matchCommentQueryBuilder = mDaoSession.getMatchCommentDao().queryBuilder();
            matchCommentQueryBuilder.where(MatchCommentDao.Properties.EventId.eq(mEvent.getId()));
            matchCommentQueryBuilder.where(MatchCommentDao.Properties.TeamId.eq(mTeam.getNumber()));
            matchCommentQueryBuilder.where(MatchCommentDao.Properties.RobotId.eq(robot.getId()));

            MatchComment currentData = matchCommentQueryBuilder.unique();
            MatchComment matchComment = new MatchComment(null, mMatch.getId(), mComment, robot.getId(), mEvent.getId(), mTeam.getNumber());

            if (currentData != null) {
                currentData.setComment(matchComment.getComment());
                currentData.update();
            } else {
                mDaoSession.insert(matchComment);
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Toast.makeText(context, "Save Complete", Toast.LENGTH_LONG).show();
    }
}
