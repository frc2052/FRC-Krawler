package com.team2052.frckrawler.fragment;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.activeandroid.query.Select;
import com.team2052.frckrawler.GlobalValues;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.DatabaseActivity;
import com.team2052.frckrawler.activity.MetricsActivity;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.database.models.Event;
import com.team2052.frckrawler.database.models.Metric;
import com.team2052.frckrawler.database.models.RobotEvents;
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
public class ScoutPitFragment extends Fragment {
    private Event mEvent;
    private Spinner mTeamSpinner;


    public static ScoutPitFragment newInstance(Event event) {
        ScoutPitFragment fragment = new ScoutPitFragment();
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
        View view = inflater.inflate(R.layout.fragment_scouting_pit, null);
        mTeamSpinner = (Spinner) view.findViewById(R.id.team);
        new GetAllMetrics().execute();
        new GetAllRobots().execute();
        return view;
    }

    public class GetAllRobots extends AsyncTask<Void, Void, List<RobotEvents>> {
        @Override
        protected List<RobotEvents> doInBackground(Void... params) {
            return new Select().from(RobotEvents.class).where("Event = ?", mEvent.getId()).and("Attending = ?", true).execute();
        }

        @Override
        protected void onPostExecute(List<RobotEvents> robotEventses) {
            //Sort by Team number
            Collections.sort(robotEventses, new Comparator<RobotEvents>() {
                @Override
                public int compare(RobotEvents lhs, RobotEvents rhs) {
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

    public class GetAllMetrics extends AsyncTask<Void, Void, List<Metric>> {

        @Override
        protected List<Metric> doInBackground(Void... params) {
            return new Select().from(Metric.class).where("Game = ?", mEvent.game.getId()).and("Category = ?", MetricsActivity.MetricType.ROBOT_METRICS.ordinal()).execute();
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
