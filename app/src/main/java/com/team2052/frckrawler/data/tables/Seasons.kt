package com.team2052.frckrawler.data.tables

import com.team2052.frckrawler.data.DBManager
import com.team2052.frckrawler.models.*

class Seasons(dao: SeasonDao, dbManager: DBManager) : AbstractTable<Season, SeasonDao>(dao, dbManager) {
    override fun delete(model: Season) {
        model.resetEventList()
        model.resetRobotList()
        model.resetMetricList()
        dbManager.eventsTable.delete(model.eventList)
        dbManager.robotsTable.delete(model.robotList)
        dbManager.metricsTable.delete(model.metricList)
        super.delete(model)
    }

    fun getRobots(game: Season): List<Robot> {
        game.resetRobotList()
        return game.robotList
    }

    fun getEvents(game: Season): List<Event> {
        game.resetEventList()
        return game.eventList
    }

    fun getMetrics(game: Season): List<Metric> {
        game.resetMetricList()
        return game.metricList
    }
}