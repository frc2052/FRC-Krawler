package com.team2052.frckrawler.bluetooth.operation

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.team2052.frckrawler.bluetooth.OperationResult
import com.team2052.frckrawler.bluetooth.SyncOperation
import com.team2052.frckrawler.bluetooth.readResult
import com.team2052.frckrawler.bluetooth.writeResult
import com.team2052.frckrawler.data.local.Event
import com.team2052.frckrawler.data.local.EventDao
import com.team2052.frckrawler.data.local.Game
import com.team2052.frckrawler.data.local.GameDao
import com.team2052.frckrawler.data.local.MetricDao
import com.team2052.frckrawler.data.local.MetricSet
import com.team2052.frckrawler.data.local.TeamAtEvent
import com.team2052.frckrawler.data.local.TeamAtEventDao
import com.team2052.frckrawler.data.sync.EventPacket
import com.team2052.frckrawler.data.sync.GamePacket
import com.team2052.frckrawler.data.sync.ServerConfigurationPacket
import com.team2052.frckrawler.data.sync.TeamPacket
import com.team2052.frckrawler.data.sync.toMetricPackets
import com.team2052.frckrawler.data.sync.toRecords
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okio.BufferedSink
import okio.BufferedSource
import javax.inject.Inject

class ReceiveServerConfiguration @Inject constructor(
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
      val config = getConfiguration()
      val configHash = config?.hashCode() ?: 0
      output.writeInt(configHash).emit()

      val hashResult = input.readResult()
      if (hashResult == OperationResult.ServerConfigurationMismatch) {
        val adapter = moshi.adapter<ServerConfigurationPacket>()
        val configuration = adapter.fromJson(input)

        if (configuration != null) {
          saveConfiguration(configuration)
        } else {
          return@runBlocking output.writeResult(OperationResult.FailedToSaveConfiguration)
        }

        output.writeResult(OperationResult.Success)
      } else {
        hashResult
      }
    }
  }

  private suspend fun getConfiguration(): ServerConfigurationPacket? {
    val deferredGame = scope.async { gameDao.get(Game.SCOUT_GAME_ID) }
    val deferredEvent =
      scope.async { eventDao.getAllForGame(Game.SCOUT_GAME_ID).first().firstOrNull() }

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
        matchMetrics = matchMetrics.toMetricPackets(),
        pitMetrics = pitMetrics.toMetricPackets(),
      ),
      event = EventPacket(
        name = event.name,
        teams = teams
      )
    )
  }

  private suspend fun saveConfiguration(config: ServerConfigurationPacket) {
    var game = Game(
      id = Game.SCOUT_GAME_ID,
      name = config.game.name,
    )
    gameDao.insert(game)

    metricDao.deleteAllFromSet(MetricSet.SCOUT_MATCH_METRIC_SET_ID)
    val matchMetrics = config.game.matchMetrics.toRecords(MetricSet.SCOUT_MATCH_METRIC_SET_ID)
    metricDao.insertAll(matchMetrics)

    metricDao.deleteAllFromSet(MetricSet.SCOUT_PIT_METRIC_SET_ID)
    val pitMetrics = config.game.pitMetrics.toRecords(MetricSet.SCOUT_PIT_METRIC_SET_ID)

    val sameMetricSetForBoth = matchMetrics.map { it.id } == pitMetrics.map { it.id }
    if (!sameMetricSetForBoth) {
      // We can't insert the same metrics twice, since there would be duplicate IDs.
      metricDao.insertAll(pitMetrics)
    }

    if (matchMetrics.isNotEmpty()) {
      game = game.copy(matchMetricsSetId = MetricSet.SCOUT_MATCH_METRIC_SET_ID)
    }

    if (pitMetrics.isNotEmpty()) {
      if (sameMetricSetForBoth) {
        game = game.copy(pitMetricsSetId = MetricSet.SCOUT_MATCH_METRIC_SET_ID)
      } else {
        game = game.copy(pitMetricsSetId = MetricSet.SCOUT_PIT_METRIC_SET_ID)
      }
    }
    gameDao.insert(game)

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
}