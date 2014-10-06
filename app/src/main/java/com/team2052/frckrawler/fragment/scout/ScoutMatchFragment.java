package com.team2052.frckrawler.fragment.scout;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.DatabaseActivity;
import com.team2052.frckrawler.activity.MetricsActivity;
import com.team2052.frckrawler.fragment.BaseFragment;
import com.team2052.frckrawler.view.metric.MetricWidget;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import frckrawler.Event;
import frckrawler.EventDao;
import frckrawler.Match;
import frckrawler.MatchDao;
import frckrawler.Metric;
import frckrawler.MetricDao;

/**
 * @author Adam
 */
public class ScoutMatchFragment extends BaseFragment implements AdapterView.OnItemSelectedListener
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
        mEvent = mDaoSession.getEventDao().load(getArguments().getLong(DatabaseActivity.PARENT_ID));
        mMatchSpinner.setOnItemSelectedListener(this);
        new GetAllMetrics().execute();
        new GetAllMatches().execute();
        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        mAllianceSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, new String[]{}));
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
            return mDaoSession.getMatchDao().queryBuilder().orderAsc(MatchDao.Properties.Number).where(MatchDao.Properties.EventId.eq(mEvent.getId())).list();
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
            QueryBuilder<Metric> metricQueryBuilder = mDaoSession.getMetricDao().queryBuilder();
            metricQueryBuilder.where(MetricDao.Properties.GameId.eq(mEvent.getGame().getId()));
            metricQueryBuilder.where(MetricDao.Properties.Category.eq(MetricsActivity.MetricType.MATCH_PERF_METRICS.ordinal()));
            return metricQueryBuilder.list();
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
            /*Team team = (Team) mAllianceSpinner.getSelectedItem();
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
            new RobotMatchComment(((EditText) getView().findViewById(R.id.comments)).getText().toString(), robot, match).save();
            ActiveAndroid.setTransactionSuccessful();
            ActiveAndroid.endTransaction();
*/
            return 0;
        }

        @Override
        protected void onPostExecute(Integer aVoid)
        {
            Toast.makeText(getActivity(), aVoid == 0 ? "Save Complete!" : "Cannot Save Match Data. Match Is Already Saved", Toast.LENGTH_LONG).show();
        }
    }
}
