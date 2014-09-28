package com.team2052.frckrawler.fragment;

import android.os.*;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.*;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.*;
import com.team2052.frckrawler.database.models.*;
import com.team2052.frckrawler.gui.MetricWidget;

import java.util.*;

/**
 * @author Adam
 */
public class ScoutMatchFragment extends Fragment implements AdapterView.OnItemSelectedListener
{
    private Event mEvent;
    private Spinner mMatchSpinner;
    private Spinner mAllianceSpinner;

    public static ScoutMatchFragment newInstance(Event event)
    {
        ScoutMatchFragment fragment = new ScoutMatchFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(DatabaseActivity.PARENT_ID, event.getId());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.scout, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_save) {
            if (mAllianceSpinner.getSelectedItem() != null && mMatchSpinner.getSelectedItem() != null) {
                new SaveAllMetrics().execute();
            }
        } /*else if (item.getItemId() == R.id.action_schedule) {
            getActivity().startActivity(MatchListActivity.newInstance(getActivity(), mEvent));
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_scouting_match, null);
        mMatchSpinner = (Spinner) view.findViewById(R.id.match_number);
        mAllianceSpinner = (Spinner) view.findViewById(R.id.team);
        mEvent = Event.load(Event.class, getArguments().getLong(DatabaseActivity.PARENT_ID));
        mMatchSpinner.setOnItemSelectedListener(this);
        new GetAllMetrics().execute();
        new GetAllMatches().execute();
        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
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
    public void onNothingSelected(AdapterView<?> parent)
    {
    }

    public class GetAllMatches extends AsyncTask<Void, Void, List<Match>>
    {

        @Override
        protected List<Match> doInBackground(Void... params)
        {
            return new Select().from(Match.class).where("Event = ?", mEvent.getId()).orderBy("MatchNumber ASC").execute();
        }

        @Override
        protected void onPostExecute(List<Match> matches)
        {
            mMatchSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, matches));
        }
    }

    public class GetAllMetrics extends AsyncTask<Void, Void, List<Metric>>
    {

        @Override
        protected List<Metric> doInBackground(Void... params)
        {
            return new Select().from(Metric.class).where("Game = ?", mEvent.game.getId()).and("Category = ?", MetricsActivity.MetricType.MATCH_PERF_METRICS.ordinal()).execute();
        }

        @Override
        protected void onPostExecute(List<Metric> metrics)
        {
            ((LinearLayout) getView().findViewById(R.id.metricWidgetList)).removeAllViews();
            for (Metric metric : metrics) {
                ((LinearLayout) getView().findViewById(R.id.metricWidgetList)).addView(MetricWidget.createWidget(getActivity(), metric));
            }
        }
    }

    public class SaveAllMetrics extends AsyncTask<Void, Void, Integer>
    {

        @Override
        protected Integer doInBackground(Void... params)
        {
            //Get data from view
            Team team = (Team) mAllianceSpinner.getSelectedItem();
            Match match = (Match) mMatchSpinner.getSelectedItem();
            Robot robot = new Select().from(Robot.class).where("Team = ?", team.getId()).and("Game = ?", mEvent.game.getId()).executeSingle();

            if (new Select().from(MetricMatchData.class).where("Robot = ?", robot.getId()).and("Match = ?", match.getId()).execute().size() > 0) {
                return 1;
            }

            LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.metricWidgetList);
            List<MetricWidget> widgets = new ArrayList<>();

            for (int i = 0; i < linearLayout.getChildCount(); i++) {
                widgets.add((MetricWidget) linearLayout.getChildAt(i));
            }

            //Begin Saving
            ActiveAndroid.beginTransaction();
            for (MetricWidget widget : widgets) {
                new MetricMatchData(robot, widget.getMetric(), widget.getMetricValue(), match).save();
            }
            new MatchComments(match, robot, ((EditText) getView().findViewById(R.id.comments)).getText().toString()).save();
            ActiveAndroid.setTransactionSuccessful();
            ActiveAndroid.endTransaction();

            return 0;
        }

        @Override
        protected void onPostExecute(Integer aVoid)
        {
            Toast.makeText(getActivity(), aVoid == 0 ? "Save Complete!" : "Cannot Save Match Data. Match Is Already Saved", Toast.LENGTH_LONG).show();
        }
    }
}
