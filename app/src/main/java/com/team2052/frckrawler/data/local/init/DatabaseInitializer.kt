package com.team2052.frckrawler.data.local.init

import com.team2052.frckrawler.data.local.GameDao
import dev.zacsweers.metro.Inject

@Inject
class DatabaseInitializer(
  private val gameDao: GameDao,
  private val seedDatabaseTask: Lazy<SeedDatabaseTask>,
) {
  suspend fun ensureInitialized() {
    if (gameDao.getGameCount() == 0) {
      seedDatabaseTask.value.seed()
    }
  }
}