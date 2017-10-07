package com.team2052.frckrawler.fragments.robot;

import android.os.Bundle;

import com.team2052.frckrawler.activities.DatabaseActivity;
import com.team2052.frckrawler.activities.RobotEventActivity;
import com.team2052.frckrawler.adapters.items.smart.EventItemView;
import com.team2052.frckrawler.adapters.items.smart.SmartAdapterInteractions;
import com.team2052.frckrawler.di.binding.RecyclerViewBinder;
import com.team2052.frckrawler.fragments.RecyclerViewFragment;
import com.team2052.frckrawler.models.Event;

import java.util.List;

import io.nlopez.smartadapters.SmartAdapter;
import rx.Observable;

/**
 * @author Adam
 * @since 11/24/2014
 */
public class RobotAttendingEventsFragment extends RecyclerViewFragment<List<Event>, RecyclerViewBinder> {

    private long robot_id;

    public static RobotAttendingEventsFragment newInstance(long robot_id) {
        RobotAttendingEventsFragment fragment = new RobotAttendingEventsFragment();
        Bundle args = new Bundle();
        args.putLong(DatabaseActivity.Companion.getPARENT_ID(), robot_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        robot_id = getArguments().getLong(DatabaseActivity.Companion.getPARENT_ID());
        super.onCreate(savedInstanceState);
    }

    @Override
    public void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<? extends List<Event>> getObservable() {
        return rxDbManager.robotAtEvents(robot_id);
    }

    @Override
    public void provideAdapterCreator(SmartAdapter.MultiAdaptersCreator creator) {
        creator.map(Event.class, EventItemView.class);
        creator.listener((actionId, item, position, view) -> {
            if (actionId == SmartAdapterInteractions.EVENT_CLICKED && item instanceof Event) {
                Event event = (Event) item;
                startActivity(RobotEventActivity.Companion.newInstance(getContext(), robot_id, event.getId()));
            }
        });
    }
}
