package com.team2052.frckrawler.domain

import com.team2052.frckrawler.data.local.Event
import com.team2052.frckrawler.data.local.EventDao
import com.team2052.frckrawler.data.local.Game
import com.team2052.frckrawler.data.local.GameDao
import com.team2052.frckrawler.data.local.MetricDao
import com.team2052.frckrawler.data.local.MetricRecord
import com.team2052.frckrawler.data.local.MetricSet
import com.team2052.frckrawler.data.local.TeamAtEvent
import com.team2052.frckrawler.data.local.TeamAtEventDao
import com.team2052.frckrawler.data.sync.MetricPacket
import com.team2052.frckrawler.data.sync.ServerConfigurationPacket
import javax.inject.Inject

/**
 * This use case saves a [ServerConfigurationPacket] locally to be used for scouting clients
 */
class SaveServerConfigurationForScoutingUseCase @Inject constructor(
    private val gameDao: GameDao,
    private val metricDao: MetricDao,
    private val eventDao: EventDao,
    private val teamAtEventDao: TeamAtEventDao,
) {

    suspend operator fun invoke(config: ServerConfigurationPacket) {
        gameDao.insert(
            Game(
                id = Game.SCOUT_GAME_ID,
                name = config.game.name
            )
        )

        metricDao.deleteAllFromSet(MetricSet.SCOUT_MATCH_METRIC_SET_ID)
        val matchMetrics = config.game.matchMetrics.toRecords(MetricSet.SCOUT_MATCH_METRIC_SET_ID)
        metricDao.insertAll(matchMetrics)

        metricDao.deleteAllFromSet(MetricSet.SCOUT_PIT_METRIC_SET_ID)
        val pitMetrics = config.game.matchMetrics.toRecords(MetricSet.SCOUT_PIT_METRIC_SET_ID)
        metricDao.insertAll(pitMetrics)

        eventDao.insert(
            Event(
                id = Event.SCOUT_EVENT_ID,
                gameId = Game.SCOUT_GAME_ID,
                name = config.event.name,
            )
        )

        teamAtEventDao.deleteAllFromEvent(Event.SCOUT_EVENT_ID)
        val teams = config.event.teams.map { team ->
            TeamAtEvent(
                number = team.number,
                name = team.name,
                eventId = Event.SCOUT_EVENT_ID
            )
        }
        teamAtEventDao.insertAll(teams)
    }

    private fun List<MetricPacket>.toRecords(
        metricSetId: Int
    ): List<MetricRecord> {
        return map { metric ->
            MetricRecord(
                uuid = metric.uuid,
                name = metric.name,
                type = metric.type,
                priority = metric.priority,
                enabled = true,
                metricSetId = metricSetId,
                options = metric.options
            )
        }
    }

}