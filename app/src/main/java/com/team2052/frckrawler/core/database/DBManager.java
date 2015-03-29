package com.team2052.frckrawler.core.database;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.core.tba.JSON;
import com.team2052.frckrawler.db.Contact;
import com.team2052.frckrawler.db.ContactDao;
import com.team2052.frckrawler.db.DaoSession;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.EventDao;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.db.GameDao;
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
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.db.RobotDao;
import com.team2052.frckrawler.db.RobotEvent;
import com.team2052.frckrawler.db.RobotEventDao;
import com.team2052.frckrawler.db.RobotPhoto;
import com.team2052.frckrawler.db.RobotPhotoDao;
import com.team2052.frckrawler.db.Team;
import com.team2052.frckrawler.db.TeamDao;
import com.team2052.frckrawler.db.UserDao;

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

    public Robot getRobot(long team_number, long game_id) {
        QueryBuilder<Robot> robotQueryBuilder = robotDao.queryBuilder();
        robotQueryBuilder.where(RobotDao.Properties.GameId.eq(game_id));
        robotQueryBuilder.where(RobotDao.Properties.TeamId.eq(team_number));
        return robotQueryBuilder.unique();
    }

    /**
     * Loads robots
     * Either one can be null to not query the column
     * Nulling both will load all robots
     *
     * @param team_number can be null
     * @param game_id     can be null
     * @return
     */
    public List<Robot> getRobots(Long team_number, Long game_id) {
        QueryBuilder<Robot> robotQueryBuilder = robotDao.queryBuilder();
        if (team_number != null || game_id != null) {
            if (team_number != null)
                robotQueryBuilder.where(RobotDao.Properties.TeamId.eq(team_number));
            if (game_id != null)
                robotQueryBuilder.where(RobotDao.Properties.GameId.eq(game_id));
            return robotQueryBuilder.list();
        }
        return robotDao.loadAll();
    }


    public List<MatchData> getMatchData(Long robotId, Long metricId, Long matchId, Long eventId, Long userId) {
        QueryBuilder<MatchData> matchDataQueryBuilder = matchDataDao.queryBuilder();
        if (robotId != null || metricId != null || matchId != null || eventId != null || userId != null) {
            if (robotId != null) {
                matchDataQueryBuilder.where(MatchDataDao.Properties.RobotId.eq(robotId));
            }
            if (metricId != null) {
                matchDataQueryBuilder.where(MatchDataDao.Properties.MetricId.eq(metricId));
            }
            if (matchId != null) {
                matchDataQueryBuilder.where(MatchDataDao.Properties.MatchId.eq(matchId));
            }
            if (eventId != null) {
                matchDataQueryBuilder.where(MatchDataDao.Properties.EventId.eq(eventId));
            }
            if (userId != null) {
                matchDataQueryBuilder.where(MatchDataDao.Properties.UserId.eq(userId));
            }
            return matchDataQueryBuilder.list();
        }
        return matchDataDao.loadAll();
    }

    public List<Team> getTeams(Match match) {
        JsonObject alliances = JSON.getAsJsonObject(match.getData()).get("alliances").getAsJsonObject();
        List<Team> teams = new ArrayList<>();
        JsonArray red = alliances.get("red").getAsJsonObject().get("teams").getAsJsonArray();
        JsonArray blue = alliances.get("blue").getAsJsonObject().get("teams").getAsJsonArray();
        teams.add(getTeam(Long.parseLong(red.get(0).getAsString().replace("frc", ""))));
        teams.add(getTeam(Long.parseLong(red.get(1).getAsString().replace("frc", ""))));
        teams.add(getTeam(Long.parseLong(red.get(2).getAsString().replace("frc", ""))));
        teams.add(getTeam(Long.parseLong(blue.get(0).getAsString().replace("frc", ""))));
        teams.add(getTeam(Long.parseLong(blue.get(1).getAsString().replace("frc", ""))));
        teams.add(getTeam(Long.parseLong(blue.get(2).getAsString().replace("frc", ""))));
        return teams;
    }

    /**
     * Inserts team and Robot, and Robot Event
     *
     * @param team
     */
    public void insertTeam(Team team, Event event) {
        boolean team_added = teamDao.load(team.getNumber()) != null;
        if (team_added) {
            team = teamDao.load(team.getNumber());
        } else {
            daoSession.insert(team);
        }

        //Check robot
        QueryBuilder<Robot> robotQueryBuilder = robotDao.queryBuilder();
        robotQueryBuilder.where(RobotDao.Properties.GameId.eq(event.getGameId()));
        robotQueryBuilder.where(RobotDao.Properties.TeamId.eq(team.getNumber()));
        Robot robot = robotQueryBuilder.unique();
        boolean robot_added = robot != null;

        if (!robot_added) {
            robot = new Robot(null, team.getNumber(), event.getGameId(), null, null);
            daoSession.insert(robot);
        }

        QueryBuilder<RobotEvent> robotEventQueryBuilder = robotEventDao.queryBuilder();
        robotEventQueryBuilder.where(RobotEventDao.Properties.EventId.eq(event.getGameId()));
        robotEventQueryBuilder.where(RobotEventDao.Properties.RobotId.eq(robot.getId()));
        RobotEvent robotEvent = robotEventQueryBuilder.unique();
        boolean robot_event_exists = robotEvent != null;

        if (!robot_event_exists) {
            robotEvent = new RobotEvent(null, robot.getId(), event.getId(), null);
            daoSession.insert(robotEvent);
        }
    }

    public List<RobotComment> getRobotComments() {
        List<Robot> robots = robotDao.loadAll();
        List<RobotComment> robotComments = new ArrayList<>();

        for (Robot robot : robots) {
            robotComments.add(new RobotComment(robot.getId(), robot.getComments()));
        }

        return robotComments;
    }

    public Robot getRobot(long robotId) {
        return robotDao.load(robotId);
    }

    public Team getTeam(Robot robot) {
        return teamDao.load(robot.getTeamId());
    }
}
