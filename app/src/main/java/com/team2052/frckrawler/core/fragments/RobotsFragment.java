package com.team2052.frckrawler.core.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.team2052.frckrawler.core.activities.DatabaseActivity;
import com.team2052.frckrawler.core.activities.RobotActivity;
import com.team2052.frckrawler.core.adapters.ListViewAdapter;
import com.team2052.frckrawler.core.listitems.ListElement;
import com.team2052.frckrawler.core.listitems.ListItem;
import com.team2052.frckrawler.core.listitems.elements.RobotListElement;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.db.RobotDao;
import com.team2052.frckrawler.db.RobotEvent;
import com.team2052.frckrawler.db.RobotEventDao;
import com.team2052.frckrawler.db.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        b.putLong(DatabaseActivity.PARENT_ID, team.getNumber());
        fragment.setArguments(b);
        return fragment;
    }

    public static RobotsFragment newInstance(Event event) {
        RobotsFragment fragment = new RobotsFragment();
        Bundle b = new Bundle();
        b.putInt(VIEW_TYPE, 1);
        b.putLong(DatabaseActivity.PARENT_ID, event.getId());
        fragment.setArguments(b);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setShowAddAction(false);
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        this.mViewType = b.getInt(VIEW_TYPE, 0);
        mKey = b.getLong(DatabaseActivity.PARENT_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getActivity().startActivity(RobotActivity.newInstance(getActivity(), Long.parseLong(((ListElement) parent.getAdapter().getItem(position)).getKey())));
            }
        });
        return view;
    }

    @Override
    public void updateList() {
        new GetAllRobotsBy().execute();
    }


    public class GetAllRobotsBy extends AsyncTask<Void, Void, List<Robot>> {

        @Override
        protected List<Robot> doInBackground(Void... params) {
            WhereCondition condition = null;
            List<Robot> robots;
            if (mViewType == 0) {
                condition = RobotDao.Properties.TeamId.eq(mKey);
                robots = mDaoSession.getRobotDao().queryBuilder().where(condition).list();
            } else {
                condition = RobotEventDao.Properties.EventId.eq(mKey);
                List<RobotEvent> robotEvents = mDaoSession.getRobotEventDao().queryBuilder().where(condition).list();
                robots = new ArrayList<>();
                for (RobotEvent robotEvent : robotEvents) {
                    robots.add(robotEvent.getRobot());
                }
                Collections.sort(robots, new Comparator<Robot>() {
                    @Override
                    public int compare(Robot robot, Robot robot2) {
                        return Double.compare(robot.getTeamId(), robot2.getTeamId());
                    }
                });
            }
            //Load robots based on the view type
            return robots;
        }

        @Override
        protected void onPostExecute(List<Robot> robots) {
            if (robots.size() == 0) {
                showError(true);
                return;
            }
            showError(false);
            List<ListItem> listItems = new ArrayList<>();
            for (Robot robot : robots) {
                listItems.add(new RobotListElement(robot));
            }
            mListView.setAdapter(new ListViewAdapter(getActivity(), listItems));
        }
    }
}
