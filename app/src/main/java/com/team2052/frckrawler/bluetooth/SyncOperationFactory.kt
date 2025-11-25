package com.team2052.frckrawler.bluetooth

import com.team2052.frckrawler.BuildConfig
import com.team2052.frckrawler.bluetooth.operation.GameAndEvent
import com.team2052.frckrawler.bluetooth.operation.ReceiveConnectHandshake
import com.team2052.frckrawler.bluetooth.operation.ReceiveMetricDataArgs
import com.team2052.frckrawler.bluetooth.operation.ReceiveMetricDataFactory
import com.team2052.frckrawler.bluetooth.operation.ReceiveServerConfiguration
import com.team2052.frckrawler.bluetooth.operation.SendConnectHandshake
import com.team2052.frckrawler.bluetooth.operation.SendMetricData
import com.team2052.frckrawler.bluetooth.operation.SendServerConfigurationFactory
import dev.zacsweers.metro.Inject

@Inject
class SyncOperationFactory(
  private val sendServerConfigurationFactory: SendServerConfigurationFactory,
  private val receiveServerConfiguration: ReceiveServerConfiguration,
  private val sendMetricData: SendMetricData,
  private val receiveMetricDataFactory: ReceiveMetricDataFactory,
) {

  fun createServerOperations(gameId: Int, eventId: Int): List<SyncOperation> = listOf(
    ReceiveConnectHandshake(
      versionCode = BuildConfig.VERSION_CODE,
      versionName = BuildConfig.VERSION_NAME
    ),
    sendServerConfigurationFactory.create(
      GameAndEvent(gameId = gameId, eventId = eventId)
    ),
    receiveMetricDataFactory.create(
      ReceiveMetricDataArgs(eventId = eventId)
    ),
  )

  fun createScoutOperations(): List<SyncOperation> = listOf(
    SendConnectHandshake(
      versionCode = BuildConfig.VERSION_CODE
    ),
    receiveServerConfiguration,
    sendMetricData,
  )
}