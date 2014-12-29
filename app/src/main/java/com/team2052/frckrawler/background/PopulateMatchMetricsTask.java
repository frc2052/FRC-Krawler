package com.team2052.frckrawler.background;

import android.content.Context;
import android.os.AsyncTask;

import com.team2052.frckrawler.FRCKrawler;
import com.team2052.frckrawler.database.MetricValue;
import com.team2052.frckrawler.db.DaoSession;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Match;
import com.team2052.frckrawler.db.MatchComment;
import com.team2052.frckrawler.db.MatchCommentDao;
import com.team2052.frckrawler.db.MatchData;
import com.team2052.frckrawler.db.MatchDataDao;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.MetricDao;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.db.RobotDao;
import com.team2052.frckrawler.db.Team;
import com.team2052.frckrawler.fragment.ScoutMatchFragment;
import com.team2052.frckrawler.util.Utilities;
import com.team2052.frckrawler.view.metric.MetricWidget;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * @author Adam
 * @since 12/28/2014.
 */
public class PopulateMatchMetricsTask extends AsyncTask<Void, Void, Void> {

    private final Context context;
    private final DaoSession mDaoSession;
    private ScoutMatchFragment mFragment;
    private Event event;
    private Team team;
    private Match match;
    private ArrayList<MetricValue> mMetricValues;
    private MatchComment mMatchComment;

    public PopulateMatchMetricsTask(ScoutMatchFragment fragment, Event event, Team team, Match match) {
        this.mFragment = fragment;
        this.context = fragment.getActivity();
        this.mDaoSession = ((FRCKrawler) context.getApplicationContext()).getDaoSession();
        this.event = event;
        this.team = team;
        this.match = match;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Robot robot = mDaoSession.getRobotDao().queryBuilder().where(RobotDao.Properties.GameId.eq(event.getGameId())).where(RobotDao.Properties.TeamId.eq(team.getNumber())).unique();

        QueryBuilder<MatchData> matchDataQueryBuilder = mDaoSession.getMatchDataDao().queryBuilder();
        matchDataQueryBuilder.where(MatchDataDao.Properties.EventId.eq(event.getId()));
        matchDataQueryBuilder.where(MatchDataDao.Properties.MatchId.eq(match.getId()));
        matchDataQueryBuilder.where(MatchDataDao.Properties.RobotId.eq(robot.getId()));

        QueryBuilder<Metric> metricQueryBuilder = mDaoSession.getMetricDao().queryBuilder();
        metricQueryBuilder.where(MetricDao.Properties.GameId.eq(event.getGame().getId()));
        metricQueryBuilder.where(MetricDao.Properties.Category.eq(Utilities.MetricUtil.MetricType.MATCH_PERF_METRICS.ordinal()));

        QueryBuilder<MatchComment> matchCommentQueryBuilder = mDaoSession.getMatchCommentDao().queryBuilder();
        matchCommentQueryBuilder.where(MatchCommentDao.Properties.EventId.eq(event.getId()));
        matchCommentQueryBuilder.where(MatchCommentDao.Properties.TeamId.eq(team.getNumber()));
        matchCommentQueryBuilder.where(MatchCommentDao.Properties.RobotId.eq(robot.getId()));
        matchCommentQueryBuilder.where(MatchCommentDao.Properties.MatchId.eq(match.getId()));

        mMatchComment = matchCommentQueryBuilder.unique();


        List<Metric> metrics = metricQueryBuilder.list();
        List<MatchData> matchDatas = matchDataQueryBuilder.list();
        mMetricValues = new ArrayList<>();


        if (matchDatas.size() == metrics.size()) {
            for (MatchData matchData : matchDatas) {
                mMetricValues.add(new MetricValue(matchData));
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
        mFragment.mMetricList.removeAllViews();

        for (MetricValue metricValue : mMetricValues) {
            mFragment.mMetricList.addView(MetricWidget.createWidget(context, metricValue));
        }

        if (mMatchComment != null) {
            mFragment.mComments.setText(mMatchComment.getComment());
        } else {
            mFragment.mComments.setText("");
        }
    }
}
