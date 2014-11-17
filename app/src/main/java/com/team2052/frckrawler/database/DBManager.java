package com.team2052.frckrawler.database;

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

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Used to keep a clean database
 * There is no CASCADE ON DELETE function with greenDAO, so we have to configure it manually.
 *
 * @author Adam
 * @since 10/7/2014
 */
public class DBManager
{
    /**
     * Used to delete a game deletes all other references
     *
     * @param daoSession
     * @param game
     */
    public static void deleteGame(DaoSession daoSession, Game game)
    {
        for (Event event : daoSession.getEventDao().queryBuilder().where(EventDao.Properties.GameId.eq(game.getId())).list()) {
            deleteEvent(daoSession, event);
        }

        for (Robot robot : daoSession.getRobotDao().queryBuilder().where(RobotDao.Properties.GameId.eq(game.getId())).list()) {
            deleteRobot(daoSession, robot);
        }

        for (Metric metric : daoSession.getMetricDao().queryBuilder().where(MetricDao.Properties.GameId.eq(game.getId())).list()) {
            deleteMetric(daoSession, metric);
        }
        daoSession.getGameDao().delete(game);
    }

    /**
     * Used to delete an event deletes all other references
     *
     * @param daoSession
     * @param event
     */
    public static void deleteEvent(DaoSession daoSession, Event event)
    {
        for (Match match : daoSession.getMatchDao().queryBuilder().where(MatchDao.Properties.EventId.eq(event.getId())).list()) {
            deleteMatch(daoSession, match);
        }

        for (RobotEvent robotEvent : daoSession.getRobotEventDao().queryBuilder().where(RobotEventDao.Properties.EventId.eq(event.getId())).list()) {
            deleteRobotEvent(daoSession, robotEvent);
        }

        for (PitData pitData : daoSession.getPitDataDao().queryBuilder().where(PitDataDao.Properties.EventId.eq(event.getId())).list()) {
            deletePitData(daoSession, pitData);
        }

        daoSession.getEventDao().delete(event);
    }

    /**
     * Used to delete a robotEvent deletes all other references
     *
     * @param daoSession
     * @param robotEvent
     */
    public static void deleteRobotEvent(DaoSession daoSession, RobotEvent robotEvent)
    {
        daoSession.getRobotEventDao().delete(robotEvent);
    }


    /**
     * Used to delete a team deletes all other references
     *
     * @param daoSession
     * @param team
     */
    public static void deleteTeam(DaoSession daoSession, Team team)
    {
        for (Contact contact : daoSession.getContactDao().queryBuilder().where(ContactDao.Properties.TeamId.eq(team.getNumber())).list()) {
            deleteContact(daoSession, contact);
        }

        QueryBuilder<Match> matchQueryBuilder = daoSession.getMatchDao().queryBuilder();
        matchQueryBuilder.where(MatchDao.Properties.Blue1Id.eq(team.getNumber()));
        matchQueryBuilder.where(MatchDao.Properties.Blue2Id.eq(team.getNumber()));
        matchQueryBuilder.where(MatchDao.Properties.Blue3Id.eq(team.getNumber()));
        matchQueryBuilder.where(MatchDao.Properties.Red1Id.eq(team.getNumber()));
        matchQueryBuilder.where(MatchDao.Properties.Red2Id.eq(team.getNumber()));
        matchQueryBuilder.where(MatchDao.Properties.Red3Id.eq(team.getNumber()));

        for (Match match : matchQueryBuilder.list()) {
            deleteMatch(daoSession, match);
        }

        for (Robot robot : daoSession.getRobotDao().queryBuilder().where(RobotDao.Properties.TeamId.eq(team.getNumber())).list()) {
            deleteRobot(daoSession, robot);
        }

        daoSession.getTeamDao().delete(team);
    }

    /**
     * Used to delete a contact deletes all other references
     *
     * @param daoSession
     * @param contact
     */
    public static void deleteContact(DaoSession daoSession, Contact contact)
    {
        daoSession.getContactDao().delete(contact);
    }

    /**
     * Used to delete a match deletes all other references
     *
     * @param daoSession
     * @param match
     */
    public static void deleteMatch(DaoSession daoSession, Match match)
    {
        daoSession.getMatchDao().delete(match);
    }


    /**
     * Used to delete a robot deletes all other references
     *
     * @param daoSession
     * @param robot
     */
    public static void deleteRobot(DaoSession daoSession, Robot robot)
    {
        for (RobotEvent robotEvent : daoSession.getRobotEventDao().queryBuilder().where(RobotEventDao.Properties.RobotId.eq(robot.getId())).list()) {
            deleteRobotEvent(daoSession, robotEvent);
        }

        for (RobotPhoto robotPhoto : daoSession.getRobotPhotoDao().queryBuilder().where(RobotPhotoDao.Properties.RobotId.eq(robot.getId())).list()) {
            deleteRobotPhoto(daoSession, robotPhoto);
        }

        for (PitData pitData : daoSession.getPitDataDao().queryBuilder().where(PitDataDao.Properties.RobotId.eq(robot.getId())).list()) {
            deletePitData(daoSession, pitData);
        }

        for (MatchData matchData : daoSession.getMatchDataDao().queryBuilder().where(MatchDataDao.Properties.RobotId.eq(robot.getId())).list()) {
            deleteMatchData(daoSession, matchData);
        }

        daoSession.getRobotDao().delete(robot);
    }

    /**
     * Used to delete a metric deletes all other references
     *
     * @param daoSession
     * @param metric
     */
    public static void deleteMetric(DaoSession daoSession, Metric metric)
    {
        daoSession.getMetricDao().delete(metric);
    }

    /**
     * Used to delete a robotPhoto deletes all other references
     * It also deletes the file
     *
     * @param daoSession
     * @param robotPhoto
     */
    public static void deleteRobotPhoto(DaoSession daoSession, RobotPhoto robotPhoto)
    {
        //Delete the file
        new File(robotPhoto.getLocation()).delete();
        daoSession.getRobotPhotoDao().delete(robotPhoto);
    }

    /**
     * Used to delete pitData metric deletes all other references
     *
     * @param daoSession
     * @param pitData
     */
    public static void deletePitData(DaoSession daoSession, PitData pitData)
    {
        daoSession.getPitDataDao().delete(pitData);
    }


    /**
     * Used to delete matchData metric deletes all other references
     *
     * @param daoSession
     * @param matchData
     */
    public static void deleteMatchData(DaoSession daoSession, MatchData matchData)
    {
        daoSession.getMatchDataDao().delete(matchData);
    }

}
