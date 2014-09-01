package com.team2052.frckrawler.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.activeandroid.query.Select;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.team2052.frckrawler.GlobalValues;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.NewDatabaseActivity;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.models.Event;
import com.team2052.frckrawler.database.models.Game;
import com.team2052.frckrawler.database.models.Match;
import com.team2052.frckrawler.database.models.Robot;
import com.team2052.frckrawler.database.models.Team;
import com.team2052.frckrawler.listitems.ListElement;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.SimpleListElement;
import com.team2052.frckrawler.tba.HTTP;
import com.team2052.frckrawler.tba.JSON;
import com.team2052.frckrawler.tba.TBA;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam
 */
public class ImportDataSimpleDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {

    private String[] yearDropDownItems;
    private Spinner yearSpinner;
    private Spinner eventSpinner;
    private Game mGame;
    private ProgressDialog progressBar;

    public static ImportDataSimpleDialogFragment newInstance(Game game) {
        ImportDataSimpleDialogFragment fragment = new ImportDataSimpleDialogFragment();
        Bundle b = new Bundle();
        b.putLong(NewDatabaseActivity.PARENT_ID, game.getId());
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mGame = Game.load(Game.class, getArguments().getLong(NewDatabaseActivity.PARENT_ID));
        yearDropDownItems = new String[GlobalValues.MAX_COMP_YEAR - GlobalValues.FIRST_COMP_YEAR + 2];
        yearDropDownItems[0] = "Select Year";
        for (int i = 1; i < yearDropDownItems.length; i++) {
            yearDropDownItems[i] = Integer.toString(GlobalValues.MAX_COMP_YEAR - i + 1);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_import_simple, null);
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setPositiveButton("Import", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (new Select().from(Event.class).where("FMSid = ?", ((ListElement) eventSpinner.getSelectedItem()).getKey()).execute().size() == 0) {
                    //progressBar = ProgressDialog.show(getActivity() == null ? getParentFragment().getActivity() : getActivity(), "Importing Event Data...", "", true);
                    new LoadAllEventData(((ListElement) eventSpinner.getSelectedItem()).getKey()).execute();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity() == null ? getParentFragment().getActivity() : getActivity());
                    builder.setPositiveButton("Ok", null);
                    builder.setTitle("Error Importing");
                    builder.setMessage("Can't import event that has been already imported");
                    builder.show();
                }
            }
        });
        b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        yearSpinner = (Spinner) view.findViewById(R.id.import_year_spinner);
        yearSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, yearDropDownItems));
        yearSpinner.setOnItemSelectedListener(this);
        eventSpinner = (Spinner) view.findViewById(R.id.import_event_spinner);
        b.setView(view);
        b.setTitle("Import Event");
        return b.create();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) return;
        LoadAllEventsByYear eventsByYear = new LoadAllEventsByYear(Integer.parseInt((String) yearSpinner.getSelectedItem()));
        eventsByYear.execute();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * Used to load all events and post it to a spinner list
     * @author Adam
     */
    public class LoadAllEventsByYear extends AsyncTask<Void, Void, List<Event>> {
        private final String url;

        public LoadAllEventsByYear(int year) {
            this.url = TBA.BASE_TBA_URL + String.format(TBA.EVENT_BY_YEAR_REQUEST, year);
        }

        @Override
        protected List<Event> doInBackground(Void... params) {
            JsonArray jsonArray = JSON.getAsJsonArray(HTTP.dataFromResponse(HTTP.getResponse(url)));
            ArrayList<Event> events = new ArrayList<Event>();
            for (JsonElement element : jsonArray) {
                events.add(JSON.getGson().fromJson(element, Event.class));
            }
            return events;
        }

        @Override
        protected void onPostExecute(List<Event> events) {
            ArrayList<ListItem> listItems = new ArrayList<ListItem>();
            for (Event event : events) {
                listItems.add(new SimpleListElement(event.name, event.fmsId));
            }
            ListViewAdapter adapter = new ListViewAdapter(getActivity(), listItems);
            eventSpinner.setAdapter(adapter);
        }
    }

    /**
     * Used to import data to the database based on the event key.
     * @author Adam
     */
    public class LoadAllEventData extends AsyncTask<Void, Void, Void> {
        private final String url;

        public LoadAllEventData(String eventKey) {
            this.url = TBA.BASE_TBA_URL + String.format(TBA.EVENT_REQUEST, eventKey);
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Get event Data
            JsonElement jEvent = JSON.getAsJsonObject(HTTP.dataFromResponse(HTTP.getResponse(url)));
            Event event = JSON.getGson().fromJson(jEvent, Event.class);
            event.game = mGame;
            event.save();
            //Get teams
            JsonArray jTeams = JSON.getAsJsonArray(HTTP.dataFromResponse(HTTP.getResponse(url + "/teams")));
            JsonArray jMatches = JSON.getAsJsonArray(HTTP.dataFromResponse(HTTP.getResponse(url + "/matches")));
            List<Team> teams = new ArrayList<Team>();
            Log.d("FRCKrawler", "Parsing Teams");
            for (JsonElement element : jTeams) {
                teams.add(JSON.getGson().fromJson(element, Team.class));
            }
            //Save all the teams and create robot based on the current selected game.
            for (Team team : teams) {
                Log.d("FRCKrawler", String.format("Importing Team %s", team.number));
                Robot robot = new Robot(team, null, -1, mGame);
                robot.setRemoteId();
                robot.team.save();
                robot.save();
            }
            //Convert JsonMatches to Matches
            List<Match> matches = new ArrayList<Match>();
            Log.d("FRCKrawler", "Parsing Matches");
            for (JsonElement element : jMatches) {
                matches.add(JSON.getGson().fromJson(element, Match.class));
            }

            //Save all the matches including the alliances, Score and what not.
            for (Match match : matches) {
                Log.d("FRCKrawler", String.format("Importing Match %s", match.key));
                match.alliance.save();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //progressBar.dismiss();
            dismiss();
            super.onPostExecute(aVoid);
        }
    }
}


