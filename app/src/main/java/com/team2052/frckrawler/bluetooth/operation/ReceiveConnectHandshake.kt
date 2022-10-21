package com.team2052.frckrawler.bluetooth.operation

import com.team2052.frckrawler.bluetooth.OperationResult
import com.team2052.frckrawler.bluetooth.SyncOperation
import com.team2052.frckrawler.bluetooth.writeResult
import okio.BufferedSink
import okio.BufferedSource
import java.nio.charset.Charset

/**
 * Receive a connection handshake from a remote device.
 * Checks that the app versions match
 */
class ReceiveConnectHandshake(
  val versionCode: Int,
  val versionName: String
): SyncOperation {
  override fun execute(output: BufferedSink, input: BufferedSource): OperationResult {
    val clientAppVersionCode = input.readInt()
    return if (clientAppVersionCode != versionCode) {
      output.writeResult(OperationResult.VersionMismatch).also {
        output.writeString(versionName, Charset.defaultCharset())
      }
    } else {
      output.writeResult(OperationResult.Success)
    }
  }
}