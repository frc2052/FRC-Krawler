package com.team2052.frckrawler.ui.scout.remote

sealed class ServerSyncState  {
    data object NotSynced : ServerSyncState()
    data object Syncing : ServerSyncState()
    data object Synced : ServerSyncState()
    data class PendingSyncData(
        private val pendingDataCount: Int,
    )
}