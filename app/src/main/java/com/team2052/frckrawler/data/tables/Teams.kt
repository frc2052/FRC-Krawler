package com.team2052.frckrawler.data.tables

import com.team2052.frckrawler.data.DBManager
import com.team2052.frckrawler.models.*
import java.util.*

class Teams(dao: TeamDao, dbManager: DBManager) : AbstractTable<Team, TeamDao>(dao, dbManager) {
    fun insertNew(insert_team: Team, event: Event) {
        var team = insert_team
        val loaded_team = load(team.number)

        if (loaded_team != null) {
            team = loaded_team
        } else {
            insert(team)
        }

        //Check robot
        val robotQueryBuilder = dbManager.robotsTable.query(season_id = event.season_id, team_number = team.number)
        val robot_exists = robotQueryBuilder.count() > 0

        if (!robot_exists) {
            val robot = Robot(null, team.number, event.season_id, null, "", Date())
            dbManager.robotsTable.insert(robot)

            val robotEventQueryBuilder = dbManager.robotEventsTable.query(event_id = event.id, robot_id = robot.id)
            val robot_Event_exists = robotEventQueryBuilder.count() > 0

            if (!robot_Event_exists) {
                dbManager.robotEventsTable.insert(RobotEvent(null, robot.id, event.id, null))
            }
        }
    }
}

