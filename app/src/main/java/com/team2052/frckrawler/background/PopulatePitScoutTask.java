package com.team2052.frckrawler.background;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import com.team2052.frckrawler.FRCKrawler;
import com.team2052.frckrawler.database.MetricValue;
import com.team2052.frckrawler.db.DaoSession;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.MetricDao;
import com.team2052.frckrawler.db.RobotEvent;
import com.team2052.frckrawler.db.RobotEventDao;
import com.team2052.frckrawler.db.Team;
import com.team2052.frckrawler.fragment.ScoutPitFragment;
import com.team2052.frckrawler.util.Utilities;
import com.team2052.frckrawler.view.metric.MetricWidget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * @author Adam
 * @since 12/28/2014.
 */
public class PopulatePitScoutTask extends AsyncTask<Void, Void, Void> {
    private final ScoutPitFragment mFragment;
    private final DaoSession mDaoSession;
    private final Event mEvent;
    private final Context context;
    private ArrayList<MetricValue> mMetricWidgets;
    private String[] mRobotListStrings;
    private List<RobotEvent> mRobots;

    public PopulatePitScoutTask(ScoutPitFragment fragment, Event event) {
        this.mFragment = fragment;
        this.context = fragment.getActivity();
        this.mEvent = event;
        this.mDaoSession = ((FRCKrawler) context.getApplicationContext()).getDaoSession();
    }

    @Override
    protected Void doInBackground(Void... params) {
        //Build Query
        QueryBuilder<Metric> metricQueryBuilder = mDaoSession.getMetricDao().queryBuilder();
        metricQueryBuilder.where(MetricDao.Properties.GameId.eq(mEvent.getGame().getId()));
        metricQueryBuilder.where(MetricDao.Properties.Category.eq(Utilities.MetricUtil.MetricType.ROBOT_METRICS.ordinal()));
        mMetricWidgets = new ArrayList<>();

        for (Metric metric : metricQueryBuilder.list()) {
            mMetricWidgets.add(new MetricValue(metric, null));
        }

        mRobots = mDaoSession.getRobotEventDao().queryBuilder().where(RobotEventDao.Properties.EventId.eq(mEvent.getId())).list();

        //Sort the robot numbers
        Collections.sort(mRobots, new Comparator<RobotEvent>() {
            @Override
            public int compare(RobotEvent lhs, RobotEvent rhs) {
                return Double.compare(lhs.getRobot().getTeam().getNumber(), rhs.getRobot().getTeam().getNumber());
            }
        });

        ArrayList<String> robots = new ArrayList<>();
        for (RobotEvent robotEvent : mRobots) {
            Team robot = robotEvent.getRobot().getTeam();
            robots.add(robot.getNumber() + ", " + robot.getName());
        }
        mRobotListStrings = robots.toArray(new String[robots.size()]);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mFragment.mLinearLayout.removeAllViews();

        for (MetricValue metric : mMetricWidgets) {
            mFragment.mLinearLayout.addView(MetricWidget.createWidget(context, metric));
        }

        mFragment.mRobots = mRobots;
        mFragment.mTeamSpinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, mRobotListStrings));
    }
}
