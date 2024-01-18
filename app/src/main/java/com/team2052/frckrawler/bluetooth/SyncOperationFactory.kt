package com.team2052.frckrawler.bluetooth

import com.team2052.frckrawler.BuildConfig
import com.team2052.frckrawler.bluetooth.operation.GameAndEvent
import com.team2052.frckrawler.bluetooth.operation.ReceiveConnectHandshake
import com.team2052.frckrawler.bluetooth.operation.ReceiveMetricData
import com.team2052.frckrawler.bluetooth.operation.ReceiveServerConfiguration
import com.team2052.frckrawler.bluetooth.operation.SendConnectHandshake
import com.team2052.frckrawler.bluetooth.operation.SendMetricData
import com.team2052.frckrawler.bluetooth.operation.SendServerConfigurationFactory
import javax.inject.Inject

class SyncOperationFactory @Inject constructor(
  private val sendServerConfigurationFactory: SendServerConfigurationFactory,
  private val receiveServerConfiguration: ReceiveServerConfiguration,
  private val sendMetricData: SendMetricData,
  private val receiveMetricData: ReceiveMetricData,
) {

  fun createServerOperations(gameId: Int, eventId: Int): List<SyncOperation> = listOf(
    ReceiveConnectHandshake(
      versionCode = BuildConfig.VERSION_CODE,
      versionName = BuildConfig.VERSION_NAME
    ),
    sendServerConfigurationFactory.create(
      GameAndEvent(gameId = gameId, eventId = eventId)
    ),
    receiveMetricData,
  )

  fun createScoutOperations(): List<SyncOperation> = listOf(
    SendConnectHandshake(
      versionCode = BuildConfig.VERSION_CODE
    ),
    receiveServerConfiguration,
    sendMetricData,
  )
}