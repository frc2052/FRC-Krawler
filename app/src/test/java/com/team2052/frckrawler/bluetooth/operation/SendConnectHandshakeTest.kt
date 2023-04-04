package com.team2052.frckrawler.bluetooth.operation

import com.team2052.frckrawler.bluetooth.operation.send.SendConnectHandshake
import com.team2052.frckrawler.bluetooth.writeResult
import okio.Buffer
import org.junit.Assert.*
import org.junit.Test

class SendConnectHandshakeTest {

  val output = Buffer()
  val input = Buffer()

  @Test
  fun `send handshake`() {
    val operation = SendConnectHandshake(
      versionCode = 2052
    )

    input.writeResult(OperationResult.ResultCode.Success)
    val result = operation.execute(output, input)

    assertEquals(result, OperationResult.ResultCode.Success)
  }
}