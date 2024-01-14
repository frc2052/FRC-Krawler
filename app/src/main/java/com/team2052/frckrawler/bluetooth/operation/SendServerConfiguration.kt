package com.team2052.frckrawler.bluetooth.operation

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.team2052.frckrawler.bluetooth.OperationResult
import com.team2052.frckrawler.bluetooth.SyncOperation
import com.team2052.frckrawler.bluetooth.readResult
import com.team2052.frckrawler.bluetooth.writeResult
import com.team2052.frckrawler.data.sync.ServerConfigurationPacket
import com.team2052.frckrawler.domain.GetServerConfigurationForSyncUseCase
import kotlinx.coroutines.runBlocking
import okio.BufferedSink
import okio.BufferedSource

// TODO better injection
class SendServerConfiguration(
    private val gameId: Int,
    private val eventId: Int,
    private val getConfiguration: GetServerConfigurationForSyncUseCase,
    private val moshi: Moshi,
) : SyncOperation {

    @OptIn(ExperimentalStdlibApi::class)
    override fun execute(output: BufferedSink, input: BufferedSource): OperationResult {
        return runBlocking {
            val config = getConfiguration(gameId = gameId, eventId = eventId)

            val scoutConfigHash = input.readInt()
            if (scoutConfigHash == config.hashCode()) {
               output.writeResult(OperationResult.Success)
            } else {
                output.writeResult(OperationResult.ServerConfigurationMismatch)
                val adapter = moshi.adapter<ServerConfigurationPacket>()
                adapter.toJson(output, config)

                input.readResult()
            }
        }
    }
}