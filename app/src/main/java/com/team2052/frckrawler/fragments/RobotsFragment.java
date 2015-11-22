package com.team2052.frckrawler.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.BaseActivity;
import com.team2052.frckrawler.activities.RobotActivity;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.db.RobotDao;
import com.team2052.frckrawler.db.RobotEvent;
import com.team2052.frckrawler.db.RobotEventDao;
import com.team2052.frckrawler.db.Team;
import com.team2052.frckrawler.listitems.ListElement;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.elements.RobotListElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.greenrobot.dao.query.WhereCondition;

/**
 * @author Adam
 */
public class RobotsFragment extends ListFragment {
    public static final String VIEW_TYPE = "VIEW_TYPE";
    private int mViewType;
    private long mKey;

    //To create a valid instance view by team or by game
    public static RobotsFragment newInstance(Team team) {
        RobotsFragment fragment = new RobotsFragment();
        Bundle b = new Bundle();
        b.putInt(VIEW_TYPE, 0);
        b.putLong(BaseActivity.PARENT_ID, team.getNumber());
        fragment.setArguments(b);
        return fragment;
    }

    public static RobotsFragment newInstance(long event_id) {
        RobotsFragment fragment = new RobotsFragment();
        Bundle b = new Bundle();
        b.putInt(VIEW_TYPE, 1);
        b.putLong(BaseActivity.PARENT_ID, event_id);
        fragment.setArguments(b);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setShowAddAction(false);
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        this.mViewType = b.getInt(VIEW_TYPE, 0);
        mKey = b.getLong(BaseActivity.PARENT_ID);

        if (mViewType == 1) {
            Event event = mDbManager.getEventsTable().load(mKey);
            if (event != null) {
                if (event.getFmsid() == null || !event.getFmsid().equals("null")) {
                    setHasOptionsMenu(true);
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_team_manual, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_team) {
            Event load = mDbManager.getEventsTable().load(mKey);
            if (load != null) {
                AddTeamToEventDialogFragment.newInstance(load).show(getChildFragmentManager(), "addTeam");
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mListView.setOnItemClickListener((parent, view1, position, id) ->
                getActivity().startActivity(RobotActivity.newInstance(getActivity(), Long.parseLong(((ListElement) parent.getAdapter().getItem(position)).getKey()))));
        return view;
    }

    @Override
    public void refresh() {
        new GetAllRobotsBy().execute();
    }


    public class GetAllRobotsBy extends AsyncTask<Void, Void, List<Robot>> {

        @Override
        protected List<Robot> doInBackground(Void... params) {
            WhereCondition condition;
            List<Robot> robots;
            if (mViewType == 0) {
                condition = RobotDao.Properties.Team_id.eq(mKey);
                robots = mDbManager.getRobotsTable().getQueryBuilder().where(condition).list();
            } else {
                condition = RobotEventDao.Properties.Event_id.eq(mKey);
                List<RobotEvent> robotEvents = mDbManager.getRobotEvents().getQueryBuilder().where(condition).list();
                robots = new ArrayList<>();
                for (RobotEvent robotEvent : robotEvents) {
                    robots.add(mDbManager.getRobotEvents().getRobot(robotEvent));
                }
                Collections.sort(robots, (robot, robot2) -> Double.compare(robot.getTeam_id(), robot2.getTeam_id()));
            }
            //Load robots based on the view type
            return robots;
        }

        @Override
        protected void onPostExecute(List<Robot> robots) {
            if (robots.isEmpty()) {
                showError(true);
                return;
            }
            showError(false);
            List<ListItem> listItems = new ArrayList<>();
            for (Robot robot : robots) {
                listItems.add(new RobotListElement(robot, mDbManager.getRobotsTable().getGame(robot)));
            }
            mListView.setAdapter(new ListViewAdapter(getActivity(), listItems));
        }
    }
}
