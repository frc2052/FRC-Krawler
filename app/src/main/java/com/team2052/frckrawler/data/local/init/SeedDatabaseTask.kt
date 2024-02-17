package com.team2052.frckrawler.data.local.init

import com.team2052.frckrawler.data.local.Event
import com.team2052.frckrawler.data.local.EventDao
import com.team2052.frckrawler.data.local.Game
import com.team2052.frckrawler.data.local.GameDao
import com.team2052.frckrawler.data.local.MetricSet
import com.team2052.frckrawler.data.local.MetricSetDao
import javax.inject.Inject

data class SeedDatabaseTask @Inject constructor(
  private val gameDao: GameDao,
  private val metricSetDao: MetricSetDao,
  private val eventDao: EventDao,
) {
  suspend fun seed() {
    var game = Game(
      id = 1,
      name = "Remote Scout"
    )
    gameDao.insert(game)

    val matchMetrics = MetricSet(
      id = 1,
      name = "Remote Scout Match Metrics",
      gameId = 1
    )
    val pitMetrics = MetricSet(
      id = 2,
      name = "Remote Scout Pit Metrics",
      gameId = 1
    )
    metricSetDao.insert(matchMetrics)
    metricSetDao.insert(pitMetrics)

    game = game.copy(
      matchMetricsSetId = 1,
      pitMetricsSetId = 2
    )

    gameDao.insert(game)

    val event = Event(
      id = 1,
      name = "Remote Scout Event",
      gameId = 1
    )
    eventDao.insert(event)
  }

}