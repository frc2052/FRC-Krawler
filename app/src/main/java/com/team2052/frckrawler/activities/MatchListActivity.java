package com.team2052.frckrawler.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Match;
import com.team2052.frckrawler.db.MatchDao;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.items.MatchListItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Adam
 */
public class MatchListActivity extends DatabaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.list)
    ListView mListView;
    private Event mEvent;
    private ListViewAdapter mAdapter;

    public static Intent newInstance(Context c, Event event) {
        Intent i = new Intent(c, MatchListActivity.class);
        i.putExtra(DatabaseActivity.PARENT_ID, event.getId());
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_view_list);
        ButterKnife.bind(this);

        mEvent = dbManager.getEventsTable().load(getIntent().getLongExtra(DatabaseActivity.PARENT_ID, 0));

        toolbar.setTitle("Match Schedule");
        toolbar.setSubtitle(mEvent.getName());
        setSupportActionBar(toolbar);

        setActionBarTitle("Schedule");
        setActionBarSubtitle(mEvent.getName());
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
        } else {
            new GetMatchesByEvent().execute();
        }
    }

    @Override
    public void inject() {
        getComponent().inject(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class GetMatchesByEvent extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            List<Match> matches = dbManager.getMatchesTable().query(null, null, mEvent.getId(), null).orderAsc(MatchDao.Properties.Match_number).list();
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
            mListView.setDivider(null);
            mListView.setClipToPadding(false);
            mListView.setDividerHeight(0);
        }
    }
}
