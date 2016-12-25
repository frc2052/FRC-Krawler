package com.team2052.frckrawler.database.tables;

import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.db.RobotDao;
import com.team2052.frckrawler.db.RobotEvent;
import com.team2052.frckrawler.db.RobotEventDao;
import com.team2052.frckrawler.db.Team;
import com.team2052.frckrawler.db.TeamDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.Date;

public class Teams extends Table<Team, TeamDao> {
    public Teams(TeamDao dao, DBManager dbManager) {
        super(dao, dbManager);
    }

    /**
     * Inserts team and Robot, and Robot Event
     *
     * @param team
     */
    public void insertNew(Team team, Event event) {
        boolean team_added = load(team.getNumber()) != null;
        if (team_added) {
            team = load(team.getNumber());
        } else {
            insert(team);
        }

        //Check robot
        QueryBuilder<Robot> robotQueryBuilder = dbManager.getRobotsTable().getQueryBuilder();
        robotQueryBuilder.where(RobotDao.Properties.Game_id.eq(event.getGame_id()));
        robotQueryBuilder.where(RobotDao.Properties.Team_id.eq(team.getNumber()));
        Robot robot = robotQueryBuilder.unique();
        boolean robot_added = robot != null;

        if (!robot_added) {
            robot = new Robot(null, team.getNumber(), event.getGame_id(), null, "", new Date());
            dbManager.getRobotsTable().insert(robot);
        }

        QueryBuilder<RobotEvent> robotEventQueryBuilder = dbManager.getRobotEventsTable().getQueryBuilder();
        robotEventQueryBuilder.where(RobotEventDao.Properties.Event_id.eq(event.getId()));
        robotEventQueryBuilder.where(RobotEventDao.Properties.Robot_id.eq(robot.getId()));
        RobotEvent robotEvent = robotEventQueryBuilder.unique();
        boolean robot_event_exists = robotEvent != null;

        if (!robot_event_exists) {
            robotEvent = new RobotEvent(null, robot.getId(), event.getId(), null);
            dbManager.getRobotEventsTable().insert(robotEvent);
        }
    }

    @Deprecated
    /**
     * Please do not use this unless you really need to
     */
    public void insert(Team team) {
        dao.insertOrReplace(team);
    }

    @Override
    public Team load(long id) {
        return dao.load(id);
    }

    @Override
    public void delete(Team model) {
        dao.delete(model);
    }
}
