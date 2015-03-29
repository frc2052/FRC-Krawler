package com.team2052.frckrawler.client.background;

import android.content.Context;
import android.os.AsyncTask;

import com.team2052.frckrawler.core.FRCKrawler;
import com.team2052.frckrawler.core.database.DBManager;
import com.team2052.frckrawler.core.database.MetricValue;
import com.team2052.frckrawler.core.fragments.ScoutMatchFragment;
import com.team2052.frckrawler.core.ui.metric.MetricWidget;
import com.team2052.frckrawler.core.util.MetricUtil;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Match;
import com.team2052.frckrawler.db.MatchComment;
import com.team2052.frckrawler.db.MatchCommentDao;
import com.team2052.frckrawler.db.MatchData;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.MetricDao;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.db.Team;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * @author Adam
 * @since 12/28/2014.
 * Used to auto fill the metrics so the scout can update the metric data
 */
public class PopulateMatchMetricsTask extends AsyncTask<Void, Void, Void> {

    private final Context context;
    private final DBManager mDbManager;
    private ScoutMatchFragment mFragment;
    private Event event;
    private Team team;
    private Match match;
    private ArrayList<MetricValue> mMetricValues;
    private MatchComment mMatchComment;

    public PopulateMatchMetricsTask(ScoutMatchFragment fragment, Event event, Team team, Match match) {
        this.mFragment = fragment;
        this.context = fragment.getActivity();
        this.mDbManager = ((FRCKrawler) context.getApplicationContext()).getDBSession();
        this.event = event;
        this.team = team;
        this.match = match;
    }

    @Override
    protected Void doInBackground(Void... params) {
        //Get the robot
        Robot robot = mDbManager.getRobot(team.getNumber(), event.getGameId());
        //Build the queries
        QueryBuilder<Metric> metricQueryBuilder = mDbManager.getDaoSession().getMetricDao().queryBuilder();
        metricQueryBuilder.where(MetricDao.Properties.GameId.eq(event.getGameId()));
        metricQueryBuilder.where(MetricDao.Properties.Category.eq(MetricUtil.MetricType.MATCH_PERF_METRICS.ordinal()));

        QueryBuilder<MatchComment> matchCommentQueryBuilder = mDbManager.getDaoSession().getMatchCommentDao().queryBuilder();
        matchCommentQueryBuilder.where(MatchCommentDao.Properties.EventId.eq(event.getId()));
        matchCommentQueryBuilder.where(MatchCommentDao.Properties.RobotId.eq(robot.getId()));
        matchCommentQueryBuilder.where(MatchCommentDao.Properties.MatchId.eq(match.getId()));

        mMatchComment = matchCommentQueryBuilder.unique();


        //Get the metrics and the current data
        List<Metric> metrics = metricQueryBuilder.list();

        List<MatchData> currentData = mDbManager.getMatchData(robot.getId(), null, match.getId(), event.getId(), null);

        mMetricValues = new ArrayList<>();


        //Use the current data if it's equal to the size of metrics
        if (currentData.size() == metrics.size()) {
            for (MatchData matchData : currentData) {
                mMetricValues.add(new MetricValue(mDbManager.getDaoSession(), matchData));
            }
        } else {
            for (Metric metric : metrics) {
                mMetricValues.add(new MetricValue(metric, null));
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        //Display the views to the user
        mFragment.mMetricList.removeAllViews();
        for (MetricValue metricValue : mMetricValues) {
            mFragment.mMetricList.addView(MetricWidget.createWidget(context, metricValue));
        }

        //Set the comment if there is one
        if (mMatchComment != null) {
            mFragment.mComments.setText(mMatchComment.getComment());
        } else {
            mFragment.mComments.setText("");
        }
    }
}
