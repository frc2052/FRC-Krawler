package com.team2052.frckrawler.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.team2052.frckrawler.ListUpdateListener;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.EventsActivity;
import com.team2052.frckrawler.activity.MetricsActivity;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.models.Game;
import com.team2052.frckrawler.fragment.dialog.AddGameDialogFragment;
import com.team2052.frckrawler.listitems.GameListItem;
import com.team2052.frckrawler.listitems.ListElement;
import com.team2052.frckrawler.listitems.ListItem;

import java.util.ArrayList;
import java.util.List;

public class GamesFragment extends ListFragment implements ListUpdateListener
{

    public int currentSelectedListItem;
    private ActionMode currentActionMode;
    ActionMode.Callback callback = new ActionMode.Callback()
    {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu)
        {
            long gameId = Long.parseLong(((ListElement) mAdapter.getItem(currentSelectedListItem)).getKey());
            Game game = Game.load(Game.class, gameId);
            mode.setTitle(game.name);
            mode.getMenuInflater().inflate(R.menu.edit_delete_game_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu)
        {
            return false;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item)
        {
            long gameId = Long.parseLong(((ListElement) mAdapter.getItem(currentSelectedListItem)).getKey());
            final Game game = Game.load(Game.class, gameId);
            switch (item.getItemId()) {
                case R.id.menu_event:
                    getActivity().startActivity(EventsActivity.newInstance(getActivity(), game));
                    return true;
                case R.id.menu_match_metrics:
                    getActivity().startActivity(MetricsActivity.newInstance(getActivity(), game, MetricsActivity.MetricType.MATCH_PERF_METRICS));
                    return true;
                case R.id.menu_pit_metrics:
                    getActivity().startActivity(MetricsActivity.newInstance(getActivity(), game, MetricsActivity.MetricType.ROBOT_METRICS));
                    return true;
                case R.id.menu_delete:
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Are you sure you want to remove this game and all its data?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            game.delete();
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
        public void onDestroyActionMode(ActionMode mode)
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
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (currentActionMode != null) {
                    return false;
                }
                currentSelectedListItem = position;
                currentActionMode = getActivity().startActionMode(callback);
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
            return DBManager.loadAllGames();
        }

        @Override
        protected void onPostExecute(List<Game> games)
        {
            ArrayList<ListItem> element = new ArrayList<>();
            for (Game game : games)
                element.add(new GameListItem(game));
            mListView.setAdapter(mAdapter = new ListViewAdapter(getActivity(), element));
        }
    }
}
