package com.team2052.frckrawler.bluetooth

import com.team2052.frckrawler.BuildConfig
import com.team2052.frckrawler.bluetooth.operation.ReceiveConnectHandshake
import javax.inject.Inject

class SyncOperationFactory @Inject constructor() {

  fun createServerOperations(): List<SyncOperation> {
    return listOf(
      ReceiveConnectHandshake(
        versionCode = BuildConfig.VERSION_CODE,
        versionName = BuildConfig.VERSION_NAME
      )
    )
  }
}