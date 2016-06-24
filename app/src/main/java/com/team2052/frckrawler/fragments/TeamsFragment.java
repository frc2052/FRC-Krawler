package com.team2052.frckrawler.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.TeamInfoActivity;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.binding.ListViewBinder;
import com.team2052.frckrawler.db.Team;
import com.team2052.frckrawler.listitems.ListElement;
import com.team2052.frckrawler.subscribers.TeamListSubscriber;

import java.util.List;

import rx.Observable;

public class TeamsFragment extends ListViewFragment<List<Team>, TeamListSubscriber> {
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
        return dbManager.allTeams();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView.setOnItemClickListener((parent, view1, position, id) -> {
            Team team = dbManager.getTeamsTable().load(Long.valueOf((((ListElement) ((ListViewAdapter) parent.getAdapter()).getItem(position))).getKey()));
            startActivity(TeamInfoActivity.newInstance(getActivity(), team));
        });
    }


    @Override
    protected ListViewBinder.ListViewNoDataParams getNoDataParams() {
        return new ListViewBinder.ListViewNoDataParams("No teams found", R.drawable.ic_team);
    }
}
