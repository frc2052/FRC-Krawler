package com.team2052.frckrawler.bluetooth.operation

import com.team2052.frckrawler.bluetooth.operation.recieve.ReceiveConnectHandshake
import com.team2052.frckrawler.bluetooth.readResult
import okio.Buffer
import org.junit.Assert.*
import org.junit.Test
import java.nio.charset.Charset

class ReceiveConnectHandshakeTest {

  val output = Buffer()
  val input = Buffer()

  @Test
  fun `versions match`() {
    val operation = ReceiveConnectHandshake(
      versionCode = 2052,
      versionName = "test"
    )

    input.writeInt(2052)
    val result = operation.execute(output, input)

    val sentResult = output.readResult()
    assertEquals(sentResult, OperationResult.ResultCode.Success)
    assertEquals(result, OperationResult.ResultCode.Success)
  }

  @Test
  fun `versions do not match`() {
    val operation = ReceiveConnectHandshake(
      versionCode = 2052,
      versionName = "test"
    )

    input.writeInt(10)
    val result = operation.execute(output, input)
    val sentResult = output.readResult()

    assertEquals(sentResult, OperationResult.ResultCode.VersionMismatch)
    assertEquals(result, OperationResult.ResultCode.VersionMismatch)

    val requiredVersion = output.readString(Charset.defaultCharset())
    assertEquals(requiredVersion, "test")
  }
}