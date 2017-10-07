package com.team2052.frckrawler.data.tables

import com.team2052.frckrawler.bluetooth.model.RobotComment
import com.team2052.frckrawler.data.DBManager
import com.team2052.frckrawler.models.Robot
import com.team2052.frckrawler.models.RobotDao
import com.team2052.frckrawler.models.Team
import org.greenrobot.greendao.query.QueryBuilder
import java.util.*


class Robots(dao: RobotDao, dbManager: DBManager) : AbstractTable<Robot, RobotDao>(dao, dbManager) {
    fun getRobotComments(): List<RobotComment> {
        val robots = loadAll()
        val robotComments = ArrayList<RobotComment>()

        for (robot in robots) {
            robotComments.add(getRobotComment(robot))
        }

        return robotComments
    }

    fun getTeam(robot: Robot): Team? {
        return dbManager.teamsTable.load(robot.team_id)
    }

    fun getRobotComment(robot: Robot): RobotComment {
        return RobotComment(robot.id!!, robot.comments)
    }

    fun query(id: Long? = null, team_number: Long? = null, season_id: Long? = null): QueryBuilder<Robot> {
        val robotQueryBuilder = queryBuilder
        if (team_number != null)
            robotQueryBuilder.where(RobotDao.Properties.Team_id.eq(team_number))
        if (season_id != null)
            robotQueryBuilder.where(RobotDao.Properties.Season_id.eq(season_id))
        if (id != null)
            robotQueryBuilder.where(RobotDao.Properties.Id.eq(id))
        return robotQueryBuilder
    }

    fun update(robot: Robot) {
        dao.update(robot)
    }
}
