package com.team2052.frckrawler.fragments.event;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.databinding.FragmentEventInfoBinding;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.fragments.BaseFragment;
import com.team2052.frckrawler.listeners.ListUpdateListener;

/**
 * Created by adam on 6/15/15.
 */
public class EventInfoFragment extends BaseFragment implements ListUpdateListener {
    public static final String EVENT_ID = "EVENT_ID";
    private FragmentEventInfoBinding binding;
    private Event mEvent;

    public static EventInfoFragment newInstance(Event event) {
        Bundle args = new Bundle();
        args.putLong(EVENT_ID, event.getId());
        EventInfoFragment fragment = new EventInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mEvent = mDbManager.getEventsTable().load(getArguments().getLong(EVENT_ID));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_info, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentEventInfoBinding.bind(view);
        updateList();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_delete_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateList() {
        new LoadEventInfo().execute();
    }

    public class LoadEventInfo extends AsyncTask<Void, Void, Void> {
        int numOfTeams = 0;
        int numOfMatches = 0;
        int numOfPitData = 0;
        int numOfMatchData = 0;

        @Override
        protected Void doInBackground(Void... params) {
            numOfTeams = mDbManager.getEventsTable().getRobotEvents(mEvent).size();
            numOfMatches = mDbManager.getEventsTable().getMatches(mEvent).size();
            numOfPitData = mDbManager.getEventsTable().getPitData(mEvent).size();
            numOfMatchData = mDbManager.getEventsTable().getMatchData(mEvent).size();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            binding.setNumOfTeams(numOfTeams);
            binding.setNumOfMatches(numOfMatches);
            binding.setNumOfPitData(numOfPitData);
            binding.setNumOfMatchData(numOfMatchData);
        }
    }
}
