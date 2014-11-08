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

    public static void deleteRobotEvent(DaoSession daoSession, RobotEvent robotEvent)
    {
        daoSession.getRobotEventDao().delete(robotEvent);
    }

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

    public static void deleteContact(DaoSession daoSession, Contact contact)
    {
        daoSession.getContactDao().delete(contact);
    }

    public static void deleteMatch(DaoSession daoSession, Match match)
    {
        daoSession.getMatchDao().delete(match);
    }

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

    public static void deleteMetric(DaoSession daoSession, Metric metric)
    {
        daoSession.getMetricDao().delete(metric);
    }

    public static void deleteRobotPhoto(DaoSession daoSession, RobotPhoto robotPhoto)
    {
        daoSession.getRobotPhotoDao().delete(robotPhoto);
    }

    public static void deletePitData(DaoSession daoSession, PitData pitData)
    {
        daoSession.getPitDataDao().delete(pitData);
    }

    public static void deleteMatchData(DaoSession daoSession, MatchData matchData)
    {
        daoSession.getMatchDataDao().delete(matchData);
    }

}
