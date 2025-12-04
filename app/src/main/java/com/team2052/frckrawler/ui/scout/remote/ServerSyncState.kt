package com.team2052.frckrawler.ui.scout.remote

import com.team2052.frckrawler.bluetooth.OperationResult
import java.time.ZonedDateTime

sealed class ServerSyncState {
  data class NotSynced(
    val hasSyncFailure: Boolean,
    val failureReason: OperationResult? = null,
  ) : ServerSyncState()
  data object Syncing : ServerSyncState()
  data class Synced(
    val pendingDataCount: Int,
    val lastSyncTime: ZonedDateTime,
    val hasSyncFailure: Boolean = false,
  ) : ServerSyncState()
}