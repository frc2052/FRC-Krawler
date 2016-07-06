package com.team2052.frckrawler.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.DatabaseActivity;
import com.team2052.frckrawler.activities.RobotActivity;
import com.team2052.frckrawler.binding.ListViewBinder;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.fragments.dialog.AddTeamToEventDialogFragment;
import com.team2052.frckrawler.listeners.FABButtonListener;
import com.team2052.frckrawler.listitems.ListElement;
import com.team2052.frckrawler.subscribers.RobotListSubscriber;

import java.util.List;

import rx.Observable;

/**
 * @author Adam
 */
public class RobotsFragment extends ListViewFragment<List<Robot>, RobotListSubscriber> implements FABButtonListener {
    public static final String VIEW_TYPE = "VIEW_TYPE";
    private int mViewType;
    private long mKey;

    //To create a valid instance view by team or by game
    public static RobotsFragment newTeamInstance(long team_id) {
        RobotsFragment fragment = new RobotsFragment();
        Bundle b = new Bundle();
        b.putInt(VIEW_TYPE, 0);
        b.putLong(DatabaseActivity.PARENT_ID, team_id);
        fragment.setArguments(b);
        return fragment;
    }

    public static RobotsFragment newEventInstance(long event_id) {
        RobotsFragment fragment = new RobotsFragment();
        Bundle b = new Bundle();
        b.putInt(VIEW_TYPE, 1);
        b.putLong(DatabaseActivity.PARENT_ID, event_id);
        fragment.setArguments(b);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        this.mViewType = b.getInt(VIEW_TYPE, 0);
        mKey = b.getLong(DatabaseActivity.PARENT_ID);
    }

    @Override
    public void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<? extends List<Robot>> getObservable() {
        return mViewType == 0 ? dbManager.robotsWithTeam(mKey) : dbManager.robotsAtEvent(mKey);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView.setOnItemClickListener((parent, view1, position, id) ->
                getActivity().startActivity(RobotActivity.newInstance(getActivity(), Long.parseLong(((ListElement) parent.getAdapter().getItem(position)).getKey()))));
    }

    @Override
    public void onFABPressed() {
        Event load = dbManager.getEventsTable().load(mKey);
        if (load != null) {
            AddTeamToEventDialogFragment.newInstance(load).show(getChildFragmentManager(), "addTeam");
        }
    }

    @Override
    protected ListViewBinder.ListViewNoDataParams getNoDataParams() {
        return new ListViewBinder.ListViewNoDataParams("No teams found", R.drawable.ic_team);
    }
}
