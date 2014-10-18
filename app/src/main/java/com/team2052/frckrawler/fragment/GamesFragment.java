package com.team2052.frckrawler.fragment;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.GameInfoActivity;
import com.team2052.frckrawler.adapters.ListViewAdapter;
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
    private ActionMode currentActionMode;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            inflater.inflate(R.menu.addbutton, menu);
            menu.findItem(R.id.add_action).setIcon(R.drawable.ic_action_new_event);
        }
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
