package com.team2052.frckrawler.fragments;

import android.os.Bundle;

import com.team2052.frckrawler.activities.BaseActivity;
import com.team2052.frckrawler.database.subscribers.EventListSubscriber;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Robot;

import java.util.List;

import rx.Observable;

/**
 * @author Adam
 * @since 11/24/2014
 */
public class RobotAttendingEventsFragment extends ListViewFragment<List<Event>, EventListSubscriber> {

    private long robot_id;

    public static RobotAttendingEventsFragment newInstance(long robot_id) {
        RobotAttendingEventsFragment fragment = new RobotAttendingEventsFragment();
        Bundle args = new Bundle();
        args.putLong(BaseActivity.PARENT_ID, robot_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        robot_id = getArguments().getLong(BaseActivity.PARENT_ID);
    }

    @Override
    public void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<? extends List<Event>> getObservable() {
        return dbManager.robotAtEvents(robot_id);
    }
}
