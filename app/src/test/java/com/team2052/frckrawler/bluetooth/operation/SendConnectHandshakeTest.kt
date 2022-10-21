package com.team2052.frckrawler.bluetooth.operation

import com.team2052.frckrawler.bluetooth.OperationResult
import com.team2052.frckrawler.bluetooth.readResult
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

    input.writeResult(OperationResult.Success)
    val result = operation.execute(output, input)

    assertEquals(result, OperationResult.Success)
  }
}