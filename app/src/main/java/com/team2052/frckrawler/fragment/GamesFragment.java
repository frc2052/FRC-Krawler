package com.team2052.frckrawler.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.activeandroid.query.Select;
import com.team2052.frckrawler.ListUpdateListener;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.database.models.Game;
import com.team2052.frckrawler.fragment.dialog.AddGameDialogFragment;
import com.team2052.frckrawler.listitems.GameListItem;
import com.team2052.frckrawler.listitems.ListItem;

import java.util.ArrayList;
import java.util.List;

public class GamesFragment extends ListFragment implements ListUpdateListener {

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.addbutton, menu);
        menu.findItem(R.id.add_action).setIcon(R.drawable.ic_action_new_event);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_action) {
            AddGameDialogFragment fragment = new AddGameDialogFragment();
            fragment.show(getChildFragmentManager(), "addGame");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateList() {
        new GetGamesTask().execute();
    }

    private class GetGamesTask extends AsyncTask<Void, Void, List<Game>> {


        @Override
        protected List<Game> doInBackground(Void... params) {
            return new Select().from(Game.class).execute();
        }

        @Override
        protected void onPostExecute(List<Game> games) {
            ArrayList<ListItem> element = new ArrayList<>();
            for (Game game : games) {
                element.add(new GameListItem(game));
            }
            mListView.setAdapter(mAdapter = new ListViewAdapter(getActivity(), element));
        }
    }
}
