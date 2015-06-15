package com.team2052.frckrawler.background.scout;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.MetricValue;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.RobotEvent;
import com.team2052.frckrawler.db.Team;
import com.team2052.frckrawler.fragments.scout.ScoutPitFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Adam
 * @since 12/28/2014.
 */
public class PopulatePitRobotsTask extends AsyncTask<Void, Void, Void> {
    private final ScoutPitFragment mFragment;
    private final DBManager dbManager;
    private final Event mEvent;
    private final Context context;
    private ArrayList<MetricValue> mMetricWidgets;
    private String[] mRobotListStrings;
    private List<RobotEvent> mRobots;

    public PopulatePitRobotsTask(ScoutPitFragment fragment, Event event) {
        this.mFragment = fragment;
        this.context = fragment.getActivity();
        this.mEvent = event;
        this.dbManager = DBManager.getInstance(context);
    }

    @Override
    protected Void doInBackground(Void... params) {
        mRobots = dbManager.mEvents.getRobotEvents(mEvent);

        //Sort the robot numbers
        Collections.sort(mRobots, (lhs, rhs) -> Double.compare(dbManager.mRobotEvents.getTeam(lhs).getNumber(), dbManager.mRobotEvents.getTeam(rhs).getNumber()));

        ArrayList<String> robots = new ArrayList<>();

        for (RobotEvent robotEvent : mRobots) {
            Team team = dbManager.mRobotEvents.getTeam(robotEvent);
            robots.add(team.getNumber() + ", " + team.getName());
        }

        mRobotListStrings = robots.toArray(new String[robots.size()]);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mFragment.mRobots = mRobots;
        mFragment.mTeamSpinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, mRobotListStrings));
        mFragment.setErrorVisible(false);
    }
}
