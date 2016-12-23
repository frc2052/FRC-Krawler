package com.team2052.frckrawler.database.tables;

import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.db.RobotEvent;
import com.team2052.frckrawler.db.RobotEventDao;
import com.team2052.frckrawler.db.Team;

public class RobotEvents extends Table<RobotEvent, RobotEventDao> {

    public RobotEvents(RobotEventDao dao, DBManager dbManager) {
        super(dao, dbManager);
    }

    public Team getTeam(RobotEvent robotEvent) {
        return dbManager.getTeamsTable().load(getRobot(robotEvent).getTeam_id());
    }

    public Robot getRobot(RobotEvent robotEvent) {
        return dbManager.getRobotsTable().load(robotEvent.getRobot_id());
    }

    public void insert(RobotEvent robotEvent) {
        dao.insert(robotEvent);
    }

    @Override
    public RobotEvent load(long id) {
        return dao.load(id);
    }


    @Override
    public void delete(RobotEvent model) {
        dao.delete(model);
    }
}
