package com.team2052.frckrawler.client.background;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.team2052.frckrawler.client.LoginHandler;
import com.team2052.frckrawler.core.FRCKrawler;
import com.team2052.frckrawler.core.database.DBManager;
import com.team2052.frckrawler.core.database.MetricValue;
import com.team2052.frckrawler.core.util.LogHelper;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Match;
import com.team2052.frckrawler.db.MatchComment;
import com.team2052.frckrawler.db.MatchData;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.db.RobotDao;
import com.team2052.frckrawler.db.Team;

import java.util.List;

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
    private final DBManager mDaoSession;

    public SaveMatchMetricsTask(Context context, Event event, Team team, Match match, List<MetricValue> metricValues, String comment) {
        this.context = context;
        this.mDaoSession = ((FRCKrawler) context.getApplicationContext()).getDBSession();
        this.mEvent = event;
        this.mTeam = team;
        this.mMatch = match;
        this.mMetricValues = metricValues;
        this.mComment = comment;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Robot robot = mDaoSession.getDaoSession().getRobotDao().queryBuilder().where(RobotDao.Properties.TeamId.eq(mTeam.getNumber())).where(RobotDao.Properties.GameId.eq(mEvent.getGameId())).unique();

        LogHelper.info(String.valueOf(mMatch.getId()));
        //Insert Metric Data
        for (MetricValue metricValue : mMetricValues) {
            MatchData matchData = new MatchData(
                    null,
                    robot.getId(),
                    metricValue.getMetric().getId(),
                    mMatch.getId(), mEvent.getId(),
                    LoginHandler.getInstance(context, mDaoSession).getLoggedOnUser().getId(),
                    metricValue.getValue());
            mDaoSession.insertMatchData(matchData);
        }


        if (!Strings.isNullOrEmpty(mComment)) {
            MatchComment matchComment = new MatchComment(
                    null,
                    mMatch.getId(),
                    robot.getId(),
                    mEvent.getId(),
                    mComment);
            mDaoSession.insertMatchComment(matchComment);
        }

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        Toast.makeText(context, "Save Complete", Toast.LENGTH_LONG).show();
    }
}
