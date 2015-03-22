package com.team2052.frckrawler.core.database;

import android.content.Context;

import com.team2052.frckrawler.db.Contact;
import com.team2052.frckrawler.db.ContactDao;
import com.team2052.frckrawler.db.DaoSession;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.EventDao;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.db.Match;
import com.team2052.frckrawler.db.MatchDao;
import com.team2052.frckrawler.db.MatchData;
import com.team2052.frckrawler.db.MatchDataDao;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.MetricDao;
import com.team2052.frckrawler.db.PitData;
import com.team2052.frckrawler.db.PitDataDao;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.db.RobotDao;
import com.team2052.frckrawler.db.RobotEvent;
import com.team2052.frckrawler.db.RobotEventDao;
import com.team2052.frckrawler.db.RobotPhoto;
import com.team2052.frckrawler.db.RobotPhotoDao;
import com.team2052.frckrawler.db.Team;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Used to keep a clean database
 * There is no CASCADE ON DELETE function with greenDAO, so we have to configure it manually.
 *
 * @author Adam
 * @since 10/7/2014
 */
public class DBManager {

    private static DBManager instance;
    private Context context;
    private DaoSession daoSession;

    private DBManager(Context context, DaoSession daoSession) {

        this.context = context;
        this.daoSession = daoSession;
    }

    public static DBManager getInstance(Context context, DaoSession daoSession) {
        if (instance == null) {
            synchronized (DBManager.class) {
                if (instance == null) {
                    instance = new DBManager(context, daoSession);
                }
            }
        }
        return instance;
    }

    /**
     * Used to delete a game deletes all other references
     *
     * @param game
     */
    public void deleteGame(Game game) {
        for (Event event : daoSession.getEventDao().queryBuilder().where(EventDao.Properties.GameId.eq(game.getId())).list()) {
            deleteEvent(event);
        }

        for (Robot robot : daoSession.getRobotDao().queryBuilder().where(RobotDao.Properties.GameId.eq(game.getId())).list()) {
            deleteRobot(robot);
        }

        for (Metric metric : daoSession.getMetricDao().queryBuilder().where(MetricDao.Properties.GameId.eq(game.getId())).list()) {
            deleteMetric(metric);
        }
        daoSession.getGameDao().delete(game);
    }

    /**
     * Used to delete an event deletes all other references
     *
     * @param event
     */
    public void deleteEvent(Event event) {
        for (Match match : daoSession.getMatchDao().queryBuilder().where(MatchDao.Properties.EventId.eq(event.getId())).list()) {
            deleteMatch(match);
        }

        for (RobotEvent robotEvent : daoSession.getRobotEventDao().queryBuilder().where(RobotEventDao.Properties.EventId.eq(event.getId())).list()) {
            deleteRobotEvent(robotEvent);
        }

        for (PitData pitData : daoSession.getPitDataDao().queryBuilder().where(PitDataDao.Properties.EventId.eq(event.getId())).list()) {
            deletePitData(pitData);
        }

        for (MatchData pitData : daoSession.getMatchDataDao().queryBuilder().where(MatchDataDao.Properties.EventId.eq(event.getId())).list()) {
            deleteMatchData(pitData);
        }
        daoSession.getEventDao().delete(event);
    }

    /**
     * Used to delete a robotEvent deletes all other references
     *
     * @param robotEvent
     */
    public void deleteRobotEvent(RobotEvent robotEvent) {
        daoSession.getRobotEventDao().delete(robotEvent);
    }


    /**
     * Used to delete a team deletes all other references
     *
     * @param team
     */
    public void deleteTeam(Team team) {
        for (Contact contact : daoSession.getContactDao().queryBuilder().where(ContactDao.Properties.TeamId.eq(team.getNumber())).list()) {
            deleteContact(contact);
        }

        QueryBuilder<Match> matchQueryBuilder = daoSession.getMatchDao().queryBuilder();
        matchQueryBuilder.where(MatchDao.Properties.Blue1Id.eq(team.getNumber()));
        matchQueryBuilder.where(MatchDao.Properties.Blue2Id.eq(team.getNumber()));
        matchQueryBuilder.where(MatchDao.Properties.Blue3Id.eq(team.getNumber()));
        matchQueryBuilder.where(MatchDao.Properties.Red1Id.eq(team.getNumber()));
        matchQueryBuilder.where(MatchDao.Properties.Red2Id.eq(team.getNumber()));
        matchQueryBuilder.where(MatchDao.Properties.Red3Id.eq(team.getNumber()));

        for (Match match : matchQueryBuilder.list()) {
            deleteMatch(match);
        }

        for (Robot robot : daoSession.getRobotDao().queryBuilder().where(RobotDao.Properties.TeamId.eq(team.getNumber())).list()) {
            deleteRobot(robot);
        }

        daoSession.getTeamDao().delete(team);
    }

    /**
     * Used to delete a contact deletes all other references
     *
     * @param contact
     */
    public void deleteContact(Contact contact) {
        daoSession.getContactDao().delete(contact);
    }

    /**
     * Used to delete a match deletes all other references
     *
     * @param match
     */
    public void deleteMatch(Match match) {
        for (MatchData data : daoSession.getMatchDataDao().queryBuilder().where(MatchDataDao.Properties.MatchId.eq(match.getId())).list()) {
            deleteMatchData(data);
        }
        daoSession.getMatchDao().delete(match);
    }


    /**
     * Used to delete a robot deletes all other references
     *
     * @param robot
     */
    public void deleteRobot(Robot robot) {
        for (RobotEvent robotEvent : daoSession.getRobotEventDao().queryBuilder().where(RobotEventDao.Properties.RobotId.eq(robot.getId())).list()) {
            deleteRobotEvent(robotEvent);
        }

        for (RobotPhoto robotPhoto : daoSession.getRobotPhotoDao().queryBuilder().where(RobotPhotoDao.Properties.RobotId.eq(robot.getId())).list()) {
            deleteRobotPhoto(robotPhoto);
        }

        for (PitData pitData : daoSession.getPitDataDao().queryBuilder().where(PitDataDao.Properties.RobotId.eq(robot.getId())).list()) {
            deletePitData(pitData);
        }

        for (MatchData matchData : daoSession.getMatchDataDao().queryBuilder().where(MatchDataDao.Properties.RobotId.eq(robot.getId())).list()) {
            deleteMatchData(matchData);
        }

        daoSession.getRobotDao().delete(robot);
    }

    /**
     * Used to delete a metric deletes all other references
     *
     * @param metric
     */
    public void deleteMetric(Metric metric) {
        daoSession.getMetricDao().delete(metric);
    }

    /**
     * Used to delete a robotPhoto deletes all other references
     * It also deletes the file
     *
     * @param robotPhoto
     */
    public void deleteRobotPhoto(RobotPhoto robotPhoto) {
        //Delete the file
        new File(robotPhoto.getLocation()).delete();
        daoSession.getRobotPhotoDao().delete(robotPhoto);
    }

    /**
     * Used to delete pitData metric deletes all other references
     *
     * @param pitData
     */
    public void deletePitData(PitData pitData) {
        daoSession.getPitDataDao().delete(pitData);
    }


    /**
     * Used to delete matchData metric deletes all other references
     *
     * @param matchData
     */
    public void deleteMatchData(MatchData matchData) {
        daoSession.getMatchDataDao().delete(matchData);
    }

    public List<Team> getTeamsForMatch(Match match) {
        List<Team> teams = new ArrayList<>();
        teams.add(match.getBlue1());
        teams.add(match.getBlue2());
        teams.add(match.getBlue3());
        teams.add(match.getRed1());
        teams.add(match.getRed2());
        teams.add(match.getRed3());
        return teams;
    }



}
