package com.team2052.frckrawler.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.team2052.frckrawler.FRCKrawler;
import com.team2052.frckrawler.GlobalValues;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.DatabaseActivity;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.db.DaoSession;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.db.Match;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.db.RobotDao;
import com.team2052.frckrawler.db.RobotEvent;
import com.team2052.frckrawler.db.RobotEventDao;
import com.team2052.frckrawler.db.Team;
import com.team2052.frckrawler.listeners.ListUpdateListener;
import com.team2052.frckrawler.listitems.ListElement;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.elements.SimpleListElement;
import com.team2052.frckrawler.tba.HTTP;
import com.team2052.frckrawler.tba.JSON;
import com.team2052.frckrawler.tba.TBA;
import com.team2052.frckrawler.util.LogHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Used to import a event to a game in the most simple way for the user.
 *
 * @author Adam
 */
public class ImportDataSimpleDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener
{

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
    public static ImportDataSimpleDialogFragment newInstance(Game game)
    {
        ImportDataSimpleDialogFragment fragment = new ImportDataSimpleDialogFragment();
        Bundle b = new Bundle();
        b.putLong(DatabaseActivity.PARENT_ID, game.getId());
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.mGame = ((FRCKrawler) getActivity().getApplication()).getDaoSession().getGameDao().load(getArguments().getLong(DatabaseActivity.PARENT_ID));
        yearDropDownItems = new String[GlobalValues.MAX_COMP_YEAR - GlobalValues.FIRST_COMP_YEAR + 2];
        yearDropDownItems[0] = "Select Year";
        for (int i = 1; i < yearDropDownItems.length; i++) {
            yearDropDownItems[i] = Integer.toString(GlobalValues.MAX_COMP_YEAR - i + 1);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_import_simple, null);
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setPositiveButton("Import", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                ProgressDialogFragment.showLoadingProgress(getFragmentManager());
                new LoadAllEventData(((ListElement) eventSpinner.getSelectedItem()).getKey()).execute();
            }
        });
        b.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        if (position == 0) return;
        LoadAllEventsByYear eventsByYear = new LoadAllEventsByYear(Integer.parseInt((String) yearSpinner.getSelectedItem()));
        eventSpinner.setVisibility(View.GONE);
        getDialog().findViewById(R.id.progress).setVisibility(View.VISIBLE);
        eventsByYear.execute();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {
    }

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        ((ListUpdateListener) getParentFragment()).updateList();
        super.onDismiss(dialog);
    }

    /**
     * Used to load all events and post it to a spinner list
     *
     * @author Adam
     */
    public class LoadAllEventsByYear extends AsyncTask<Void, Void, List<Event>>
    {
        private final String url;

        public LoadAllEventsByYear(int year)
        {
            this.url = TBA.BASE_TBA_URL + String.format(TBA.EVENT_BY_YEAR_REQUEST, year);
        }

        @Override
        protected List<Event> doInBackground(Void... params)
        {
            //Get the response for the events by year
            JsonArray jsonArray = JSON.getAsJsonArray(HTTP.dataFromResponse(HTTP.getResponse(url)));
            //Create an array for events
            List<Event> events = new ArrayList<>();
            for (JsonElement element : jsonArray) {
                //Add the events to the list
                events.add(JSON.getGson().fromJson(element, Event.class));
            }
            //Sort the event by date
            Collections.sort(events, new Comparator<Event>()
            {
                @Override
                public int compare(Event event, Event event2)
                {
                    return Double.compare(event.getDate().getTime(), event2.getDate().getTime());
                }
            });
            return events;
        }

        @Override
        protected void onPostExecute(List<Event> events)
        {
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

    /**
     * Used to import data to the database based on the event key.
     *
     * @author Adam
     */
    public class LoadAllEventData extends AsyncTask<Void, Void, Void>
    {
        final FragmentManager fragman = getFragmentManager();
        private final String url;

        public LoadAllEventData(String eventKey)
        {
            //Get the main event url based on the key
            this.url = TBA.BASE_TBA_URL + String.format(TBA.EVENT_REQUEST, eventKey);
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            final long startTime = System.currentTimeMillis();
            final DaoSession daoSession = ((FRCKrawler) getActivity().getApplication()).getDaoSession();
            //Get all data from TBA ready to parse
            final JsonElement jEvent = JSON.getAsJsonObject(HTTP.dataFromResponse(HTTP.getResponse(url)));
            final JsonArray jTeams = JSON.getAsJsonArray(HTTP.dataFromResponse(HTTP.getResponse(url + "/teams")));
            final JsonArray jMatches = JSON.getAsJsonArray(HTTP.dataFromResponse(HTTP.getResponse(url + "/matches")));

            //Run in bulk transaction
            daoSession.runInTx(new Runnable()
            {
                @Override
                public void run()
                {
                    //Save the event
                    Event event = JSON.getGson().fromJson(jEvent, Event.class);
                    event.setGame(mGame);
                    daoSession.getEventDao().insert(event);

                    //Save the teams
                    for (JsonElement element : jTeams) {
                        //Convert json element to team
                        Team team = JSON.getGson().fromJson(element, Team.class);
                        daoSession.getTeamDao().insertOrReplace(team);
                        //Create a robot and save that robot to the database as well with the team
                        Robot robot = daoSession.getRobotDao().queryBuilder().where(RobotDao.Properties.GameId.eq(mGame.getId())).where(RobotDao.Properties.TeamId.eq(team.getNumber())).unique();

                        if (robot == null) {
                            daoSession.getRobotEventDao().insert(new RobotEvent(null, daoSession.getRobotDao().insert(new Robot(null, team.getNumber(), mGame.getId(), "", 0.0)), event.getId()));
                        } else {
                            List<RobotEvent> robotEvents = daoSession.getRobotEventDao().queryBuilder().where(RobotEventDao.Properties.RobotId.eq(robot.getId())).where(RobotEventDao.Properties.EventId.eq(event.getId())).list();
                            if (robotEvents.size() <= 0) {
                                daoSession.getRobotEventDao().insert(new RobotEvent(null, robot.getId(), event.getId()));
                            }
                        }
                    }
                    JSON.set_daoSession(daoSession);
                    for (JsonElement element : jMatches) {
                        //Save all the matches and alliances
                        Match match = JSON.getGson().fromJson(element, Match.class);
                        //Only save Qualifications
                        if (match.getType().contains("qm")) {
                            daoSession.insert(match);
                        }
                    }
                    LogHelper.debug("Saved " + event.getName() + " In " + (System.currentTimeMillis() - startTime) + "ms");
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            ProgressDialogFragment.dismissLoadingProgress(fragman);
        }
    }
}


