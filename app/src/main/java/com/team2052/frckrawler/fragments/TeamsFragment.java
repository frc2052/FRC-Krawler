package com.team2052.frckrawler.fragments;

import android.os.Bundle;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.TeamInfoActivity;
import com.team2052.frckrawler.binding.ListViewNoDataParams;
import com.team2052.frckrawler.binding.RecyclerViewBinder;
import com.team2052.frckrawler.db.Team;
import com.team2052.frckrawler.listitems.smart.SmartAdapterInteractions;
import com.team2052.frckrawler.listitems.smart.TeamItemView;
import com.team2052.frckrawler.subscribers.TeamListSubscriber;

import java.util.List;

import io.nlopez.smartadapters.SmartAdapter;
import rx.Observable;

public class TeamsFragment extends RecyclerViewFragment<List<Team>, TeamListSubscriber, RecyclerViewBinder> {
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<? extends List<Team>> getObservable() {
        return rxDbManager.allTeams();
    }

    @Override
    protected ListViewNoDataParams getNoDataParams() {
        return new ListViewNoDataParams("No teams found", R.drawable.ic_team);
    }

    @Override
    public void provideAdapterCreator(SmartAdapter.MultiAdaptersCreator creator) {
        creator.map(Team.class, TeamItemView.class);
        creator.listener((actionId, item, position, view) -> {
            if (actionId == SmartAdapterInteractions.EVENT_CLICKED && item instanceof Team) {
                Team team = (Team) item;
                startActivity(TeamInfoActivity.newInstance(getActivity(), team.getNumber()));
            }
        });
    }
}
