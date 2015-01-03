package com.team2052.frckrawler.client;

import android.content.Context;
import android.content.SharedPreferences;

import com.team2052.frckrawler.core.GlobalValues;
import com.team2052.frckrawler.core.database.Schedule;
import com.team2052.frckrawler.db.DaoSession;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Match;
import com.team2052.frckrawler.db.MatchComment;
import com.team2052.frckrawler.db.MatchCommentDao;
import com.team2052.frckrawler.db.MatchDao;
import com.team2052.frckrawler.db.MatchData;
import com.team2052.frckrawler.db.MatchDataDao;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.MetricDao;
import com.team2052.frckrawler.db.PitData;
import com.team2052.frckrawler.db.PitDataDao;
import com.team2052.frckrawler.db.RobotEvent;
import com.team2052.frckrawler.db.RobotEventDao;
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
    private final List<RobotEvent> robots;
    private final Event event;
    private final List<MatchData> matchData;
    private final List<MatchComment> matchComments;

    public ScoutPackage(DaoSession session, Event event) {
        this.event = event;
        users = session.getUserDao().loadAll();
        metrics = session.getMetricDao().queryDeep("WHERE " + MetricDao.Properties.GameId.columnName + " = " + event.getGame().getId());
        robots = session.getRobotEventDao().queryDeep("WHERE " + RobotEventDao.Properties.EventId.columnName + " = " + event.getId());
        schedule = new Schedule(event, session.getMatchDao().queryDeep("WHERE " + MatchDao.Properties.EventId.columnName + " = " + event.getId()));
        pitData = session.getPitDataDao().queryBuilder().where(PitDataDao.Properties.EventId.eq(event.getId())).list();
        matchData = session.getMatchDataDao().queryBuilder().where(MatchDataDao.Properties.EventId.eq(event.getId())).list();
        matchComments = session.getMatchCommentDao().queryBuilder().where(MatchCommentDao.Properties.EventId.eq(event.getId())).list();
        for (RobotEvent robotEvent : robots) {
            teams.add(robotEvent.getRobot().getTeam());
        }
    }

    public void save(final DaoSession session, Context context) {
        session.runInTx(new Runnable() {
            @Override
            public void run() {
                for (Metric metric : metrics) {
                    session.insertOrReplace(metric);
                }

                for (User user : users) {
                    session.insertOrReplace(user);
                }

                for (RobotEvent robotEvent : robots) {
                    session.insert(robotEvent);
                    session.insertOrReplace(robotEvent.getRobot());
                }

                for (Team team : teams) {
                    session.insertOrReplace(team);
                }

                for (Match match : schedule.matches) {
                    session.insertOrReplace(match);
                }

                for (PitData pitValues : pitData) {
                    session.insertOrReplace(pitValues);
                }

                for (MatchData matchValues : matchData) {
                    session.insertOrReplace(matchValues);
                }

                for (MatchComment matchComment : matchComments) {
                    session.insertOrReplace(matchComment);
                }

                session.insertOrReplace(event);
                session.insertOrReplace(event.getGame());
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
