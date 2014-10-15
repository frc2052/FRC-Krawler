package com.team2052.frckrawler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.items.MatchListItem;

import java.util.ArrayList;
import java.util.List;

import frckrawler.Event;
import frckrawler.Match;
import frckrawler.MatchDao;

/**
 * @author Adam
 */
public class MatchListActivity extends ListActivity
{
    private Event mEvent;

    public static Intent newInstance(Context c, Event event)
    {
        Intent i = new Intent(c, MatchListActivity.class);
        i.putExtra(PARENT_ID, event.getId());
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mEvent = mDaoSession.getEventDao().load(getIntent().getLongExtra(PARENT_ID, 0));
        setActionBarTitle("Schedule");
        setActionBarSubtitle(mEvent.getName());
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void updateList()
    {
        new GetMatchesByEvent().execute();
    }

    public class GetMatchesByEvent extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... params)
        {
            List<Match> matches = mDaoSession.getMatchDao().queryBuilder().orderAsc(MatchDao.Properties.Number).where(MatchDao.Properties.EventId.eq(mEvent.getId())).listLazy();
            List<ListItem> listItems = new ArrayList<>();

            for (Match match : matches) {
                listItems.add(new MatchListItem(match));
            }

            mAdapter = new ListViewAdapter(MatchListActivity.this, listItems);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            mListView.setAdapter(mAdapter);
        }
    }
}
