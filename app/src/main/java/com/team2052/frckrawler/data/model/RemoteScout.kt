package com.team2052.frckrawler.data.model

import com.team2052.frckrawler.bluetooth.OperationResult
import java.time.LocalDateTime

data class RemoteScout(
  val name: String,
  val address: String,
  val lastSync: LocalDateTime = LocalDateTime.now(),
  val lastSyncResult: OperationResult
)