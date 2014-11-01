package com.team2052.frckrawler.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.team2052.frckrawler.FRCKrawler;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.GameInfoActivity;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.fragment.dialog.AddGameDialogFragment;
import com.team2052.frckrawler.listeners.ListUpdateListener;
import com.team2052.frckrawler.listitems.ListElement;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.elements.GameListElement;

import java.util.ArrayList;
import java.util.List;

import frckrawler.Game;

public class GamesFragment extends ListFragment implements ListUpdateListener
{

    public int currentSelectedListItem;
    private android.support.v7.view.ActionMode currentActionMode;

    android.support.v7.view.ActionMode.Callback callback = new android.support.v7.view.ActionMode.Callback()
    {
        @Override
        public boolean onCreateActionMode(android.support.v7.view.ActionMode mode, Menu menu)
        {
            long gameId = Long.parseLong(((ListElement) mAdapter.getItem(currentSelectedListItem)).getKey());
            Game game = mDaoSession.getGameDao().load(gameId);
            mode.setTitle(game.getName());
            mode.getMenuInflater().inflate(R.menu.edit_delete_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(android.support.v7.view.ActionMode mode, Menu menu)
        {
            return false;
        }

        @Override
        public boolean onActionItemClicked(final android.support.v7.view.ActionMode mode, MenuItem item)
        {
            long gameId = Long.parseLong(((ListElement) mAdapter.getItem(currentSelectedListItem)).getKey());
            final Game game = mDaoSession.getGameDao().load(gameId);
            switch (item.getItemId()) {
                case R.id.menu_delete:
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Are you sure you want to remove this game and all its data?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            mDaoSession.runInTx(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    DBManager.deleteGame(((FRCKrawler) getActivity().getApplication()).getDaoSession(), game);
                                }
                            });
                            dialogInterface.dismiss();
                            updateList();
                            mode.finish();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
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
        public void onDestroyActionMode(android.support.v7.view.ActionMode mode)
        {
            currentActionMode = null;
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.addbutton, menu);
        menu.findItem(R.id.add_action).setIcon(R.drawable.ic_action_new_event);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        currentActionMode = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                long gameId = Long.parseLong(((ListElement) mAdapter.getItem(i)).getKey());
                final Game game = mDaoSession.getGameDao().load(gameId);
                startActivity(GameInfoActivity.newInstance(getActivity(), game));
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (currentActionMode != null) {
                    return false;
                }
                currentSelectedListItem = position;
                currentActionMode = ((ActionBarActivity) getActivity()).startSupportActionMode(callback);
                return true;
            }
        });
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.add_action) {
            AddGameDialogFragment fragment = new AddGameDialogFragment();
            fragment.show(getChildFragmentManager(), "addGame");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateList()
    {
        new GetGamesTask().execute();
    }

    @Override
    public void onDestroy()
    {
        if (currentActionMode != null)
            currentActionMode.finish();
        super.onDestroy();
    }

    private class GetGamesTask extends AsyncTask<Void, Void, List<Game>>
    {
        @Override
        protected List<Game> doInBackground(Void... params)
        {
            return mDaoSession.getGameDao().queryBuilder().list();
        }

        @Override
        protected void onPostExecute(List<Game> games)
        {
            ArrayList<ListItem> element = new ArrayList<>();
            for (Game game : games)
                element.add(new GameListElement(game));
            mListView.setAdapter(mAdapter = new ListViewAdapter(getActivity(), element));
        }
    }
}
