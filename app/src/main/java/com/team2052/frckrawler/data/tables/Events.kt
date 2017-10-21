@file:JvmName("EventHelper")

package com.team2052.frckrawler.data.tables

import com.google.common.base.Strings
import com.google.common.collect.Lists
import com.team2052.frckrawler.data.DBManager
import com.team2052.frckrawler.models.*
import com.team2052.frckrawler.toJsonObject
import org.greenrobot.greendao.query.QueryBuilder
import java.util.*


class Events(dao: EventDao, dbManager: DBManager) : AbstractTable<Event, EventDao>(dao, dbManager) {
    fun query(fms_id: String? = null, game_id: Long? = null): QueryBuilder<Event> {
        val queryBuilder = queryBuilder
        if (fms_id != null)
            queryBuilder.where(EventDao.Properties.Fmsid.eq(fms_id))
        if (game_id != null)
            queryBuilder.where(EventDao.Properties.Season_id.eq(game_id))
        return queryBuilder
    }

    override fun delete(model: Event) {
        dbManager.matchDataTable.delete(getMatchData(model))
        dbManager.pitDataTable.delete(getPitData(model))
        dbManager.matchCommentsTable.delete(getMatchComments(model))
        dbManager.robotEventsTable.delete(getRobotEvents(model))
        super.delete(model)
    }

    fun getAllEvents(): List<Event> {
        return dao.loadAll()
    }

    fun getMatchData(event: Event): List<MatchDatum> {
        event.resetMatchDatumList()
        return event.matchDatumList
    }

    fun getPitData(event: Event): List<PitDatum> {
        event.resetPitDatumList()
        return event.pitDatumList
    }

    fun getMatchComments(event: Event): List<MatchComment> {
        event.resetMatchCommentList()
        return event.matchCommentList
    }

    fun getRobotEvents(event: Event): List<RobotEvent> {
        event.resetRobotEventList()
        return event.robotEventList
    }

    fun getRobots(event: Event): List<Robot?> = getRobotEvents(event).map { dbManager.robotEventsTable.getRobot(it) }

    fun getTeamsAtEvent(event: Event): List<Team> {
        val robotEventList = event.robotEventList
        val teams = Lists.newArrayList<Team>()
        robotEventList.mapTo(teams) { dbManager.robotEventsTable.getTeam(it) }
        Collections.sort(teams, { lhs, rhs -> java.lang.Double.compare(lhs.number.toDouble(), rhs.number.toDouble()) })
        return teams
    }
}

fun Event.getEventLocation(): String {
    if (Strings.isNullOrEmpty(data))
        return "Unknown Location"
    val data = data.toJsonObject()
    if (data != null) {
        if (data.has("address")) {
            return data.get("address").asString
        }
    }
    return "Unknown Location"
}