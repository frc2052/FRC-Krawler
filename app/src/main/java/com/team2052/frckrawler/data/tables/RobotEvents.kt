package com.team2052.frckrawler.data.tables

import com.team2052.frckrawler.data.DBManager
import com.team2052.frckrawler.models.Robot
import com.team2052.frckrawler.models.RobotEvent
import com.team2052.frckrawler.models.RobotEventDao
import com.team2052.frckrawler.models.Team
import org.greenrobot.greendao.query.QueryBuilder

class RobotEvents(dao: RobotEventDao, dbManager: DBManager) : AbstractTable<RobotEvent, RobotEventDao>(dao, dbManager) {
    fun query(robot_id: Long?, event_id: Long?): QueryBuilder<RobotEvent> {
        val queryBuilder = queryBuilder
        if (robot_id != null) {
            queryBuilder.where(RobotEventDao.Properties.Robot_id.eq(robot_id))
        }
        if (event_id != null) {
            queryBuilder.where(RobotEventDao.Properties.Event_id.eq(event_id))
        }
        return queryBuilder
    }

    fun getTeam(robotEvent: RobotEvent): Team? {
        return dbManager.teamsTable.load(getRobot(robotEvent)?.team_id)
    }

    fun getRobot(robotEvent: RobotEvent): Robot? {
        return dbManager.robotsTable.load(robotEvent.robot_id)
    }
}