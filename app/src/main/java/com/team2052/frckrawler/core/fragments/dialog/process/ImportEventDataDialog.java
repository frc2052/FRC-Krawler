package com.team2052.frckrawler.core.fragments.dialog.process;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.team2052.frckrawler.core.FRCKrawler;
import com.team2052.frckrawler.core.activities.DatabaseActivity;
import com.team2052.frckrawler.core.database.DBManager;
import com.team2052.frckrawler.core.tba.HTTP;
import com.team2052.frckrawler.core.tba.JSON;
import com.team2052.frckrawler.core.tba.TBA;
import com.team2052.frckrawler.core.util.LogHelper;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.db.Match;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.db.RobotDao;
import com.team2052.frckrawler.db.RobotEvent;
import com.team2052.frckrawler.db.RobotEventDao;
import com.team2052.frckrawler.db.Team;

/**
 * @author Adam
 * @since 3/9/2015.
 */
public class ImportEventDataDialog extends BaseProgressDialog {

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
        Game game = mDbManager.getDaoSession().getGameDao().load(getArguments().getLong(DatabaseActivity.PARENT_ID));
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
            final DBManager daoSession = ((FRCKrawler) getActivity().getApplication()).getDBSession();
            //Get all data from TBA ready to parse

            publishProgress("Downloading Data");
            final JsonElement jEvent = JSON.getAsJsonObject(HTTP.dataFromResponse(HTTP.getResponse(url)));
            final JsonArray jTeams = JSON.getAsJsonArray(HTTP.dataFromResponse(HTTP.getResponse(url + "/teams")));
            final JsonArray jMatches = JSON.getAsJsonArray(HTTP.dataFromResponse(HTTP.getResponse(url + "/matches")));

            //Run in bulk transaction
            daoSession.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    //Save the event

                    Event event = JSON.getGson().fromJson(jEvent, Event.class);
                    event.setGameId(game.getId());
                    daoSession.getDaoSession().getEventDao().insert(event);


                    //Save the teams
                    publishProgress("Saving Teams");
                    for (JsonElement element : jTeams) {
                        //Convert json element to team
                        Team team = JSON.getGson().fromJson(element, Team.class);
                        daoSession.getDaoSession().getTeamDao().insertOrReplace(team);
                        //Create a robot and save that robot to the database as well with the team
                        Robot robot = daoSession.getDaoSession().getRobotDao().queryBuilder().where(RobotDao.Properties.GameId.eq(game.getId())).where(RobotDao.Properties.TeamId.eq(team.getNumber())).unique();

                        if (robot == null) {
                            daoSession.getDaoSession().getRobotEventDao().insert(new RobotEvent(null, daoSession.getDaoSession().getRobotDao().insert(new Robot(null, team.getNumber(), game.getId(), null)), event.getId(), null));
                        } else {
                            RobotEvent robotEvents = daoSession.getDaoSession().getRobotEventDao().queryBuilder().where(RobotEventDao.Properties.RobotId.eq(robot.getId())).where(RobotEventDao.Properties.EventId.eq(event.getId())).unique();
                            if (robotEvents == null) {
                                daoSession.getDaoSession().getRobotEventDao().insert(new RobotEvent(null, robot.getId(), event.getId(), null));
                            }
                        }
                    }
                    publishProgress("Saving Matches");
                    JSON.set_daoSession(daoSession);
                    for (JsonElement element : jMatches) {
                        //Save all the matches and alliances
                        Match match = JSON.getGson().fromJson(element, Match.class);
                        //Only save Qualifications
                        if (match.getType().contains("qm")) {
                            daoSession.getDaoSession().insert(match);
                        }
                    }
                    LogHelper.debug("Saved " + event.getName() + " In " + (System.currentTimeMillis() - startTime) + "ms");
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ImportEventDataDialog.this.dismissAllowingStateLoss();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            ((ProgressDialog) getDialog()).setMessage(values[0]);
        }


    }
}

