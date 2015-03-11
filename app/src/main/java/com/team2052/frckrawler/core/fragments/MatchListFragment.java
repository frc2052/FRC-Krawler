package com.team2052.frckrawler.core.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.core.activities.DatabaseActivity;
import com.team2052.frckrawler.core.adapters.ListViewAdapter;
import com.team2052.frckrawler.core.fragments.dialog.process.UpdateMatchesProcessDialog;
import com.team2052.frckrawler.core.listitems.ListItem;
import com.team2052.frckrawler.core.listitems.items.MatchListItem;
import com.team2052.frckrawler.core.tba.HTTP;
import com.team2052.frckrawler.core.tba.JSON;
import com.team2052.frckrawler.core.tba.TBA;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Match;
import com.team2052.frckrawler.db.MatchDao;

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
        b.putLong(DatabaseActivity.PARENT_ID, event.getId());
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
        mEvent = mDaoSession.getEventDao().load(getArguments().getLong(DatabaseActivity.PARENT_ID));
    }

    @Override
    public void updateList() {
        new GetMatches().execute();
    }

    public class GetMatches extends AsyncTask<Void, Void, List<Match>> {

        @Override
        protected List<Match> doInBackground(Void... params) {
            //Get Matches ascending from the provided event id
            return mDaoSession.getMatchDao().queryBuilder().orderAsc(MatchDao.Properties.Number).where(MatchDao.Properties.EventId.eq(mEvent.getId())).list();
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
                String url = TBA.BASE_TBA_URL + String.format(TBA.EVENT_REQUEST, mEvent.getFmsid());
                final JsonArray jMatches = JSON.getAsJsonArray(HTTP.dataFromResponse(HTTP.getResponse(url + "/matches")));
                JSON.set_daoSession(mDaoSession);
                for (JsonElement element : jMatches) {
                    Match match = JSON.getGson().fromJson(element, Match.class);
                    if (match.getType().contains("qm")) {
                        Match unique = mDaoSession.getMatchDao().queryBuilder().where(MatchDao.Properties.Key.eq(match.getKey())).unique();
                        unique.setRedscore(match.getRedscore());
                        unique.setBluescore(match.getBluescore());
                        unique.update();
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

    /**
     *
     */
    public class GetMatchesUpdate extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            return null;
        }
    }
}
