package com.team2052.frckrawler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.activeandroid.query.Select;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.dialog.AddEventDialogActivity;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.database.models.Event;
import com.team2052.frckrawler.database.models.Game;
import com.team2052.frckrawler.listitems.EventListItem;
import com.team2052.frckrawler.listitems.ListItem;

import java.util.ArrayList;
import java.util.List;

public class EventsActivity extends NewDatabaseActivity {
    private static final int EDIT_EVENT_ID = 1;
    private Game mGame;

    public static Intent newInstance(Context context, Game game) {
        Intent i = new Intent(context, EventsActivity.class);
        i.putExtra(PARENT_ID, game.getId());
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        mGame = Game.load(Game.class, getIntent().getLongExtra(PARENT_ID, -1));
        getActionBar().setTitle(mGame == null ? "Edit Events" : "Edit Events - " + mGame.name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addbutton, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_metric_action) {
            startActivity(AddEventDialogActivity.newInstance(this, mGame));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        new GetEventsTask().execute();
    }

    private class GetEventsTask extends AsyncTask<Void, Void, List<Event>> {

        @Override
        protected List<Event> doInBackground(Void... params) {
            //Load events based on gameId
            return new Select().from(Event.class).where("Game = ?", mGame.getId()).execute();
        }

        @Override
        protected void onPostExecute(List<Event> events) {
            ArrayList<ListItem> eventList = new ArrayList<ListItem>();
            for (Event event : events) {
                eventList.add(new EventListItem(event));
            }
            ListViewAdapter adapter = new ListViewAdapter(EventsActivity.this, eventList);
            ((ListView) findViewById(R.id.events_list)).setAdapter(adapter);
        }
    }
}
