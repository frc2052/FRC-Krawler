package com.team2052.frckrawler.ui.scout.remote

import java.time.ZonedDateTime

sealed class ServerSyncState {
  data class NotSynced(
    val hasSyncFailure: Boolean
  ) : ServerSyncState()
  data object Syncing : ServerSyncState()
  data class Synced(
    val pendingDataCount: Int,
    val lastSyncTime: ZonedDateTime,
    val hasSyncFailure: Boolean = false,
  ) : ServerSyncState()
}