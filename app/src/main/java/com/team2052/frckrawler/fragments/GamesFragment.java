package com.team2052.frckrawler.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.GameInfoActivity;
import com.team2052.frckrawler.binding.ListViewNoDataParams;
import com.team2052.frckrawler.binding.RecyclerViewBinder;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.listitems.smart.GameItemView;
import com.team2052.frckrawler.listitems.smart.SmartAdapterInteractions;

import java.util.List;

import io.nlopez.smartadapters.SmartAdapter;
import rx.Observable;

public class GamesFragment extends RecyclerViewFragment<List<Game>, RecyclerViewBinder> {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recycler_view_fab, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.fab).setOnClickListener(ignored -> showAddTeamDialog());
    }

    public void showAddTeamDialog() {
        new MaterialDialog.Builder(getContext())
                .title(R.string.add_game)
                .negativeText(android.R.string.cancel)
                .positiveText(android.R.string.ok)
                .inputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
                .input(R.string.game_name, 0, false, (materialDialog, input) -> {
                    final String name = input.toString();
                    final Game game = new Game(null, name);
                    rxDbManager.getGamesTable().insert(game);
                    refresh();
                })
                .show();
    }

    @Override
    protected ListViewNoDataParams getNoDataParams() {
        return new ListViewNoDataParams("No games found", R.drawable.ic_game);
    }

    @Override
    public void provideAdapterCreator(SmartAdapter.MultiAdaptersCreator creator) {
        creator.map(Game.class, GameItemView.class);
        creator.listener((actionId, item, position, view) -> {
            if (actionId == SmartAdapterInteractions.EVENT_CLICKED && item instanceof Game) {
                Game game = (Game) item;
                startActivity(GameInfoActivity.newInstance(getActivity(), game.getId()));
            }
        });
    }

    @Override
    protected Observable<? extends List<Game>> getObservable() {
        return rxDbManager.allGames();
    }

    @Override
    public void inject() {
        mComponent.inject(this);
    }
}