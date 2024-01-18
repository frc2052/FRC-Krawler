package com.team2052.frckrawler.bluetooth

import com.team2052.frckrawler.BuildConfig
import com.team2052.frckrawler.bluetooth.operation.ReceiveConnectHandshake
import com.team2052.frckrawler.bluetooth.operation.ReceiveServerConfiguration
import com.team2052.frckrawler.bluetooth.operation.SendConnectHandshake
import com.team2052.frckrawler.bluetooth.operation.SendServerConfigurationFactory
import javax.inject.Inject

class SyncOperationFactory @Inject constructor(
  private val sendServerConfigurationFactory: SendServerConfigurationFactory,
  private val receiveServerConfiguration: ReceiveServerConfiguration
) {

  fun createServerOperations(gameId: Int, eventId: Int): List<SyncOperation> = listOf(
    ReceiveConnectHandshake(
      versionCode = BuildConfig.VERSION_CODE,
      versionName = BuildConfig.VERSION_NAME
    ),
    sendServerConfigurationFactory.create(
      gameId = gameId,
      eventId = eventId
    )
  )

  fun createScoutOperations(): List<SyncOperation> = listOf(
    SendConnectHandshake(
      versionCode = BuildConfig.VERSION_CODE
    ),
    receiveServerConfiguration
  )
}