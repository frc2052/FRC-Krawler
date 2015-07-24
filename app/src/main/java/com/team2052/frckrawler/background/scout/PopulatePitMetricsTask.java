package com.team2052.frckrawler.background.scout;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;

import com.google.common.base.Optional;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.MetricHelper;
import com.team2052.frckrawler.database.MetricValue;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.PitData;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.fragments.scout.ScoutPitFragment;
import com.team2052.frckrawler.views.metric.MetricWidget;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam
 * @since 12/28/2014.
 * Used for auto fill
 */
public class PopulatePitMetricsTask extends AsyncTask<Void, Void, Void> {
    private final FragmentActivity context;
    private final DBManager mDBManager;
    private ScoutPitFragment mFragment;
    private Event mEvent;
    private Robot robot;
    private ArrayList<MetricValue> mMetricWidgets;

    public PopulatePitMetricsTask(ScoutPitFragment scoutPitFragment, Event mEvent, Robot robot) {
        this.robot = robot;
        this.context = scoutPitFragment.getActivity();
        this.mDBManager = DBManager.getInstance(context);
        this.mFragment = scoutPitFragment;
        this.mEvent = mEvent;
    }

    @Override
    protected Void doInBackground(Void... params) {
        mMetricWidgets = new ArrayList<>();

        List<Metric> metrics = mDBManager.getMetricsTable().query(MetricHelper.MetricCategory.ROBOT_METRICS.id, null, mEvent.getGame_id()).list();
        List<PitData> pitDatas = mDBManager.getPitDataTable().query(robot.getId(), null, mEvent.getId(), null).list();

        if (pitDatas.size() == metrics.size()) {
            for (PitData pitData : pitDatas) {
                mMetricWidgets.add(new MetricValue(mDBManager.getPitDataTable().getMetric(pitData), pitData.getData()));
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
            final Optional<MetricWidget> widget = MetricWidget.createWidget(context, metric);
            if (widget.isPresent())
                mFragment.mLinearLayout.addView(widget.get());
        }

        mFragment.mComments.getEditText().setText(robot.getComments());
    }
}
