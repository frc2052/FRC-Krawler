package com.team2052.frckrawler.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.team2052.frckrawler.GlobalValues;
import com.team2052.frckrawler.ListUpdateListener;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.DatabaseActivity;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.database.models.Event;
import com.team2052.frckrawler.database.models.Game;
import com.team2052.frckrawler.database.models.Match;
import com.team2052.frckrawler.database.models.Robot;
import com.team2052.frckrawler.database.models.RobotEvents;
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
 * Used to import a event to a game in the most simple way for the user.
 *
 * @author Adam
 */
public class ImportDataSimpleDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {

    private String[] yearDropDownItems;
    private Spinner yearSpinner;
    private Spinner eventSpinner;
    private Game mGame;

    /**
     * Used to create the dialog. To import the event to the game
     *
     * @param game the game that the event will eventually be imported to.
     * @return The fragment with the specific arguments to run the dialog
     */
    public static ImportDataSimpleDialogFragment newInstance(Game game) {
        ImportDataSimpleDialogFragment fragment = new ImportDataSimpleDialogFragment();
        Bundle b = new Bundle();
        b.putLong(DatabaseActivity.PARENT_ID, game.getId());
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mGame = Game.load(Game.class, getArguments().getLong(DatabaseActivity.PARENT_ID));
        yearDropDownItems = new String[GlobalValues.MAX_COMP_YEAR - GlobalValues.FIRST_COMP_YEAR + 2];
        yearDropDownItems[0] = "Select Year";
        for (int i = 1; i < yearDropDownItems.length; i++) {
            yearDropDownItems[i] = Integer.toString(GlobalValues.MAX_COMP_YEAR - i + 1);
        }
        setRetainInstance(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_import_simple, null);
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setPositiveButton("Import", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (new Select().from(Event.class).where("FMSid = ?", ((ListElement) eventSpinner.getSelectedItem()).getKey()).execute().size() == 0) {
                    ProgressDialogFragment.showLoadingProgress(getFragmentManager());
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
        yearSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, yearDropDownItems));
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
        eventSpinner.setVisibility(View.GONE);
        getDialog().findViewById(R.id.progress).setVisibility(View.VISIBLE);
        eventsByYear.execute();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        ((ListUpdateListener) getActivity()).updateList();
        super.onDismiss(dialog);
    }

    /**
     * Used to load all events and post it to a spinner list
     *
     * @author Adam
     */
    public class LoadAllEventsByYear extends AsyncTask<Void, Void, List<Event>> {
        private final String url;

        public LoadAllEventsByYear(int year) {
            this.url = TBA.BASE_TBA_URL + String.format(TBA.EVENT_BY_YEAR_REQUEST, year);
        }

        @Override
        protected List<Event> doInBackground(Void... params) {
            //Get the response for the events by year
            JsonArray jsonArray = JSON.getAsJsonArray(HTTP.dataFromResponse(HTTP.getResponse(url)));
            //Create an array for events
            List<Event> events = new ArrayList<>();
            for (JsonElement element : jsonArray) {
                //Add the events to the list
                events.add(JSON.getGson().fromJson(element, Event.class));
            }

            return events;
        }

        @Override
        protected void onPostExecute(List<Event> events) {
            //Create listitems so the UI can read it
            ArrayList<ListItem> listItems = new ArrayList<>();
            for (Event event : events) {
                listItems.add(new SimpleListElement(event.name, event.fmsId));
            }
            //Set the adapter for the events spinner
            ListViewAdapter adapter = new ListViewAdapter(getActivity(), listItems);
            eventSpinner.setAdapter(adapter);
            getDialog().findViewById(R.id.progress).setVisibility(View.GONE);
            eventSpinner.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Used to import data to the database based on the event key.
     *
     * @author Adam
     */
    public class LoadAllEventData extends AsyncTask<Void, Void, Void> {
        //TODO Separate to avoid losing the fragment manager when you dismiss the host dialog
        private final String url;
        final FragmentManager fragman = getFragmentManager();
        public LoadAllEventData(String eventKey) {
            //Get the main event url based on the key
            this.url = TBA.BASE_TBA_URL + String.format(TBA.EVENT_REQUEST, eventKey);
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Get all data from TBA ready to parse
            JsonElement jEvent = JSON.getAsJsonObject(HTTP.dataFromResponse(HTTP.getResponse(url)));
            JsonArray jTeams = JSON.getAsJsonArray(HTTP.dataFromResponse(HTTP.getResponse(url + "/teams")));
            JsonArray jMatches = JSON.getAsJsonArray(HTTP.dataFromResponse(HTTP.getResponse(url + "/matches")));

            ActiveAndroid.beginTransaction();
            //Save the event
            Event event = JSON.getGson().fromJson(jEvent, Event.class);
            event.game = mGame;
            event.save();

            //Save the teams
            for (JsonElement element : jTeams) {
                //Convert json element to team
                Team team = JSON.getGson().fromJson(element, Team.class);
                //Create a robot and save that robot to the database as well with the team
                Robot robot = new Robot(team, null, -1, mGame);
                robot.setRemoteId();
                robot.team.save();
                RobotEvents robotEvents = new RobotEvents(robot, event);
                robotEvents.robot.save();
                robotEvents.save();
            }
            for (JsonElement element : jMatches) {
                //Save all the matches and alliances
                Match match = JSON.getGson().fromJson(element, Match.class);
                //Only save Qualifications
                if (match.matchType.contains("qm")) {
                    match.alliance.save();
                    match.save();
                }
            }
            ActiveAndroid.setTransactionSuccessful();
            ActiveAndroid.endTransaction();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ProgressDialogFragment.dismissLoadingProgress(fragman);
        }
    }
}

