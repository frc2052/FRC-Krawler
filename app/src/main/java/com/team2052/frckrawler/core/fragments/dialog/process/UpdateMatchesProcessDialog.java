package com.team2052.frckrawler.core.fragments.dialog.process;

import android.os.AsyncTask;
import android.os.Bundle;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.team2052.frckrawler.core.activities.DatabaseActivity;
import com.team2052.frckrawler.core.fragments.ListFragment;
import com.team2052.frckrawler.core.tba.HTTP;
import com.team2052.frckrawler.core.tba.JSON;
import com.team2052.frckrawler.core.tba.TBA;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Match;
import com.team2052.frckrawler.db.MatchDao;

import java.util.List;

/**
 * @author Adam
 * @since 3/10/2015.
 */
public class UpdateMatchesProcessDialog extends BaseProgressDialog {

    public static UpdateMatchesProcessDialog newInstance(Event event) {
        UpdateMatchesProcessDialog updateMatchesProcessDialog = new UpdateMatchesProcessDialog();
        Bundle bundle = new Bundle();
        bundle.putLong(DatabaseActivity.PARENT_ID, event.getId());
        updateMatchesProcessDialog.setArguments(bundle);
        return updateMatchesProcessDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Event event = mDbManager.getDaoSession().getEventDao().load(getArguments().getLong(DatabaseActivity.PARENT_ID));
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
            this.url = TBA.BASE_TBA_URL + String.format(TBA.EVENT_REQUEST, event.getFmsid());

        }

        @Override
        protected Void doInBackground(Void... params) {
            //Delete all match data
            List<Match> data = mDbManager.getDaoSession().getMatchDao().queryBuilder().where(MatchDao.Properties.EventId.eq(event.getId())).list();
            for (Match match : data) {
                mDbManager.deleteMatch(match);
            }

            final JsonArray jMatches = JSON.getAsJsonArray(HTTP.dataFromResponse(HTTP.getResponse(url + "/matches")));

            JSON.set_daoSession(mDbManager);
            for (JsonElement element : jMatches) {
                //Save all the matches and alliances
                Match match = JSON.getGson().fromJson(element, Match.class);
                //Only save Qualifications
                /*if (match.getType().contains("qm")) {
                    mDbManager.insert(match);
                }*/
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ((ListFragment) getParentFragment()).updateList();
            dismissAllowingStateLoss();
        }
    }

}
