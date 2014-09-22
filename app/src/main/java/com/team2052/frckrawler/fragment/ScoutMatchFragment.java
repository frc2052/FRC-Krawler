package com.team2052.frckrawler.fragment;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.activeandroid.query.Select;
import com.team2052.frckrawler.GlobalValues;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.DatabaseActivity;
import com.team2052.frckrawler.activity.MetricsActivity;
import com.team2052.frckrawler.database.models.Alliance;
import com.team2052.frckrawler.database.models.Event;
import com.team2052.frckrawler.database.models.Match;
import com.team2052.frckrawler.database.models.Metric;
import com.team2052.frckrawler.database.models.Team;
import com.team2052.frckrawler.gui.MetricWidget;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam
 */
public class ScoutMatchFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private Event mEvent;
    private Spinner mMatchSpinner;
    private Spinner mAllianceSpinner;

    public static ScoutMatchFragment newInstance(Event event) {
        ScoutMatchFragment fragment = new ScoutMatchFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(DatabaseActivity.PARENT_ID, event.getId());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getActivity().getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
        /*Long eventId = preferences.getLong(GlobalValues.CURRENT_SCOUT_EVENT_ID, -1);
        if (eventId == -1) {
            try {
                throw new Exception("Event Id Can't be -1 Try Resyncing");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
        mEvent = Event.load(Event.class, getArguments().getLong(DatabaseActivity.PARENT_ID));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scouting_match, null);
        mMatchSpinner = (Spinner) view.findViewById(R.id.match_number);
        mAllianceSpinner = (Spinner) view.findViewById(R.id.team);
        mMatchSpinner.setOnItemSelectedListener(this);
        new GetAllMetrics().execute();
        new GetAllMatches().execute();
        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Alliance alliance = ((Match) mMatchSpinner.getSelectedItem()).alliance;
        List<Team> teams = new ArrayList<>();
        teams.add(alliance.blue1);
        teams.add(alliance.blue2);
        teams.add(alliance.blue3);
        teams.add(alliance.red1);
        teams.add(alliance.red2);
        teams.add(alliance.red3);
        mAllianceSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, teams));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public class GetAllMatches extends AsyncTask<Void, Void, List<Match>> {

        @Override
        protected List<Match> doInBackground(Void... params) {
            return new Select().from(Match.class).where("Event = ?", mEvent.getId()).orderBy("MatchNumber ASC").execute();
        }

        @Override
        protected void onPostExecute(List<Match> matches) {
            mMatchSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, matches));
        }
    }

    public class GetAllMetrics extends AsyncTask<Void, Void, List<Metric>> {

        @Override
        protected List<Metric> doInBackground(Void... params) {
            return new Select().from(Metric.class).where("Game = ?", mEvent.game.getId()).and("Category = ?", MetricsActivity.MetricType.MATCH_PERF_METRICS.ordinal()).execute();
        }

        @Override
        protected void onPostExecute(List<Metric> metrics) {
            ((LinearLayout) getView().findViewById(R.id.metricWidgetList)).removeAllViews();
            for (Metric metric : metrics) {
                ((LinearLayout) getView().findViewById(R.id.metricWidgetList)).addView(MetricWidget.createWidget(getActivity(), metric));
            }
        }
    }
}
