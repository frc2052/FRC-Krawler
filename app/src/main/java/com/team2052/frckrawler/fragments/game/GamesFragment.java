package com.team2052.frckrawler.fragments.game;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.GameInfoActivity;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.fragments.BaseFragment;
import com.team2052.frckrawler.listeners.ListUpdateListener;
import com.team2052.frckrawler.listitems.ListElement;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.elements.GameListElement;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Bind;

public class GamesFragment extends BaseFragment implements ListUpdateListener {
    @Bind(R.id.list_layout)
    protected ListView mListView;

    @Bind(R.id.fab)
    protected FloatingActionButton mFab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_view_fab, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mListView.setOnItemClickListener((adapterView, view1, i, l) -> {
            long gameId = Long.parseLong(((ListElement) adapterView.getAdapter().getItem(i)).getKey());
            final Game game = mDbManager.getGamesTable().load(gameId);
            GamesFragment.this.startActivity(GameInfoActivity.newInstance(getActivity(), game));
        });

        mFab.setOnClickListener(view1 -> new AddGameDialogFragment().show(GamesFragment.this.getChildFragmentManager(), "addGame"));
    }

    @Override
    public void onResume() {
        super.onResume();
        updateList();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        updateList();
    }

    @Override
    public void updateList() {
        new GetGamesTask().execute();
    }

    private class GetGamesTask extends AsyncTask<Void, Void, List<Game>> {
        @Override
        protected List<Game> doInBackground(Void... params) {
            return mDbManager.getGamesTable().loadAll();
        }

        @Override
        protected void onPostExecute(List<Game> games) {
            ArrayList<ListItem> element = new ArrayList<>();
            for (Game game : games)
                element.add(new GameListElement(game));
            mListView.setAdapter(new ListViewAdapter(getActivity(), element));
        }

    }
}
