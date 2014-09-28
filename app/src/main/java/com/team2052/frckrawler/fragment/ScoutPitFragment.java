package com.team2052.frckrawler.fragment;

import android.os.*;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.*;

import com.activeandroid.query.Select;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.*;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.database.models.*;
import com.team2052.frckrawler.gui.MetricWidget;
import com.team2052.frckrawler.listitems.*;

import java.util.*;

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
}
