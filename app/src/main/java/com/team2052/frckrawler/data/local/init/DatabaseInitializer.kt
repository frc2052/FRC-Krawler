package com.team2052.frckrawler.data.local.init

import com.team2052.frckrawler.data.local.GameDao
import dagger.Lazy
import javax.inject.Inject

class DatabaseInitializer @Inject constructor(
  private val gameDao: GameDao,
  private val seedDatabaseTask: Lazy<SeedDatabaseTask>,
) {
  suspend fun ensureInitialized() {
    if (gameDao.getGameCount() == 0) {
      seedDatabaseTask.get().seed()
    }
  }
}