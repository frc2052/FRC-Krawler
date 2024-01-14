package com.team2052.frckrawler.bluetooth

import com.squareup.moshi.Moshi
import com.team2052.frckrawler.BuildConfig
import com.team2052.frckrawler.bluetooth.operation.ReceiveConnectHandshake
import com.team2052.frckrawler.bluetooth.operation.ReceiveServerConfiguration
import com.team2052.frckrawler.bluetooth.operation.SendConnectHandshake
import com.team2052.frckrawler.bluetooth.operation.SendServerConfiguration
import com.team2052.frckrawler.domain.GetScoutConfigurationForSyncUseCase
import com.team2052.frckrawler.domain.GetServerConfigurationForSyncUseCase
import com.team2052.frckrawler.domain.SaveServerConfigurationForScoutingUseCase
import javax.inject.Inject

// TODO better injection for individual operations
class SyncOperationFactory @Inject constructor(
  private val getServerConfiguration: GetServerConfigurationForSyncUseCase,
  private val getScoutConfiguration: GetScoutConfigurationForSyncUseCase,
  private val saveConfiguration: SaveServerConfigurationForScoutingUseCase,
  private val moshi: Moshi
) {

  fun createServerOperations(gameId: Int, eventId: Int): List<SyncOperation> = listOf(
    ReceiveConnectHandshake(
      versionCode = BuildConfig.VERSION_CODE,
      versionName = BuildConfig.VERSION_NAME
    ),
    SendServerConfiguration(
      gameId = gameId,
      eventId = eventId,
      getConfiguration = getServerConfiguration,
      moshi = moshi,
    )
  )

  fun createScoutOperations(): List<SyncOperation> = listOf(
    SendConnectHandshake(
      versionCode = BuildConfig.VERSION_CODE
    ),
    ReceiveServerConfiguration(
      getConfiguration = getScoutConfiguration,
      saveConfiguration = saveConfiguration,
      moshi = moshi
    )
  )
}