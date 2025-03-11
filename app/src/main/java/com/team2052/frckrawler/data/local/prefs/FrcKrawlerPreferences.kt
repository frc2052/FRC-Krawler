package com.team2052.frckrawler.data.local.prefs

import kotlinx.coroutines.flow.Flow

interface FrcKrawlerPreferences {

  val lastEventId: Flow<Int?>
  suspend fun setLastEventId(id: Int?)

  val lastGameId: Flow<Int?>
  suspend fun setLastGameId(id: Int?)
}