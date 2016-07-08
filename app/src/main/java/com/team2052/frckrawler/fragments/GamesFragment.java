package com.team2052.frckrawler.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.GameInfoActivity;
import com.team2052.frckrawler.binding.ListViewBinder;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.fragments.dialog.AddGameDialogFragment;
import com.team2052.frckrawler.listitems.ListElement;
import com.team2052.frckrawler.subscribers.GameListSubscriber;

import java.util.List;

import rx.Observable;

public class GamesFragment extends ListViewFragment<List<Game>, GameListSubscriber> {
    protected FloatingActionButton mFab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_view_fab, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFab = (FloatingActionButton) view.findViewById(R.id.fab);
        mListView.setOnItemClickListener((adapterView, view1, i, l) -> {
            startActivity(GameInfoActivity.newInstance(getActivity(), Long.parseLong(((ListElement) adapterView.getAdapter().getItem(i)).getKey())));
        });
        mFab.setOnClickListener(view1 -> new AddGameDialogFragment().show(GamesFragment.this.getChildFragmentManager(), "addGame"));
    }

    @Override
    public void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<? extends List<Game>> getObservable() {
        return dbManager.allGames();
    }

    @Override
    protected ListViewBinder.ListViewNoDataParams getNoDataParams() {
        return new ListViewBinder.ListViewNoDataParams("No games found", R.drawable.ic_game);
    }
}