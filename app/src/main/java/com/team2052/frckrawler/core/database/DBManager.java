package com.team2052.frckrawler.core.database;

import android.content.Context;

import com.team2052.frckrawler.db.*;

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

//TODO
public class DBManager {

    private static DBManager instance;

    private final MatchDataDao matchDataDao;
    private final PitDataDao pitDataDao;
    private final MatchCommentDao matchCommentDao;
    private final RobotEventDao robotEventDao;
    private final MatchDao matchDao;
    private final EventDao eventDao;
    private final RobotDao robotDao;
    private final MetricDao metricDao;
    private final GameDao gameDao;
    private final ContactDao contactDao;
    private final RobotPhotoDao robotPhotoDao;
    private final UserDao userDao;

    private Context context;
    private DaoSession daoSession;

    private TeamDao teamDao;

    private DBManager(Context context, DaoSession daoSession) {
        this.context = context;
        this.daoSession = daoSession;

        //DAO's
        matchDataDao = daoSession.getMatchDataDao();
        pitDataDao = daoSession.getPitDataDao();
        matchCommentDao = daoSession.getMatchCommentDao();
        robotEventDao = daoSession.getRobotEventDao();
        matchDao = daoSession.getMatchDao();
        eventDao = daoSession.getEventDao();
        robotDao = daoSession.getRobotDao();
        metricDao = daoSession.getMetricDao();
        gameDao = daoSession.getGameDao();
        contactDao = daoSession.getContactDao();
        teamDao = daoSession.getTeamDao();
        robotPhotoDao = daoSession.getRobotPhotoDao();
        userDao = daoSession.getUserDao();
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
        for (Event event : game.getEventList()) {
            deleteEvent(event);
        }

        for (Metric metric : game.getMetricList()) {
            deleteMetric(metric);
        }

        for (Robot robot : game.getRobotList()) {
            deleteRobot(robot);
        }

        gameDao.delete(game);
    }

    /**
     * Used to delete an event deletes all other references
     *
     * @param event
     */
    public void deleteEvent(Event event) {
        for (MatchComment comment : event.getMatchCommentList()) {
            deleteMatchComment(comment);
        }
    }

    /**
     * Used to delete an event deletes all other references
     *
     * @param matchComment
     */
    public void deleteMatchComment(MatchComment matchComment) {
        matchCommentDao.delete(matchComment);
    }


    /**
     * Used to delete a robotEvent deletes all other references
     *
     * @param robotEvent
     */
    public void deleteRobotEvent(RobotEvent robotEvent) {
        robotEventDao.delete(robotEvent);
    }


    /**
     * Used to delete a team deletes all other references
     *
     * @param team
     */
    public void deleteTeam(Team team) {
        teamDao.delete(team);
    }

    /**
     * Used to delete a contact deletes all other references
     *
     * @param contact
     */
    public void deleteContact(Contact contact) {
        contactDao.delete(contact);
    }

    /**
     * Used to delete a match deletes all other references
     *
     * @param match
     */
    public void deleteMatch(Match match) {
        matchDao.delete(match);
    }


    /**
     * Used to delete a robot deletes all other references
     *
     * @param robot
     */
    public void deleteRobot(Robot robot) {
        robotDao.delete(robot);
    }

    /**
     * Used to delete a metric deletes all other references
     *
     * @param metric
     */
    public void deleteMetric(Metric metric) {
        metricDao.delete(metric);
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
        robotPhotoDao.delete(robotPhoto);
    }

    /**
     * Used to delete pitData metric deletes all other references
     *
     * @param pitData
     */
    public void deletePitData(PitData pitData) {
        pitDataDao.delete(pitData);
    }


    /**
     * Used to delete matchData metric deletes all other references
     *
     * @param matchData
     */
    public void deleteMatchData(MatchData matchData) {
        matchDataDao.delete(matchData);
    }

    public List<Team> getTeamsForMatch(Match match) {
        return null;
    }

    public long getTeamId(RobotEvent robotEvent) {
        return robotDao.load(robotEvent.getRobotId()).getTeamId();
    }

    public Team getTeam(RobotEvent robotEvent) {
        return teamDao.load(robotDao.load(robotEvent.getRobotId()).getTeamId());
    }

    public Team getTeam(long pk) {
        return teamDao.load(pk);
    }

    public Robot getRobot(RobotEvent robotEvent) {
        return robotDao.load(robotEvent.getRobotId());
    }

    public Game getGame(Event event) {
        return gameDao.load(event.getGameId());
    }

    public long getEventId(MatchData matchData) {
        return matchDao.load(matchData.getMatchId()).getEventId();
    }

    public List<Robot> getRobots(Event event) {
        List<Robot> robots = new ArrayList<>();
        for (RobotEvent robotEvent : event.getRobotEventList()) {
            robots.add(getRobot(robotEvent));
        }
        return robots;
    }

    public Match getMatch(MatchData matchData) {
        return matchDao.load(matchData.getMatchId());
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public Match getMatch(MatchComment matchComment) {
        return matchDao.load(matchComment.getMatchId());
    }

    public Game getGame(Robot mRobot) {
        return gameDao.load(mRobot.getGameId());
    }


    /*INSERTERS - UPDATES OR INSERTS DATA*/

    public boolean insertPitData(PitData pitData) {
        QueryBuilder<PitData> pitDataQueryBuilder = pitDataDao.queryBuilder();
        pitDataQueryBuilder.where(PitDataDao.Properties.RobotId.eq(pitData.getRobotId()));
        pitDataQueryBuilder.where(PitDataDao.Properties.MetricId.eq(pitData.getMetricId()));
        pitDataQueryBuilder.where(PitDataDao.Properties.EventId.eq(pitData.getEventId()));
        PitData unique = pitDataQueryBuilder.unique();

        if (unique != null) {
            unique.setData(pitData.getData());
            pitDataDao.update(unique);
            return false;
        } else {
            pitDataDao.insert(pitData);
            return true;
        }
    }


    public boolean insertMatchData(MatchData matchData) {
        QueryBuilder<MatchData> matchDataQueryBuilder = matchDataDao.queryBuilder();
        matchDataQueryBuilder.where(MatchDataDao.Properties.RobotId.eq(matchData.getRobotId()));
        matchDataQueryBuilder.where(MatchDataDao.Properties.MetricId.eq(matchData.getMetricId()));
        matchDataQueryBuilder.where(MatchDataDao.Properties.MatchId.eq(matchData.getMatchId()));
        matchDataQueryBuilder.where(MatchDataDao.Properties.EventId.eq(matchData.getEventId()));
        MatchData unique = matchDataQueryBuilder.unique();


        if (unique != null) {
            unique.setData(matchData.getData());
            matchDataDao.update(unique);
            return false;
        } else {
            matchDataDao.insert(matchData);
            return true;
        }
    }

    public boolean insertMatchComment(MatchComment matchComment) {
        QueryBuilder<MatchComment> matchCommentQueryBuilder = matchCommentDao.queryBuilder();
        matchCommentQueryBuilder.where(MatchCommentDao.Properties.EventId.eq(matchComment.getEventId()));
        matchCommentQueryBuilder.where(MatchCommentDao.Properties.RobotId.eq(matchComment.getRobotId()));
        matchCommentQueryBuilder.where(MatchCommentDao.Properties.MatchId.eq(matchComment.getMatchId()));
        MatchComment currentData = matchCommentQueryBuilder.unique();
        if (currentData != null) {
            currentData.setComment(matchComment.getComment());
            matchCommentDao.update(currentData);
            return false;
        } else {
            matchCommentDao.insert(matchComment);
            return true;
        }
    }
}
