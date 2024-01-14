package com.team2052.frckrawler.domain

import com.team2052.frckrawler.data.local.EventDao
import com.team2052.frckrawler.data.local.Game
import com.team2052.frckrawler.data.local.GameDao
import com.team2052.frckrawler.data.local.MetricDao
import com.team2052.frckrawler.data.local.MetricRecord
import com.team2052.frckrawler.data.local.TeamAtEventDao
import com.team2052.frckrawler.data.sync.EventPacket
import com.team2052.frckrawler.data.sync.GamePacket
import com.team2052.frckrawler.data.sync.MetricPacket
import com.team2052.frckrawler.data.sync.ServerConfigurationPacket
import com.team2052.frckrawler.data.sync.TeamPacket
import com.team2052.frckrawler.ui.navigation.Arguments.eventId
import com.team2052.frckrawler.ui.navigation.Arguments.gameId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * This use case retrieves a [ServerConfigurationPacket] from a scout's local database.
 */
class GetScoutConfigurationForSyncUseCase @Inject constructor(
    private val gameDao: GameDao,
    private val metricDao: MetricDao,
    private val eventDao: EventDao,
    private val teamAtEventDao: TeamAtEventDao,
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    suspend operator fun invoke(): ServerConfigurationPacket? {
        val deferredGame = scope.async { gameDao.get(Game.SCOUT_GAME_ID) }
        val deferredEvent = scope.async { eventDao.getAllForGame(Game.SCOUT_GAME_ID).first().firstOrNull() }

        val game = deferredGame.await()
        val deferredMatchMetrics = game.matchMetricsSetId?.let { setId ->
            scope.async { metricDao.getMetrics(setId).first() }
        }
        val deferredPitMetrics = game.pitMetricsSetId?.let { setId ->
            scope.async { metricDao.getMetrics(setId).first() }
        }

        val event = deferredEvent.await() ?: return null

        val deferredTeams = scope.async {
            teamAtEventDao.getAllTeams(event.id).first()
        }

        val matchMetrics = deferredMatchMetrics?.await() ?: emptyList()
        val pitMetrics = deferredPitMetrics?.await() ?: emptyList()
        val teams = deferredTeams.await().map { team ->
            TeamPacket(name = team.name, number = team.number)
        }

        return ServerConfigurationPacket(
            game = GamePacket(
                name = game.name,
                matchMetrics = matchMetrics.toPackets(),
                pitMetrics = pitMetrics.toPackets(),
            ),
            event = EventPacket(
                name = event.name,
                teams = teams
            )
        )
    }

    private fun List<MetricRecord>.toPackets(): List<MetricPacket> {
        return map { metric ->
            MetricPacket(
                name = metric.name,
                type = metric.type,
                priority = metric.priority,
                options = metric.options
            )
        }
    }

}