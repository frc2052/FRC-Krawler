package com.team2052.frckrawler.fragments.robot;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.DatabaseActivity;
import com.team2052.frckrawler.activities.RobotActivity;
import com.team2052.frckrawler.adapters.items.ListElement;
import com.team2052.frckrawler.di.binding.NoDataParams;
import com.team2052.frckrawler.di.subscribers.RobotListSubscriber;
import com.team2052.frckrawler.fragments.ListViewFragment;
import com.team2052.frckrawler.fragments.dialog.AddTeamToEventDialogFragment;
import com.team2052.frckrawler.models.Event;
import com.team2052.frckrawler.models.Robot;

import java.util.List;

import rx.Observable;

/**
 * Generic Fragment list for viewing robots from either a single team, or in an event
 * @author Adam
 */
public class RobotsFragment extends ListViewFragment<List<Robot>, RobotListSubscriber> implements View.OnClickListener {
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
        return mViewType == 0 ? rxDbManager.robotsWithTeam(mKey) : rxDbManager.robotsAtEvent(mKey);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView.setOnItemClickListener((parent, view1, position, id) ->
                getActivity().startActivity(RobotActivity.newInstance(getActivity(), Long.parseLong(((ListElement) parent.getAdapter().getItem(position)).getKey()))));
    }

    @Override
    protected NoDataParams getNoDataParams() {
        return new NoDataParams("No teams found", R.drawable.ic_team);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.floating_action_button) {
            Event load = rxDbManager.getEventsTable().load(mKey);
            if (load != null) {
                AddTeamToEventDialogFragment.newInstance(load).show(getChildFragmentManager(), "addTeam");
            }
        }
    }
}
