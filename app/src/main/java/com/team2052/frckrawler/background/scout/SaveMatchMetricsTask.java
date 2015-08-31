package com.team2052.frckrawler.background.scout;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;

import com.google.common.base.Strings;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.MetricValue;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.MatchComment;
import com.team2052.frckrawler.db.MatchData;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.db.User;
import com.team2052.frckrawler.fragments.scout.ScoutMatchFragment;

import java.util.Date;
import java.util.List;

/**
 * @author Adam
 * @since 12/28/2014.
 */
public class SaveMatchMetricsTask extends AsyncTask<Void, Void, Void> {
    private final Context context;
    private final Event mEvent;
    private final Robot mRobot;
    private final int match_num;
    private final List<MetricValue> mMetricValues;
    private final String mComment;
    private final DBManager mDaoSession;
    private ScoutMatchFragment fragment;
    @Nullable
    private User user;
    private int match_type;
    private boolean inserted;

    public SaveMatchMetricsTask(Context context, ScoutMatchFragment fragment, Event event, Robot robot, @Nullable User user, int match_num, int match_type, List<MetricValue> metricValues, String comment) {
        this.context = context;
        this.fragment = fragment;
        this.user = user;
        this.match_type = match_type;
        this.mDaoSession = DBManager.getInstance(context);
        this.mEvent = event;
        this.mRobot = robot;
        this.match_num = match_num;
        this.mMetricValues = metricValues;
        this.mComment = comment;
    }

    @Override
    protected Void doInBackground(Void... params) {
        //Insert Metric Data
        long userid = 0;
        inserted = false;
        for (MetricValue metricValue : mMetricValues) {
            MatchData matchData = new MatchData(
                    null,
                    mEvent.getId(),
                    mRobot.getId(),
                    user != null ? user.getId() : null,
                    metricValue.getMetric().getId(),
                    match_type,
                    match_num,
                    new Date(),
                    metricValue.getValue());

            inserted = mDaoSession.getMatchDataTable().insertMatchData(matchData);
        }


        if (!Strings.isNullOrEmpty(mComment)) {
            MatchComment matchComment = new MatchComment(null);
            matchComment.setMatch_number((long) match_num);
            matchComment.setMatch_type(match_type);
            matchComment.setRobot(mRobot);
            matchComment.setEvent(mEvent);
            matchComment.setComment(mComment);
            inserted = mDaoSession.getMatchComments().insertMatchComment(matchComment);
        }

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        if (inserted) {
            Snackbar.make(fragment.getView(), "Save Complete", Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(fragment.getView(), "Update Complete", Snackbar.LENGTH_LONG).show();
        }
    }
}
