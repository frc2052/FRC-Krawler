package com.team2052.frckrawler.fragments.scout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.common.collect.Lists;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.background.scout.PopulatePitMetricsTask;
import com.team2052.frckrawler.background.scout.PopulatePitRobotsTask;
import com.team2052.frckrawler.background.scout.SavePitMetricsTask;
import com.team2052.frckrawler.database.MetricValue;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.db.RobotEvent;
import com.team2052.frckrawler.fragments.BaseFragment;
import com.team2052.frckrawler.views.metric.MetricWidget;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * @author Adam
 */
public class ScoutPitFragment extends BaseFragment implements AdapterView.OnItemSelectedListener {
    public static final String EVENT_ID = "EVENT_ID";
    public Event mEvent;
    public List<RobotEvent> mRobots;
    @InjectView(R.id.metricWidgetList)
    public LinearLayout mLinearLayout;
    @InjectView(R.id.comments)
    public TextInputLayout mComments;
    @InjectView(R.id.robot)
    public Spinner mTeamSpinner;

    private SavePitMetricsTask mSaveTask;
    private PopulatePitRobotsTask mTask;

    public static ScoutPitFragment newInstance(Event event) {
        Bundle args = new Bundle();
        args.putLong(EVENT_ID, event.getId());
        ScoutPitFragment fragment = new ScoutPitFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        mEvent = mDbManager.getEventsTable().load(getArguments().getLong(EVENT_ID));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scouting_pit, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        mTeamSpinner.setOnItemSelectedListener(this);
        mTask = new PopulatePitRobotsTask(this, mEvent);
        mTask.execute();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.scout, menu);
        menu.removeItem(R.id.action_view_match);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            if (!mRobots.isEmpty() && mEvent != null && getSelectedRobot() != null) {
                mSaveTask = new SavePitMetricsTask(
                        getActivity(),
                        mEvent,
                        getSelectedRobot(),
                        getMetricValues(),
                        getComment());
                mSaveTask.execute();
            }
        } else {
            Snackbar.make(getView(), getActivity().getString(R.string.something_seems_wrong), Snackbar.LENGTH_SHORT).show();
        }
        return false;
    }

    private Robot getSelectedRobot() {
        return mDbManager.getRobotEvents().getRobot(mRobots.get(mTeamSpinner.getSelectedItemPosition()));
    }

    private List<MetricValue> getMetricValues() {
        List<MetricValue> values = Lists.newArrayList();
        for (int i = 0; i < mLinearLayout.getChildCount(); i++) {
            values.add(((MetricWidget) mLinearLayout.getChildAt(i)).getValue());
        }
        return values;
    }

    private String getComment() {
        return mComments.getEditText().getText().toString();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Robot robot = mDbManager.getRobotEvents().getRobot(mRobots.get(mTeamSpinner.getSelectedItemPosition()));
        new PopulatePitMetricsTask(this, mEvent, robot).execute();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {/*Nope*/}
}
