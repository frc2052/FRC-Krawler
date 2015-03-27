package com.team2052.frckrawler.core.fragments;

import android.os.AsyncTask;
import android.os.Bundle;

import com.team2052.frckrawler.core.activities.DatabaseActivity;
import com.team2052.frckrawler.core.adapters.ListViewAdapter;
import com.team2052.frckrawler.core.listitems.ListItem;
import com.team2052.frckrawler.core.listitems.elements.EventListElement;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.db.RobotEvent;
import com.team2052.frckrawler.db.RobotEventDao;

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
        args.putLong(DatabaseActivity.PARENT_ID, robot.getId());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void updateList() {
        mRobot = mDaoSession.getRobotDao().load(getArguments().getLong(DatabaseActivity.PARENT_ID));
        new LoadAllEvents().execute();
    }

    private class LoadAllEvents extends AsyncTask<Void, Void, List<ListItem>> {

        List<ListItem> elements = new ArrayList<>();

        @Override
        protected List<ListItem> doInBackground(Void... voids) {
            mDaoSession.runInTx(new Runnable() {
                @Override
                public void run() {
                    List<RobotEvent> robotEvents = mDaoSession.getRobotEventDao().queryBuilder().where(RobotEventDao.Properties.RobotId.eq(mRobot.getId())).list();
                    for (RobotEvent eveRobot : robotEvents) {
                        //elements.add(new EventListElement(eveRobot.getEvent()));
                    }
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
