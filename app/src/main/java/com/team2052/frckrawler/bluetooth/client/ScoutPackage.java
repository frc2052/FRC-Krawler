package com.team2052.frckrawler.bluetooth.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.team2052.frckrawler.GlobalValues;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.Schedule;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.db.Match;
import com.team2052.frckrawler.db.MatchComment;
import com.team2052.frckrawler.db.MatchData;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.PitData;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.db.RobotEvent;
import com.team2052.frckrawler.db.Team;
import com.team2052.frckrawler.db.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A object that contains all the data that is sent to the scout
 * Easier to manage
 *
 * @author Adam
 * @since 12/24/2014.
 */
public class ScoutPackage implements Serializable {
    private final ArrayList<Team> teams = new ArrayList<>();
    private final List<PitData> pitData;
    private final Schedule schedule;
    private final List<Metric> metrics;
    private final List<User> users;
    private final List<RobotEvent> robot_events;
    private final List<Robot> robots = new ArrayList<>();
    private final Event event;
    private final List<MatchData> matchData;
    private final List<MatchComment> matchComments;
    private final Game game;
    private final String TAG = ScoutPackage.class.getSimpleName();

    public ScoutPackage(DBManager dbManager, Event event) {
        this.game = dbManager.mGames.load(event.getGame_id());
        this.event = event;
        users = dbManager.mUsers.loadAll();
        game.resetMetricList();
        metrics = game.getMetricList();

        robot_events = event.getRobotEventList();

        for (RobotEvent robotEvent : robot_events) {
            robots.add(dbManager.mRobotEvents.getRobot(robotEvent));
        }

        schedule = new Schedule(event);
        pitData = dbManager.mPitDatas.query(null, null, event.getId(), null).list();
        matchData = dbManager.mMatchDatas.query(null, null, null, null, event.getId(), null).list();
        matchComments = dbManager.mMatchComments.query(null, null, null, event.getId()).list();

        for (RobotEvent robotEvent : robot_events) {
            teams.add(dbManager.mRobotEvents.getTeam(robotEvent));
        }
    }

    public void save(final DBManager dbManager, Context context) {
        Log.d(TAG, "Saving");
        dbManager.runInTx(() -> {
                    for (Metric metric : metrics) {
                        dbManager.mMetrics.insert(metric);
                    }

                    for (User user : users) {
                        dbManager.mUsers.insert(user);
                    }

                    for (RobotEvent robotEvent : robot_events) {
                        dbManager.mRobotEvents.insert(robotEvent);
                    }

                    for (Robot robot : robots) {
                        dbManager.mRobots.insert(robot);
                    }

                    for (Team team : teams) {
                        dbManager.mTeams.insert(team);
                    }

                    for (Match match : schedule.matches) {
                        dbManager.mMatches.insert(match);
                    }

                    dbManager.mEvents.insert(event);
                    dbManager.mGames.insert(game);
                }
        );

        SharedPreferences sharedPreferences = context.getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(GlobalValues.CURRENT_SCOUT_EVENT_ID, event.getId());
        editor.apply();
    }

    public Event getEvent() {
        return event;
    }
}
