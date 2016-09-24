package com.team2052.frckrawler.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.GameInfoActivity;
import com.team2052.frckrawler.binding.ListViewNoDataParams;
import com.team2052.frckrawler.binding.RecyclerViewBinder;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.fragments.dialog.AddGameDialogFragment;
import com.team2052.frckrawler.subscribers.GameListSubscriber;
import com.team2052.frckrawler.listitems.smart.SmartAdapterInteractions;
import com.team2052.frckrawler.listitems.smart.GameItemView;

import java.util.List;

import io.nlopez.smartadapters.SmartAdapter;
import rx.Observable;

public class GamesFragment extends RecyclerViewFragment<List<Game>, GameListSubscriber, RecyclerViewBinder> {
    protected FloatingActionButton mFab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recycler_view_fab, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFab = (FloatingActionButton) view.findViewById(R.id.fab);
        /*mListView.setOnItemClickListener((adapterView, view1, i, l) -> {
            startActivity(GameInfoActivity.newInstance(getActivity(), Long.parseLong(((ListElement) adapterView.getAdapter().getItem(i)).getKey())));
        });*/
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
    protected ListViewNoDataParams getNoDataParams() {
        return new ListViewNoDataParams("No games found", R.drawable.ic_game);
    }

    @Override
    public void provideAdapterCreator(SmartAdapter.MultiAdaptersCreator creator) {
        creator.map(Game.class, GameItemView.class);
        creator.listener((actionId, item, position, view) -> {
            if(actionId == SmartAdapterInteractions.EVENT_CLICKED && item instanceof Game){
                Game game = (Game) item;
                startActivity(GameInfoActivity.newInstance(getActivity(), game.getId()));
            }
        });
    }
}