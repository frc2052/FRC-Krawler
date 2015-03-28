package com.team2052.frckrawler.core.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.team2052.frckrawler.core.adapters.ListViewAdapter;
import com.team2052.frckrawler.core.listitems.ListItem;
import com.team2052.frckrawler.core.listitems.items.MatchListItem;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Match;
import com.team2052.frckrawler.db.MatchDao;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam
 */
public class MatchListActivity extends ListActivity {
    private Event mEvent;

    public static Intent newInstance(Context c, Event event) {
        Intent i = new Intent(c, MatchListActivity.class);
        i.putExtra(PARENT_ID, event.getId());
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEvent = mDbManager.getDaoSession().getEventDao().load(getIntent().getLongExtra(PARENT_ID, 0));
        setActionBarTitle("Schedule");
        setActionBarSubtitle(mEvent.getName());
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void updateList() {
        new GetMatchesByEvent().execute();
    }

    public class GetMatchesByEvent extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            List<Match> matches = mDbManager.getDaoSession().getMatchDao().queryBuilder().orderAsc(MatchDao.Properties.Number).where(MatchDao.Properties.EventId.eq(mEvent.getId())).listLazy();
            List<ListItem> listItems = new ArrayList<>();

            for (Match match : matches) {
                listItems.add(new MatchListItem(match));
            }

            mAdapter = new ListViewAdapter(MatchListActivity.this, listItems);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mListView.setAdapter(mAdapter);
        }
    }
}
