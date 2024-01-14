package com.team2052.frckrawler.bluetooth.operation

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.team2052.frckrawler.bluetooth.OperationResult
import com.team2052.frckrawler.bluetooth.SyncOperation
import com.team2052.frckrawler.bluetooth.readResult
import com.team2052.frckrawler.bluetooth.writeResult
import com.team2052.frckrawler.data.sync.ServerConfigurationPacket
import com.team2052.frckrawler.domain.GetScoutConfigurationForSyncUseCase
import com.team2052.frckrawler.domain.GetServerConfigurationForSyncUseCase
import com.team2052.frckrawler.domain.SaveServerConfigurationForScoutingUseCase
import kotlinx.coroutines.runBlocking
import okio.BufferedSink
import okio.BufferedSource

// TODO better injection
class ReceiveServerConfiguration(
    private val getConfiguration: GetScoutConfigurationForSyncUseCase,
    private val saveConfiguration: SaveServerConfigurationForScoutingUseCase,
    private val moshi: Moshi,
) : SyncOperation {

    @OptIn(ExperimentalStdlibApi::class)
    override fun execute(output: BufferedSink, input: BufferedSource): OperationResult {
        return runBlocking {
            val config = getConfiguration()
            val configHash = config?.hashCode() ?: 0
            output.writeInt(configHash)

            val hashResult = input.readResult()
            if (hashResult == OperationResult.ServerConfigurationMismatch) {
                val adapter = moshi.adapter<ServerConfigurationPacket>()
                val configuration = adapter.fromJson(input)

                if (configuration != null) {
                    saveConfiguration(configuration)
                } else {
                    return@runBlocking output.writeResult(OperationResult.FailedToSaveConfiguration)
                }

                output.writeResult(OperationResult.Success)
            } else {
                hashResult
            }
        }
    }
}