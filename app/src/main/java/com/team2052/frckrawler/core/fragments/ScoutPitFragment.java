package com.team2052.frckrawler.core.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.client.LoginHandler;
import com.team2052.frckrawler.client.background.PopulatePitMetricsTask;
import com.team2052.frckrawler.client.background.PopulatePitRobotsTask;
import com.team2052.frckrawler.client.background.SavePitMetricsTask;
import com.team2052.frckrawler.client.events.ScoutSyncSuccessEvent;
import com.team2052.frckrawler.core.database.MetricValue;
import com.team2052.frckrawler.core.ui.metric.MetricWidget;
import com.team2052.frckrawler.core.util.Utilities;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.db.RobotEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * @author Adam
 */
public class ScoutPitFragment extends BaseFragment implements AdapterView.OnItemSelectedListener {
    @InjectView(R.id.metricWidgetList)
    public
    LinearLayout mLinearLayout;
    public Event mEvent;
    public Spinner mTeamSpinner;
    public List<RobotEvent> mRobots;
    @InjectView(R.id.comments)
    public EditText mComments;
    SavePitMetricsTask mSaveTask;
    private PopulatePitRobotsTask mTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scouting_pit, container, false);
    }

    public void save() {
        Robot robot = mRobots.get(mTeamSpinner.getSelectedItemPosition()).getRobot();
        List<MetricValue> widgets = new ArrayList<>();

        for (int i = 0; i < mLinearLayout.getChildCount(); i++) {
            widgets.add(((MetricWidget) mLinearLayout.getChildAt(i)).getValue());
        }

        mSaveTask = new SavePitMetricsTask(getActivity(), mEvent, robot, widgets, mComments.getText().toString());
        mSaveTask.execute();
    }

    private void loadAllData(Event event) {
        if (event == null) {
            setErrorVisible(true);
            return;
        }

        mEvent = event;
        mTask = new PopulatePitRobotsTask(this, mEvent);
        mTask.execute();
    }

    public void setErrorVisible(boolean visible) {
        if (visible) {
            getView().findViewById(R.id.error_message).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.scroll_view).setVisibility(View.GONE);
        } else {
            getView().findViewById(R.id.error_message).setVisibility(View.GONE);
            getView().findViewById(R.id.scroll_view).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        mTeamSpinner = (Spinner) view.findViewById(R.id.team);
        mTeamSpinner.setOnItemSelectedListener(this);
        loadAllData(Utilities.ScoutUtil.getScoutEvent(getActivity(), mDaoSession));
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.scout, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            if (mTeamSpinner.getSelectedItem() != null) {
                //Get data from view
                LoginHandler loginHandler = LoginHandler.getInstance(getActivity(), mDaoSession);
                if (!loginHandler.isLoggedOn() && !loginHandler.loggedOnUserStillExists()) {
                    loginHandler.login(getActivity());
                    //TODO AUTO AFTER RELOG
                    Toast.makeText(getActivity(), "Try to save again", Toast.LENGTH_LONG).show();
                } else {
                    save();
                }
            }
        }
        return false;
    }

    @SuppressWarnings("unused")
    public void onEvent(ScoutSyncSuccessEvent event) {
        loadAllData(Utilities.ScoutUtil.getScoutEvent(getActivity(), mDaoSession));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Robot robot = mRobots.get(mTeamSpinner.getSelectedItemPosition()).getRobot();
        new PopulatePitMetricsTask(this, mEvent, robot).execute();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
