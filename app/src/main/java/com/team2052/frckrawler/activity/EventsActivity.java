package com.team2052.frckrawler.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.activeandroid.query.Select;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.dialog.AddEventDialogActivity;
import com.team2052.frckrawler.activity.dialog.EditEventDialogActivity;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.models.Event;
import com.team2052.frckrawler.database.models.Game;
import com.team2052.frckrawler.database.structures.Robot;
import com.team2052.frckrawler.fragment.dialog.ImportDataSimpleDialogFragment;
import com.team2052.frckrawler.gui.ProgressSpinner;
import com.team2052.frckrawler.listitems.EventListItem;
import com.team2052.frckrawler.listitems.ListItem;

import java.util.ArrayList;
import java.util.List;

public class EventsActivity extends NewDatabaseActivity implements OnClickListener {
    private static final int EDIT_EVENT_ID = 1;
    private DBManager dbManager;
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
        dbManager = DBManager.getInstance(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addbutton, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_metric_action) {
            /*ImportDataSimpleDialogFragment importFragment = new ImportDataSimpleDialogFragment();
            importFragment.show(getSupportFragmentManager(), "ImportEvent");*/
            startActivity(AddEventDialogActivity.newInstance(this, mGame));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        new GetEventsTask().execute();
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case EDIT_EVENT_ID:
                i = new Intent(this, EditEventDialogActivity.class);
                i.putExtra(EditEventDialogActivity.EVENT_ID_EXTRA, v.getTag().toString());
                startActivity(i);
        }
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

    private class StartRobotsActivityTask extends AsyncTask<Integer, Void, Intent> {
        private AlertDialog progressDialog;

        @Override
        protected void onPreExecute() {
            AlertDialog.Builder builder = new AlertDialog.Builder(EventsActivity.this);
            builder.setTitle("Loading...");
            builder.setView(new ProgressSpinner(EventsActivity.this));
            builder.setCancelable(false);
            progressDialog = builder.create();
            progressDialog.show();
        }

        @Override
        protected Intent doInBackground(Integer... params) {
            Robot[] attendingRobots = dbManager.getRobotsAtEvent
                    (params[0]);
            String[] dbValsArr = new String[attendingRobots.length];
            String[] dbKeysArr = new String[attendingRobots.length];

            for (int k = 0; k < attendingRobots.length; k++) {
                dbValsArr[k] = Integer.toString(attendingRobots[k].getID());
                dbKeysArr[k] = DBContract.COL_ROBOT_ID;
            }

            Intent i = new Intent(EventsActivity.this, RobotsActivity.class);
            //i.putExtra(DB_VALUES_EXTRA, dbValsArr);
            //i.putExtra(DB_KEYS_EXTRA, dbKeysArr);
            return i;
        }

        @Override
        protected void onPostExecute(Intent i) {
            progressDialog.dismiss();
            startActivity(i);
        }
    }
}
