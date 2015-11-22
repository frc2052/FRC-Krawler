package com.team2052.frckrawler.fragments;

import android.os.AsyncTask;
import android.os.Bundle;

import com.team2052.frckrawler.activities.BaseActivity;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.db.RobotEvent;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.elements.EventListElement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam
 * @since 11/24/2014
 */
public class RobotAttendingEventsFragment extends ListFragment {

    private Robot mRobot;

    public static RobotAttendingEventsFragment newInstance(Robot robot) {
        RobotAttendingEventsFragment fragment = new RobotAttendingEventsFragment();
        Bundle args = new Bundle();
        args.putLong(BaseActivity.PARENT_ID, robot.getId());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void refresh() {
        mRobot = mDbManager.getRobotsTable().load(getArguments().getLong(BaseActivity.PARENT_ID));
        new LoadAllEvents().execute();
    }

    private class LoadAllEvents extends AsyncTask<Void, Void, List<ListItem>> {

        List<ListItem> elements = new ArrayList<>();

        @Override
        protected List<ListItem> doInBackground(Void... voids) {
            mDbManager.runInTx(() -> {
                List<RobotEvent> robotEvents = mDbManager.getRobotsTable().getRobotEvents(mRobot);
                for (RobotEvent eveRobot : robotEvents) {
                    elements.add(new EventListElement(mDbManager.getEventsTable().load(eveRobot.getEvent_id())));
                }
            });
            return elements;
        }

        @Override
        protected void onPostExecute(List<ListItem> listItems) {
            mListView.setAdapter(mAdapter = new ListViewAdapter(getActivity(), listItems));
        }
    }
}
