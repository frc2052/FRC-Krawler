package com.team2052.frckrawler.bluetooth

import com.team2052.frckrawler.BuildConfig
import com.team2052.frckrawler.bluetooth.operation.ReceiveConnectHandshake
import com.team2052.frckrawler.bluetooth.operation.SendConnectHandshake
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