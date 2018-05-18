package com.team2052.frckrawler.core.data.tables

import com.team2052.frckrawler.core.data.models.DBManager
import com.team2052.frckrawler.core.data.models.Team
import com.team2052.frckrawler.core.data.models.TeamDao

class Teams(dao: TeamDao, dbManager: DBManager) : AbstractTable<Team, TeamDao>(dao, dbManager) {
    fun insertNew(insert_team: Team) {
        var team = insert_team
        val loaded_team = load(team.number)

        if (loaded_team != null) {
            team = loaded_team
        } else {
            insert(team)
        }
    }
}

