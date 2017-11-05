package com.team2052.frckrawler.fragments.dialog;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.team2052.frckrawler.activities.DatabaseActivity;
import com.team2052.frckrawler.database.RxDBManager;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.db.Team;
import com.team2052.frckrawler.listeners.RefreshListener;
import com.team2052.frckrawler.tba.HTTP;
import com.team2052.frckrawler.tba.JSON;
import com.team2052.frckrawler.tba.TBA;
import com.team2052.frckrawler.util.Util;

/**
 * @author Adam
 * @since 3/9/2015.
 */
public class ImportEventDataDialog extends BaseProgressDialog {

    private String LOG_TAG = "ImportEventDataDialog";

    public static ImportEventDataDialog newInstance(String eventKey, Game game) {
        ImportEventDataDialog importDataDialog = new ImportEventDataDialog();
        Bundle bundle = new Bundle();
        bundle.putString("eventKey", eventKey);
        bundle.putLong(DatabaseActivity.PARENT_ID, game.getId());
        importDataDialog.setArguments(bundle);
        return importDataDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Game game = mRxDbManager.getGamesTable().load(getArguments().getLong(DatabaseActivity.PARENT_ID));
        String mEventKey = getArguments().getString("eventKey");
        new ImportEvent(mEventKey, game).execute();
    }

    public class ImportEvent extends AsyncTask<Void, String, Void> {
        private final String url;
        private final Game game;

        public ImportEvent(String eventKey, Game mGame) {
            //Get the main event based on the key
            this.url = String.format(TBA.EVENT, eventKey);
            this.game = mGame;
        }

        @Override
        protected Void doInBackground(Void... params) {
            final long startTime = System.currentTimeMillis();
            final RxDBManager daoSession = RxDBManager.getInstance(getActivity());
            //Get all data from TBA ready to parse

            publishProgress("Downloading Data");
            final JsonElement jEvent = JSON.getAsJsonObject(HTTP.dataFromResponse(HTTP.getResponse(url)));
            final JsonArray jTeams = JSON.getAsJsonArray(HTTP.dataFromResponse(HTTP.getResponse(url + "/teams")));

            //Run in bulk transaction
            daoSession.runInTx(() -> {
                //Save the event
                Event event = JSON.getGson().fromJson(jEvent, Event.class);
                event.setUnique_hash(Util.generateUniqueHash());

                event.setGame_id(game.getId());
                daoSession.getEventsTable().insert(event);
                //Save the teams
                ImportEvent.this.publishProgress("Saving Teams");
                for (JsonElement element : jTeams) {
                    //Convert json element to team
                    Team team = JSON.getGson().fromJson(element, Team.class);
                    mRxDbManager.getTeamsTable().insertNew(team, event);
                }
                JSON.set_daoSession(daoSession);
                Log.i(LOG_TAG, "Saved " + event.getName() + " In " + (System.currentTimeMillis() - startTime) + "ms");
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

