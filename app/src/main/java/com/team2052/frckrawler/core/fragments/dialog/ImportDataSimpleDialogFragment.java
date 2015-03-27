package com.team2052.frckrawler.core.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.core.FRCKrawler;
import com.team2052.frckrawler.core.GlobalValues;
import com.team2052.frckrawler.core.activities.DatabaseActivity;
import com.team2052.frckrawler.core.adapters.ListViewAdapter;
import com.team2052.frckrawler.core.fragments.dialog.process.ImportEventDataDialog;
import com.team2052.frckrawler.core.listeners.ListUpdateListener;
import com.team2052.frckrawler.core.listitems.ListElement;
import com.team2052.frckrawler.core.listitems.ListItem;
import com.team2052.frckrawler.core.listitems.elements.SimpleListElement;
import com.team2052.frckrawler.core.tba.HTTP;
import com.team2052.frckrawler.core.tba.JSON;
import com.team2052.frckrawler.core.tba.TBA;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        this.mGame = ((FRCKrawler) getActivity().getApplication()).getDaoSession().getGameDao().load(getArguments().getLong(DatabaseActivity.PARENT_ID));
        yearDropDownItems = new String[GlobalValues.MAX_COMP_YEAR - GlobalValues.FIRST_COMP_YEAR + 1];
        for (int i = 0; i < yearDropDownItems.length; i++) {
            yearDropDownItems[i] = Integer.toString(GlobalValues.MAX_COMP_YEAR - i);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_import_simple, null);
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setPositiveButton("Import", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ImportEventDataDialog.newInstance(((ListElement) eventSpinner.getSelectedItem()).getKey(), mGame).show(getFragmentManager(), "importDialog");
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
    public void onDismiss(DialogInterface dialog) {
        if (getParentFragment() != null && getParentFragment() instanceof ListUpdateListener) {
            ((ListUpdateListener) getParentFragment()).updateList();
        }
        super.onDismiss(dialog);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        LoadAllEventsByYear eventsByYear = new LoadAllEventsByYear(Integer.parseInt((String) yearSpinner.getSelectedItem()));
        eventSpinner.setVisibility(View.GONE);
        getDialog().findViewById(R.id.progress).setVisibility(View.VISIBLE);
        eventsByYear.execute();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /**
     * Used to load all events and post it to a spinner list
     *
     * @author Adam
     */
    private class LoadAllEventsByYear extends AsyncTask<Void, Void, List<Event>> {
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
            //Sort the event by date
            /*Collections.sort(events, new Comparator<Event>() {
                @Override
                public int compare(Event event, Event event2) {
                    return Double.compare(event.getDate().getTime(), event2.getDate().getTime());
                }
            });*/
            return events;
        }

        @Override
        protected void onPostExecute(List<Event> events) {
            //Create listitems so the UI can read it
            ArrayList<ListItem> listItems = new ArrayList<>();
            for (Event event : events) {
                listItems.add(new SimpleListElement(event.getName(), event.getFmsid()));
            }
            //Set the adapter for the events spinner
            ListViewAdapter adapter = new ListViewAdapter(getActivity(), listItems);
            eventSpinner.setAdapter(adapter);
            getDialog().findViewById(R.id.progress).setVisibility(View.GONE);
            eventSpinner.setVisibility(View.VISIBLE);
        }
    }
}


