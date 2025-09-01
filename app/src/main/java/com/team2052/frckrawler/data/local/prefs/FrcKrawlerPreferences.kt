package com.team2052.frckrawler.data.local.prefs

import kotlinx.coroutines.flow.Flow

interface FrcKrawlerPreferences {
  val hasDismissedNotificationPrompt: Flow<Boolean>
  suspend fun setHasDismissedNotificationPrompt(dismissed: Boolean)

  val lastEventId: Flow<Int?>
  suspend fun setLastEventId(id: Int?)

  val lastGameId: Flow<Int?>
  suspend fun setLastGameId(id: Int?)

  val exportIncludeTeamNames: Flow<Boolean>
  suspend fun setExportIncludeTeamNames(includeNames: Boolean)

  val exportIncludeMatchMetrics: Flow<Boolean>
  suspend fun setExportIncludeMatchMetrics(includeMatchMetrics: Boolean)

  val exportIncludePitMetrics: Flow<Boolean>
  suspend fun setExportIncludePitMetrics(includePitMetrics: Boolean)
}