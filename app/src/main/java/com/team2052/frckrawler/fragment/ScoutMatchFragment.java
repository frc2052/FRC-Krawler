package com.team2052.frckrawler.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.background.PopulateMatchMetricsTask;
import com.team2052.frckrawler.background.PopulateMatchScoutTask;
import com.team2052.frckrawler.background.SaveMatchMetricsTask;
import com.team2052.frckrawler.bluetooth.scout.LoginHandler;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.MetricValue;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Match;
import com.team2052.frckrawler.db.Team;
import com.team2052.frckrawler.events.scout.ScoutSyncSuccessEvent;
import com.team2052.frckrawler.util.Utilities;
import com.team2052.frckrawler.view.metric.MetricWidget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * @author Adam
 */
public class ScoutMatchFragment extends BaseFragment implements AdapterView.OnItemSelectedListener {
    @InjectView(R.id.match_number)
    public Spinner mMatchSpinner;
    @InjectView(R.id.metricWidgetList)
    public LinearLayout mMetricList;
    @InjectView(R.id.comments)
    public EditText mComments;
    public List<Match> mMatches;
    public List<Team> mTeams;
    @InjectView(R.id.team)
    Spinner mAllianceSpinner;
    private Event mEvent;
    private PopulateMatchScoutTask mTask;
    private SaveMatchMetricsTask mSaveTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scouting_match, container, false);
    }

    private void loadAllData(Event event) {
        if (event == null) {
            setErrorVisible(true);
            return;
        }
        mTask = new PopulateMatchScoutTask(this, mEvent = event);
        mTask.execute();
    }

    public void setErrorVisible(boolean visible) {
        if (visible) {
            getView().findViewById(R.id.error).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.scroll_view).setVisibility(View.GONE);
        } else {
            getView().findViewById(R.id.error).setVisibility(View.GONE);
            getView().findViewById(R.id.scroll_view).setVisibility(View.VISIBLE);
        }
    }

    private void save() {
        if (mSaveTask != null) {
            mSaveTask.cancel(false);
        }
        Team team = mTeams.get(mAllianceSpinner.getSelectedItemPosition());
        Match match = mMatches.get(mMatchSpinner.getSelectedItemPosition());
        String comment = mComments.getText().toString();

        List<MetricValue> metricValues = new ArrayList<>();
        //Get Widgets
        for (int i = 0; i < mMetricList.getChildCount(); i++) {
            metricValues.add(((MetricWidget) mMetricList.getChildAt(i)).getMetricValue());
        }
        mSaveTask = new SaveMatchMetricsTask(getActivity(), mEvent, team, match, metricValues, comment);
        mSaveTask.execute();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        mMatchSpinner.setOnItemSelectedListener(this);
        mAllianceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Match match = mMatches.get(mMatchSpinner.getSelectedItemPosition());
                Team team = mTeams.get(position);
                new PopulateMatchMetricsTask(ScoutMatchFragment.this, mEvent, team, match).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        loadAllData(Utilities.ScoutUtil.getScoutEvent(getActivity(), mDaoSession));
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.scout, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            if (mAllianceSpinner.getSelectedItem() != null && mMatchSpinner.getSelectedItem() != null) {
                LoginHandler loginHandler = LoginHandler.getInstance(getActivity(), mDaoSession);
                if (!loginHandler.isLoggedOn() && !loginHandler.loggedOnUserStillExists()) {
                    loginHandler.login(getActivity());
                } else {
                    save();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Match match = mMatches.get(mMatchSpinner.getSelectedItemPosition());
        mTeams = DBManager.getInstance(getActivity(), mDaoSession).getTeamsForMatch(match);
        List<String> teamNumbers = new ArrayList<>();
        for (Team team : mTeams) {
            teamNumbers.add(team.getName() + ", " + team.getNumber());
        }
        mAllianceSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, Arrays.copyOf(teamNumbers.toArray(), teamNumbers.size(), String[].class)));
    }

    @SuppressWarnings("unused")
    public void onEvent(ScoutSyncSuccessEvent event) {
        loadAllData(Utilities.ScoutUtil.getScoutEvent(getActivity(), mDaoSession));
    }    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }




}
