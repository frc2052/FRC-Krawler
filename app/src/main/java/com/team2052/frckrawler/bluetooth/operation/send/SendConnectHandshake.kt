package com.team2052.frckrawler.bluetooth.operation.send

import com.team2052.frckrawler.bluetooth.operation.OperationResult
import com.team2052.frckrawler.bluetooth.operation.OperationResult.ResultCode
import com.team2052.frckrawler.bluetooth.operation.SyncOperation
import okio.BufferedSink
import okio.BufferedSource
import java.nio.charset.Charset

/**
 * Send connection handshake to a remote device so the remote device can check that the app
 * versions match
 */
class SendConnectHandshake(
  val versionCode: Int
): SyncOperation {
  override fun execute(output: BufferedSink, input: BufferedSource): OperationResult<Any> {
    output.writeInt(versionCode).emit()
    val result = ResultCode.parse(input.readInt())
    return OperationResult(
      result,
      // If the connection handshake fails return the received devices version name
      if (result == ResultCode.VersionMismatch) {
        input.readString(Charset.defaultCharset())
      } else {
        null
      }
    )
  }
}