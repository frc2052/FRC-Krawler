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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.DatabaseActivity;
import com.team2052.frckrawler.activity.MetricsActivity;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.database.models.Event;
import com.team2052.frckrawler.database.models.Metric;
import com.team2052.frckrawler.database.models.MetricPitData;
import com.team2052.frckrawler.database.models.Robot;
import com.team2052.frckrawler.database.models.RobotEvents;
import com.team2052.frckrawler.database.models.Team;
import com.team2052.frckrawler.gui.MetricWidget;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.SimpleListElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Adam
 */
public class ScoutPitFragment extends Fragment
{
    private Event mEvent;
    private Spinner mTeamSpinner;


    public static ScoutPitFragment newInstance(Event event)
    {
        ScoutPitFragment fragment = new ScoutPitFragment();
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
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_save) {
            if (mTeamSpinner.getSelectedItem() != null) {
                new SaveAllMetrics().execute();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.scout, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_scouting_pit, null);
        mTeamSpinner = (Spinner) view.findViewById(R.id.team);
        mEvent = Event.load(Event.class, getArguments().getLong(DatabaseActivity.PARENT_ID));
        new GetAllMetrics().execute();
        new GetAllRobots().execute();
        return view;
    }

    public class GetAllRobots extends AsyncTask<Void, Void, List<RobotEvents>>
    {
        @Override
        protected List<RobotEvents> doInBackground(Void... params)
        {
            return new Select().from(RobotEvents.class).where("Event = ?", mEvent.getId()).and("Attending = ?", true).execute();
        }

        @Override
        protected void onPostExecute(List<RobotEvents> robotEventses)
        {
            //Sort by Team number
            Collections.sort(robotEventses, new Comparator<RobotEvents>()
            {
                @Override
                public int compare(RobotEvents lhs, RobotEvents rhs)
                {
                    return Double.compare(lhs.robot.team.number, rhs.robot.team.number);
                }
            });

            List<ListItem> listItems = new ArrayList<>();

            for (RobotEvents robotEvents : robotEventses) {
                listItems.add(new SimpleListElement(Integer.toString(robotEvents.robot.team.number) + " - " + robotEvents.robot.team.name, robotEvents.robot.team.teamKey));
            }
            mTeamSpinner.setAdapter(new ListViewAdapter(getActivity(), listItems));
        }
    }

    public class GetAllMetrics extends AsyncTask<Void, Void, List<Metric>>
    {

        @Override
        protected List<Metric> doInBackground(Void... params)
        {
            return new Select().from(Metric.class).where("Game = ?", mEvent.game.getId()).and("Category = ?", MetricsActivity.MetricType.ROBOT_METRICS.ordinal()).execute();
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
            Team team = (Team) mTeamSpinner.getSelectedItem();
            Robot robot = new Select().from(Robot.class).where("Team = ?", team.getId()).and("Game = ?", mEvent.game.getId()).executeSingle();

            LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.metricWidgetList);
            List<MetricWidget> widgets = new ArrayList<>();

            for (int i = 0; i < linearLayout.getChildCount(); i++) {
                widgets.add((MetricWidget) linearLayout.getChildAt(i));
            }

            //Begin Saving
            ActiveAndroid.beginTransaction();
            for (MetricWidget widget : widgets) {
                new MetricPitData(robot, widget.getMetric(), widget.getMetricValue()).save();
            }
            robot.comments = ((EditText) getView().findViewById(R.id.comments)).getText().toString();
            robot.save();
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
