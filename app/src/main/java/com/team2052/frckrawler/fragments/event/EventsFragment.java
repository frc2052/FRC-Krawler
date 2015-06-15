package com.team2052.frckrawler.fragments.event;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.BaseActivity;
import com.team2052.frckrawler.activities.EventInfoActivity;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.fragments.ListFragment;
import com.team2052.frckrawler.fragments.event.dialog.ImportDataSimpleDialogFragment;
import com.team2052.frckrawler.listeners.FABButtonListener;
import com.team2052.frckrawler.listitems.ListElement;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.elements.EventListElement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam
 * @since 10/15/2014
 */
public class EventsFragment extends ListFragment implements FABButtonListener {
    private Game mGame;
    private int mCurrentSelectedItem;
    private ActionMode mCurrentActionMode;

    public static EventsFragment newInstance(Game game) {
        EventsFragment fragment = new EventsFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(BaseActivity.PARENT_ID, game.getId());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void preUpdateList() {
        mListView.setOnItemClickListener((adapterView, view, i, l) -> startActivity(EventInfoActivity.newInstance(getActivity(), mDbManager.mEvents.load(Long.valueOf(((ListElement) mAdapter.getItem(i)).getKey())))));
        mGame = mDbManager.mGames.load(getArguments().getLong(BaseActivity.PARENT_ID, 0));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.event_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_event) {
            AddEventDialogFragment.newInstance(mGame).show(getChildFragmentManager(), "addEventDialog");
        }
        return false;
    }

    @Override
    public void updateList() {
        new GetEventsTask().execute();
    }

    @Override
    public void onFABPressed() {
        ImportDataSimpleDialogFragment.newInstance(mGame).show(getChildFragmentManager(), "importEvent");
    }

    private class GetEventsTask extends AsyncTask<Void, Void, List<Event>> {

        @Override
        protected List<Event> doInBackground(Void... params) {
            return mDbManager.mGames.getEvents(mGame);
        }

        @Override
        protected void onPostExecute(List<Event> events) {
            ArrayList<ListItem> eventList = new ArrayList<>();
            for (Event event : events) {
                eventList.add(new EventListElement(event));
            }
            mListView.setAdapter(mAdapter = new ListViewAdapter(getActivity(), eventList));
        }
    }
}
