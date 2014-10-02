package com.team2052.frckrawler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.activeandroid.query.Select;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.database.models.Event;
import com.team2052.frckrawler.database.models.Match;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.MatchListItem;

import java.util.ArrayList;
import java.util.List;

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
        mEvent = Event.load(Event.class, getIntent().getLongExtra(DatabaseActivity.PARENT_ID, 0));
        super.onCreate(savedInstanceState);
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
            List<Match> matches = new Select().from(Match.class).orderBy("MatchNumber ASC").where("Event = ?", mEvent.getId()).execute();
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
