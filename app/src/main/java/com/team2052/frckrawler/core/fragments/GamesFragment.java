package com.team2052.frckrawler.core.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.core.activities.GameInfoActivity;
import com.team2052.frckrawler.core.adapters.ListViewAdapter;
import com.team2052.frckrawler.core.database.DBManager;
import com.team2052.frckrawler.core.fragments.dialog.AddGameDialogFragment;
import com.team2052.frckrawler.core.listeners.ListUpdateListener;
import com.team2052.frckrawler.core.listitems.ListElement;
import com.team2052.frckrawler.core.listitems.ListItem;
import com.team2052.frckrawler.core.listitems.elements.GameListElement;
import com.team2052.frckrawler.db.Game;

import java.util.ArrayList;
import java.util.List;

public class GamesFragment extends ListFragmentFab implements ListUpdateListener {

    public int currentSelectedListItem;
    ActionMode currentActionMode;

    ActionMode.Callback callback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
            long gameId = Long.parseLong(((ListElement) mAdapter.getItem(currentSelectedListItem)).getKey());
            Game game = mDbManager.getDaoSession().getGameDao().load(gameId);
            mode.setTitle(game.getName());
            mode.getMenuInflater().inflate(R.menu.edit_delete_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(final android.support.v7.view.ActionMode mode, MenuItem item) {
            long gameId = Long.parseLong(((ListElement) mAdapter.getItem(currentSelectedListItem)).getKey());
            final Game game = mDbManager.getDaoSession().getGameDao().load(gameId);
            switch (item.getItemId()) {
                case R.id.menu_delete:
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Are you sure you want to remove this game and all its data?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mDbManager.getDaoSession().runInTx(new Runnable() {
                                @Override
                                public void run() {
                                    DBManager.getInstance(getActivity(), mDbManager.getDaoSession()).deleteGame(game);
                                }
                            });
                            dialogInterface.dismiss();
                            updateList();
                            mode.finish();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(android.support.v7.view.ActionMode mode) {
            currentActionMode = null;
        }
    };

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView.setOnItemClickListener((adapterView, view1, i, l) -> {
            long gameId = Long.parseLong(((ListElement) mAdapter.getItem(i)).getKey());
            final Game game = mDbManager.getDaoSession().getGameDao().load(gameId);
            startActivity(GameInfoActivity.newInstance(getActivity(), game));
        });
        mListView.setOnItemLongClickListener((parent, view1, position, id) -> {
            if (currentActionMode != null) {
                return false;
            }
            currentSelectedListItem = position;
            currentActionMode = ((ActionBarActivity) getActivity()).startSupportActionMode(callback);
            return true;
        });
        mFab.setOnClickListener(view1 -> new AddGameDialogFragment().show(getChildFragmentManager(), "addGame"));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        currentActionMode = null;
    }

    @Override
    public void onDestroy() {
        if (currentActionMode != null)
            currentActionMode.finish();
        super.onDestroy();
    }

    @Override
    public void updateList() {
        new GetGamesTask().execute();
    }

    private class GetGamesTask extends AsyncTask<Void, Void, List<Game>> {
        @Override
        protected List<Game> doInBackground(Void... params) {
            return mDbManager.getDaoSession().getGameDao().queryBuilder().list();
        }

        @Override
        protected void onPostExecute(List<Game> games) {
            ArrayList<ListItem> element = new ArrayList<>();
            for (Game game : games)
                element.add(new GameListElement(game));
            mListView.setAdapter(mAdapter = new ListViewAdapter(getActivity(), element));
        }
    }
}
