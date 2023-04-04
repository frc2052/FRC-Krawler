package com.team2052.frckrawler.bluetooth.operation

import com.team2052.frckrawler.BuildConfig
import com.team2052.frckrawler.bluetooth.operation.recieve.ReceiveConnectHandshake
import com.team2052.frckrawler.bluetooth.operation.send.SendConnectHandshake
import javax.inject.Inject

class SyncOperationFactory @Inject constructor() {
  fun createServerOperations(): List<SyncOperation> = listOf(
    ReceiveConnectHandshake(
      versionCode = BuildConfig.VERSION_CODE,
      versionName = BuildConfig.VERSION_NAME
    )
  )

  fun createScoutOperations(): List<SyncOperation> = listOf(
    SendConnectHandshake(
      versionCode = BuildConfig.VERSION_CODE
    )
  )
}