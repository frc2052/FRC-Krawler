package com.team2052.frckrawler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import com.activeandroid.query.Select;
import com.team2052.frckrawler.R;
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
public class MatchListActivity extends NewDatabaseActivity {
    private Event mEvent;
    private ListView mListView;

    public static Intent newInstance(Context c, Event event) {
        Intent i = new Intent(c, MatchListActivity.class);
        i.putExtra(PARENT_ID, event.getId());
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.match_list_view);
        mEvent = Event.load(Event.class, getIntent().getLongExtra(NewDatabaseActivity.PARENT_ID, 0));
        mListView = (ListView) findViewById(R.id.list_view_match);
        new GetMatchesByEvent().execute();
    }

    public class GetMatchesByEvent extends AsyncTask<Void, Void, List<Match>> {

        @Override
        protected List<Match> doInBackground(Void... params) {
            return new Select().from(Match.class).orderBy("MatchNumber ASC").where("Event = ?", mEvent.getId()).execute();
        }

        @Override
        protected void onPostExecute(List<Match> matches) {
            List<ListItem> listItems = new ArrayList<ListItem>();
            for (Match match : matches) {
                listItems.add(new MatchListItem(match));
            }
            mListView.setAdapter(new ListViewAdapter(MatchListActivity.this, listItems));
        }
    }
}
