package com.team2052.frckrawler.client.background;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;

import com.team2052.frckrawler.core.FRCKrawler;
import com.team2052.frckrawler.core.database.DBManager;
import com.team2052.frckrawler.core.database.MetricValue;
import com.team2052.frckrawler.core.fragments.ScoutPitFragment;
import com.team2052.frckrawler.core.ui.metric.MetricWidget;
import com.team2052.frckrawler.core.util.MetricUtil;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.MetricDao;
import com.team2052.frckrawler.db.PitData;
import com.team2052.frckrawler.db.PitDataDao;
import com.team2052.frckrawler.db.Robot;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * @author Adam
 * @since 12/28/2014.
 * Used for auto fill
 */
public class PopulatePitMetricsTask extends AsyncTask<Void, Void, Void> {
    private final FragmentActivity context;
    private final DBManager mDaoSession;
    private ScoutPitFragment mFragment;
    private Event mEvent;
    private Robot robot;
    private ArrayList<MetricValue> mMetricWidgets;

    public PopulatePitMetricsTask(ScoutPitFragment scoutPitFragment, Event mEvent, Robot robot) {
        this.robot = robot;
        this.context = scoutPitFragment.getActivity();
        this.mDaoSession = ((FRCKrawler) context.getApplicationContext()).getDBSession();
        this.mFragment = scoutPitFragment;
        this.mEvent = mEvent;
    }

    @Override
    protected Void doInBackground(Void... params) {
        mMetricWidgets = new ArrayList<>();

        QueryBuilder<PitData> pitDataQueryBuilder = mDaoSession.getDaoSession().getPitDataDao().queryBuilder();
        pitDataQueryBuilder.where(PitDataDao.Properties.EventId.eq(mEvent.getId()));
        pitDataQueryBuilder.where(PitDataDao.Properties.RobotId.eq(robot.getId()));

        QueryBuilder<Metric> metricQueryBuilder = mDaoSession.getDaoSession().getMetricDao().queryBuilder();
        metricQueryBuilder.where(MetricDao.Properties.GameId.eq(mEvent.getGameId()));
        metricQueryBuilder.where(MetricDao.Properties.Category.eq(MetricUtil.MetricType.ROBOT_METRICS.ordinal()));

        List<Metric> metrics = metricQueryBuilder.list();
        List<PitData> pitDatas = pitDataQueryBuilder.list();

        if (pitDatas.size() == metrics.size()) {
            for (PitData pitData : pitDatas) {
                mMetricWidgets.add(new MetricValue(mDaoSession.getDaoSession(), pitData));
            }
        } else {
            for (Metric metric : metrics) {
                mMetricWidgets.add(new MetricValue(metric, null));
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mFragment.mLinearLayout.removeAllViews();

        for (MetricValue metric : mMetricWidgets) {
            mFragment.mLinearLayout.addView(MetricWidget.createWidget(context, metric));
        }

        //TODO
        mFragment.mComments.setText("");
    }
}
