package com.team2052.frckrawler.fragments.scout;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.team2052.frckrawler.database.consumer.SpinnerConsumer;
import com.team2052.frckrawler.database.subscribers.RobotStringSubscriber;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.fragments.BaseDataFragment;

import java.util.List;

import rx.Observable;

/**
 * Created by Adam on 11/26/2015.
 */
public abstract class BaseScoutFragment extends BaseDataFragment<List<Robot>, List<String>, RobotStringSubscriber, SpinnerConsumer> {
    protected Event mEvent;
    public static final String EVENT_ID = "EVENT_ID";

    @Override
    public abstract void inject();

    @Override
    protected Observable<? extends List<Robot>> getObservable() {
        return dbManager.robotsAtEvent(mEvent.getId());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEvent = dbManager.getEventsTable().load(getArguments().getLong(EVENT_ID));
    }

    public List<Robot> getRobots() {
        return subscriber.getData();
    }

    @Nullable
    public Robot getRobot() {
        return subscriber.getData().get(binder.mSpinner.getSelectedItemPosition());
    }

    public boolean isSelectedRobotValid() {
        return getRobot() != null;
    }
}
