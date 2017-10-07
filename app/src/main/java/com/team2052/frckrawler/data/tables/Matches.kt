package com.team2052.frckrawler.data.tables

import com.team2052.frckrawler.data.DBManager
import com.team2052.frckrawler.data.tba.v3.JSON
import com.team2052.frckrawler.models.Match
import com.team2052.frckrawler.models.MatchDao
import com.team2052.frckrawler.models.Team
import org.greenrobot.greendao.query.QueryBuilder
import java.util.*

class Matches(dao: MatchDao, dbManager: DBManager) : AbstractTable<Match, MatchDao>(dao, dbManager) {
    fun getTeams(match: Match): List<Team> {
        val alliances = JSON.getAsJsonObject(match.data).get("alliances").asJsonObject
        val teams = ArrayList<Team>()
        val red = alliances.get("red").asJsonObject.get("teams").asJsonArray
        val blue = alliances.get("blue").asJsonObject.get("teams").asJsonArray

        //TODO Do null check better
        teams.add(dbManager.teamsTable.load(java.lang.Long.parseLong(red.get(0).asString.replace("frc", "")))!!)
        teams.add(dbManager.teamsTable.load(java.lang.Long.parseLong(red.get(1).asString.replace("frc", "")))!!)
        teams.add(dbManager.teamsTable.load(java.lang.Long.parseLong(red.get(2).asString.replace("frc", "")))!!)

        teams.add(dbManager.teamsTable.load(java.lang.Long.parseLong(blue.get(0).asString.replace("frc", "")))!!)
        teams.add(dbManager.teamsTable.load(java.lang.Long.parseLong(blue.get(1).asString.replace("frc", "")))!!)
        teams.add(dbManager.teamsTable.load(java.lang.Long.parseLong(blue.get(2).asString.replace("frc", "")))!!)
        return teams
    }

    fun query(match_number: Int?, key: String?, event_id: Long?, type: String?): QueryBuilder<Match> {
        val queryBuilder = queryBuilder
        if (match_number != null)
            queryBuilder.where(MatchDao.Properties.Match_number.eq(match_number))
        if (key != null)
            queryBuilder.where(MatchDao.Properties.Match_key.eq(key))
        if (event_id != null)
            queryBuilder.where(MatchDao.Properties.Event_id.eq(event_id))
        if (type != null)
            queryBuilder.where(MatchDao.Properties.Event_id.eq(type))
        return queryBuilder
    }
}