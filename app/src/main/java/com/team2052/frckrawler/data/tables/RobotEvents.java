package com.team2052.frckrawler.data.tables;

import com.team2052.frckrawler.data.DBManager;
import com.team2052.frckrawler.models.Robot;
import com.team2052.frckrawler.models.RobotEvent;
import com.team2052.frckrawler.models.RobotEventDao;
import com.team2052.frckrawler.models.Team;

import org.greenrobot.greendao.query.QueryBuilder;

public class RobotEvents extends AbstractTable<RobotEvent, RobotEventDao> {

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

    public QueryBuilder<RobotEvent> query(Long robot_id, Long event_id) {
        QueryBuilder<RobotEvent> queryBuilder = getQueryBuilder();
        if (robot_id != null) {
            queryBuilder.where(RobotEventDao.Properties.Robot_id.eq(robot_id));
        }
        if (event_id != null) {
            queryBuilder.where(RobotEventDao.Properties.Event_id.eq(event_id));
        }
        return queryBuilder;
    }
}
