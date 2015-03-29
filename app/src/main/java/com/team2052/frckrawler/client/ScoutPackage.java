package com.team2052.frckrawler.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.team2052.frckrawler.core.GlobalValues;
import com.team2052.frckrawler.core.database.DBManager;
import com.team2052.frckrawler.core.database.Schedule;
import com.team2052.frckrawler.db.DaoSession;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.db.Match;
import com.team2052.frckrawler.db.MatchComment;
import com.team2052.frckrawler.db.MatchCommentDao;
import com.team2052.frckrawler.db.MatchData;
import com.team2052.frckrawler.db.MatchDataDao;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.PitData;
import com.team2052.frckrawler.db.PitDataDao;
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

    public ScoutPackage(DBManager dbManager, Event event) {
        DaoSession session = dbManager.getDaoSession();

        this.game = session.getGameDao().load(event.getGameId());
        this.event = event;
        users = session.getUserDao().loadAll();
        metrics = game.getMetricList();
        robot_events = event.getRobotEventList();

        for (RobotEvent robotEvent : robot_events) {
            robots.add(session.getRobotDao().load(robotEvent.getRobotId()));
        }

        schedule = new Schedule(event, event.getMatchList());
        pitData = session.getPitDataDao().queryBuilder().where(PitDataDao.Properties.EventId.eq(event.getId())).list();
        matchData = session.getMatchDataDao().queryBuilder().where(MatchDataDao.Properties.EventId.eq(event.getId())).list();
        matchComments = session.getMatchCommentDao().queryBuilder().where(MatchCommentDao.Properties.EventId.eq(event.getId())).list();

        for (RobotEvent robotEvent : robot_events) {
            teams.add(session.getTeamDao().load(session.getRobotDao().load(robotEvent.getRobotId()).getTeamId()));
        }
    }

    public void save(final DBManager session, Context context) {
        session.getDaoSession().runInTx(new Runnable() {
            @Override
            public void run() {
                for (Metric metric : metrics) {
                    session.getDaoSession().insertOrReplace(metric);
                }

                for (User user : users) {
                    session.getDaoSession().insertOrReplace(user);
                }

                for (RobotEvent robotEvent : robot_events) {
                    session.getDaoSession().insert(robotEvent);
                }

                for (Robot robot : robots) {
                    session.getDaoSession().insertOrReplace(robot);
                }

                for (Team team : teams) {
                    session.getDaoSession().insertOrReplace(team);
                }

                for (Match match : schedule.matches) {
                    session.getDaoSession().insertOrReplace(match);
                }

                for (PitData pitValues : pitData) {
                    session.getDaoSession().insertOrReplace(pitValues);
                }

                for (MatchData matchValues : matchData) {
                    Log.i("FRCKrawler", matchValues.getData());
                    session.getDaoSession().insertOrReplace(matchValues);
                }

                for (MatchComment matchComment : matchComments) {
                    session.getDaoSession().insertOrReplace(matchComment);
                }

                session.getDaoSession().insertOrReplace(event);
                session.getDaoSession().insertOrReplace(game);
            }
        });

        SharedPreferences sharedPreferences = context.getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(GlobalValues.CURRENT_SCOUT_EVENT_ID, event.getId());
        editor.apply();
    }

    public Event getEvent() {
        return event;
    }
}
