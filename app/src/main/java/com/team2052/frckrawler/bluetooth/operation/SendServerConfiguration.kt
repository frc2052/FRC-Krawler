package com.team2052.frckrawler.bluetooth.operation

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.team2052.frckrawler.bluetooth.OperationResult
import com.team2052.frckrawler.bluetooth.SyncOperation
import com.team2052.frckrawler.bluetooth.readResult
import com.team2052.frckrawler.bluetooth.writeResult
import com.team2052.frckrawler.data.local.EventDao
import com.team2052.frckrawler.data.local.GameDao
import com.team2052.frckrawler.data.local.MetricDao
import com.team2052.frckrawler.data.local.TeamAtEventDao
import com.team2052.frckrawler.data.sync.EventPacket
import com.team2052.frckrawler.data.sync.GamePacket
import com.team2052.frckrawler.data.sync.ServerConfigurationPacket
import com.team2052.frckrawler.data.sync.TeamPacket
import com.team2052.frckrawler.data.sync.toPackets
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okio.BufferedSink
import okio.BufferedSource


@AssistedFactory
interface SendServerConfigurationFactory {
    fun create(gameId: Int, eventId: Int): SendServerConfiguration
}

class SendServerConfiguration @AssistedInject constructor(
    @Assisted private val gameId: Int,
    @Assisted private val eventId: Int,

    private val gameDao: GameDao,
    private val metricDao: MetricDao,
    private val eventDao: EventDao,
    private val teamAtEventDao: TeamAtEventDao,
    private val moshi: Moshi,
) : SyncOperation {
    private val scope = CoroutineScope(Dispatchers.IO)

    @OptIn(ExperimentalStdlibApi::class)
    override fun execute(output: BufferedSink, input: BufferedSource): OperationResult {
        return runBlocking {
            val config = getConfiguration(gameId = gameId, eventId = eventId)

            val scoutConfigHash = input.readInt()
            if (scoutConfigHash == config.hashCode()) {
               output.writeResult(OperationResult.Success)
            } else {
                output.writeResult(OperationResult.ServerConfigurationMismatch)
                val adapter = moshi.adapter<ServerConfigurationPacket>()
                adapter.toJson(output, config)

                input.readResult()
            }
        }
    }
    private suspend fun getConfiguration(
        gameId: Int,
        eventId: Int,
    ): ServerConfigurationPacket {

        val deferredGame = scope.async { gameDao.get(gameId) }
        val deferredEvent = scope.async { eventDao.get(eventId) }

        val game = deferredGame.await()
        val deferredMatchMetrics = game.matchMetricsSetId?.let { setId ->
            scope.async { metricDao.getMetrics(setId).first() }
        }
        val deferredPitMetrics = game.pitMetricsSetId?.let { setId ->
            scope.async { metricDao.getMetrics(setId).first() }
        }

        val deferredTeams = scope.async {
            teamAtEventDao.getAllTeams(eventId).first()
        }

        val event = deferredEvent.await()
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
}