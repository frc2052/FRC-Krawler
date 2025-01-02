package com.team2052.frckrawler.bluetooth.operation

import com.team2052.frckrawler.bluetooth.OperationResult
import com.team2052.frckrawler.bluetooth.SyncOperation
import okio.BufferedSink
import okio.BufferedSource

/**
 * Send connection handshake to a remote device so the remote device can check that the app
 * versions match
 */
class SendConnectHandshake(
  val versionCode: Int
) : SyncOperation {
  override fun execute(output: BufferedSink, input: BufferedSource): OperationResult {
    output.writeInt(versionCode).emit()
    return OperationResult.parse(input.readInt())
  }
}