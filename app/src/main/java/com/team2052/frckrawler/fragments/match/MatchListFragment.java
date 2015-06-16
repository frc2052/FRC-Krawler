package com.team2052.frckrawler.fragments.match;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.BaseActivity;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Match;
import com.team2052.frckrawler.db.MatchDao;
import com.team2052.frckrawler.fragments.ListFragment;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.items.MatchListItem;
import com.team2052.frckrawler.tba.HTTP;
import com.team2052.frckrawler.tba.JSON;
import com.team2052.frckrawler.tba.TBA;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam
 */
public class MatchListFragment extends ListFragment {

    private Event mEvent;
    private List<Match> mMatches;

    public static MatchListFragment newInstance(Event event) {
        MatchListFragment fragment = new MatchListFragment();
        Bundle b = new Bundle();
        b.putLong(BaseActivity.PARENT_ID, event.getId());
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.match_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.update_scores) {
            new GetMatchScores().execute();
        } else if (item.getItemId() == R.id.update_schedule) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Are you sure?");
            builder.setMessage("You will lose all your data.");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    UpdateMatchesProcessDialog.newInstance(mEvent).show(getChildFragmentManager(), "matchUpdateDialog");
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.create().show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setShowAddAction(false);
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPostCreate() {
        mEvent = mDbManager.getEventsTable().load(getArguments().getLong(BaseActivity.PARENT_ID));
    }

    @Override
    public void updateList() {
        new GetMatches().execute();
    }

    public class GetMatches extends AsyncTask<Void, Void, List<Match>> {

        @Override
        protected List<Match> doInBackground(Void... params) {
            //Get Matches ascending from the provided event id
            return mDbManager.getMatchesTable().query(null, null, mEvent.getId(), null).orderAsc(MatchDao.Properties.Number).list();
        }

        @Override
        protected void onPostExecute(List<Match> matches) {
            mMatches = matches;
            if (matches.size() == 0) {
                showError(true);
                return;
            }

            showError(false);
            List<ListItem> listItems = new ArrayList<>();

            for (Match match : matches) {
                listItems.add(new MatchListItem(match));
            }
            mListView.setAdapter(mAdapter = new ListViewAdapter(getActivity(), listItems));
        }
    }

    /**
     * Update match scores
     */
    public class GetMatchScores extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            //Check if the event is hosted by TBA if not don't update
            if (mEvent.getFmsid() != null) {
                String url = String.format(TBA.EVENT, mEvent.getFmsid());
                final JsonArray jMatches = JSON.getAsJsonArray(HTTP.dataFromResponse(HTTP.getResponse(url + "/matches")));
                JSON.set_daoSession(mDbManager);
                for (JsonElement element : jMatches) {
                    Match match = JSON.getGson().fromJson(element, Match.class);
                    if (match.getType().contains("qm")) {
                        Match unique = mDbManager.getMatchesTable().query(null, match.getKey(), null, null).unique();
                        unique.setData(match.getData());
                        //unique.update();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            updateList();
        }
    }
}
