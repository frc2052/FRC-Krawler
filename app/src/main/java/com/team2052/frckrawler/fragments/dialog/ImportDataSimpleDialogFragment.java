package com.team2052.frckrawler.fragments.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.team2052.frckrawler.Constants;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.DatabaseActivity;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.listeners.RefreshListener;
import com.team2052.frckrawler.listitems.ListElement;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.elements.SimpleListElement;
import com.team2052.frckrawler.tba.ConnectionChecker;
import com.team2052.frckrawler.tba.HTTP;
import com.team2052.frckrawler.tba.JSON;
import com.team2052.frckrawler.tba.TBA;

import java.util.ArrayList;
import java.util.Collections;
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
    private LoadAllEventsByYear eventsByYear;
    private boolean isConnected;

    /**
     * Used to create the dialog. To import the event to the game
     *
     * @param game_id the game that the event will eventually be imported to.
     * @return The fragment with the specific arguments to run the dialog
     */
    public static ImportDataSimpleDialogFragment newInstance(long game_id) {
        ImportDataSimpleDialogFragment fragment = new ImportDataSimpleDialogFragment();
        Bundle b = new Bundle();
        b.putLong(DatabaseActivity.PARENT_ID, game_id);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mGame = DBManager.getInstance(getActivity()).getGamesTable().load(getArguments().getLong(DatabaseActivity.PARENT_ID));
        isConnected = ConnectionChecker.isConnectedToInternet(getActivity());
        yearDropDownItems = new String[Constants.MAX_COMP_YEAR - Constants.FIRST_COMP_YEAR + 1];
        for (int i = 0; i < yearDropDownItems.length; i++) {
            yearDropDownItems[i] = Integer.toString(Constants.MAX_COMP_YEAR - i);
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_import_simple, null);
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity(), R.style.AppAlertDialogStyle);
        b.setPositiveButton("Import", (dialog, which) -> {
            ImportEventDataDialog.newInstance(((ListElement) eventSpinner.getSelectedItem()).getKey(), mGame).show(ImportDataSimpleDialogFragment.this.getFragmentManager(), "importDialog");
        });
        b.setNegativeButton("Cancel", (dialog, which) -> {
            ImportDataSimpleDialogFragment.this.dismiss();
        });
        b.setNeutralButton("Add Manual", ((dialog, which) -> {
            AddEventDialogFragment.newInstance(mGame).show(getParentFragment().getChildFragmentManager(), "addEvent");
        }));
        yearSpinner = (Spinner) view.findViewById(R.id.import_year_spinner);
        yearSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, yearDropDownItems));
        yearSpinner.setOnItemSelectedListener(this);
        eventSpinner = (Spinner) view.findViewById(R.id.import_event_spinner);
        if (!isConnected) {
            yearSpinner.setVisibility(View.GONE);
            eventSpinner.setVisibility(View.GONE);
            view.findViewById(R.id.no_connection).setVisibility(View.VISIBLE);
        }
        b.setView(view);
        b.setTitle("Import Event");
        return b.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (eventsByYear != null && !eventsByYear.isCancelled()) {
            eventsByYear.cancel(false);
        }
        if (getParentFragment() != null && getParentFragment() instanceof RefreshListener) {
            ((RefreshListener) getParentFragment()).refresh();
        }
        super.onDismiss(dialog);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (isConnected) {
            eventsByYear = new LoadAllEventsByYear(Integer.parseInt((String) yearSpinner.getSelectedItem()));
            eventSpinner.setVisibility(View.GONE);
            getDialog().findViewById(R.id.progress).setVisibility(View.VISIBLE);
            eventsByYear.execute();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /**
     * Used to load all events and post it to a mSpinner list
     *
     * @author Adam
     */
    private class LoadAllEventsByYear extends AsyncTask<Void, Void, List<Event>> {
        private final String url;

        public LoadAllEventsByYear(int year) {
            this.url = String.format(TBA.EVENT_BY_YEAR, year);
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
            //Sort the event by date
            Collections.sort(events, (event, event2) -> Double.compare(event.getDate().getTime(), event2.getDate().getTime()));
            return events;
        }

        @Override
        protected void onPostExecute(List<Event> events) {
            //Create listitems so the UI can read it
            ArrayList<ListItem> listItems = new ArrayList<>();
            for (Event event : events) {
                listItems.add(new SimpleListElement(event.getName(), event.getFmsid()));
            }
            //Set the adapter for the events mSpinner
            ListViewAdapter adapter = new ListViewAdapter(getActivity(), listItems);
            eventSpinner.setAdapter(adapter);
            getDialog().findViewById(R.id.progress).setVisibility(View.GONE);
            eventSpinner.setVisibility(View.VISIBLE);
        }
    }
}


