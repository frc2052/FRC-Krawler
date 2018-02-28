package com.team2052.frckrawler.fragments.dialog;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.team2052.frckrawler.activities.DatabaseActivity;
import com.team2052.frckrawler.data.RxDBManager;
import com.team2052.frckrawler.data.tba.v3.HTTP;
import com.team2052.frckrawler.data.tba.v3.JSON;
import com.team2052.frckrawler.data.tba.v3.TBA;
import com.team2052.frckrawler.interfaces.RefreshListener;
import com.team2052.frckrawler.models.Event;
import com.team2052.frckrawler.models.Season;
import com.team2052.frckrawler.models.Team;

/**
 * @author Adam
 * @since 3/9/2015.
 */
public class ImportEventDataDialog extends BaseProgressDialog {

    private String LOG_TAG = "ImportEventDataDialog";

    public static ImportEventDataDialog newInstance(String eventKey, Season game) {
        ImportEventDataDialog importDataDialog = new ImportEventDataDialog();
        Bundle bundle = new Bundle();
        bundle.putString("eventKey", eventKey);
        bundle.putLong(DatabaseActivity.Companion.getPARENT_ID(), game.getId());
        importDataDialog.setArguments(bundle);
        return importDataDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Season game = mRxDbManager.getSeasonsTable().load(getArguments().getLong(DatabaseActivity.Companion.getPARENT_ID()));
        String mEventKey = getArguments().getString("eventKey");
        new ImportEvent(mEventKey, game).execute();
    }

    public class ImportEvent extends AsyncTask<Void, String, Void> {
        private final String url;
        private final Season season;

        public ImportEvent(String eventKey, Season season) {
            //Get the main event based on the key
            this.url = String.format(TBA.EVENT, eventKey);
            this.season = season;
        }

        @Override
        protected Void doInBackground(Void... params) {
            final RxDBManager daoSession = RxDBManager.Companion.getInstance(getActivity());
            //Get all data from TBA ready to parse

            publishProgress("Downloading Data");
            final JsonElement jEvent = JSON.getAsJsonObject(HTTP.dataFromResponse(HTTP.getResponse(url)));
            final JsonArray jTeams = JSON.getAsJsonArray(HTTP.dataFromResponse(HTTP.getResponse(url + "/teams")));

            //Run in bulk transaction
            daoSession.runInTx(() -> {
                //Save the event
                Event event = JSON.getGson().fromJson(jEvent, Event.class);

                event.setSeason_id(season.getId());
                daoSession.getEventsTable().insert(event);
                //Save the teams
                ImportEvent.this.publishProgress("Saving Teams");
                for (JsonElement element : jTeams) {
                    //Convert json element to team
                    Team team = JSON.getGson().fromJson(element, Team.class);
                    mRxDbManager.getTeamsTable().insertNew(team, event);
                }
                ImportEvent.this.publishProgress("Saving Matches");
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ImportEventDataDialog.this.dismissAllowingStateLoss();
            if (getParentFragment() instanceof RefreshListener) {
                ((RefreshListener) getParentFragment()).refresh();
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            ((ProgressDialog) getDialog()).setMessage(values[0]);
        }


    }
}

