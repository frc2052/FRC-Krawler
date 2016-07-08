package com.team2052.frckrawler.fragments.dialog;

import android.os.AsyncTask;
import android.os.Bundle;

import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.team2052.frckrawler.activities.DatabaseActivity;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Match;
import com.team2052.frckrawler.listeners.RefreshListener;
import com.team2052.frckrawler.tba.HTTP;
import com.team2052.frckrawler.tba.JSON;
import com.team2052.frckrawler.tba.TBA;

import java.util.List;

/**
 * @author Adam
 * @since 3/10/2015.
 */
public class UpdateMatchesProcessDialog extends BaseProgressDialog {

    public static UpdateMatchesProcessDialog newInstance(long event_id) {
        UpdateMatchesProcessDialog updateMatchesProcessDialog = new UpdateMatchesProcessDialog();
        Bundle bundle = new Bundle();
        bundle.putLong(DatabaseActivity.PARENT_ID, event_id);
        updateMatchesProcessDialog.setArguments(bundle);
        return updateMatchesProcessDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Event event = mDbManager.getEventsTable().load(getArguments().getLong(DatabaseActivity.PARENT_ID));
        if (Strings.isNullOrEmpty(event.getFmsid())) {
            dismiss();
            return;
        }
        new UpdateMatchSchedule(event).execute();
    }

    public class UpdateMatchSchedule extends AsyncTask<Void, String, Void> {

        private final Event event;
        private final String url;

        public UpdateMatchSchedule(Event event) {
            if (event.getFmsid() == null) {
                dismissAllowingStateLoss();
            }

            this.event = event;
            this.url = String.format(TBA.EVENT, event.getFmsid());

        }

        @Override
        protected Void doInBackground(Void... params) {
            //Delete all match data
            List<Match> data = mDbManager.getMatchesTable().query(null, null, event.getId(), null).list();

            mDbManager.runInTx(() -> mDbManager.getMatchesTable().delete(data));

            final JsonArray jMatches = JSON.getAsJsonArray(HTTP.dataFromResponse(HTTP.getResponse(url + "/matches")));

            JSON.set_daoSession(mDbManager);
            mDbManager.runInTx(() -> {
                for (JsonElement element : jMatches) {
                    //Save all the matches and alliances
                    Match match = JSON.getGson().fromJson(element, Match.class);
                    //Only save Qualifications
                    if (match.getMatch_type().contains("qm")) {
                        mDbManager.getMatchesTable().insert(match);
                    }
                }
            });


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ((RefreshListener) getParentFragment()).refresh();
            dismissAllowingStateLoss();
        }
    }

}
