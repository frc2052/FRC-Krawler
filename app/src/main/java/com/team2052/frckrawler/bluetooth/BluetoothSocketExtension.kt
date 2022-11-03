package com.team2052.frckrawler.bluetooth

import android.bluetooth.BluetoothSocket
import okio.BufferedSink
import okio.BufferedSource
import okio.buffer
import okio.sink
import okio.source
import timber.log.Timber

/**
 * Open buffered input and output streams on a BluetoothSocket
 */
inline fun BluetoothSocket.bufferedIO(
  block: (output: BufferedSink, input: BufferedSource) -> Unit
) {
  outputStream.sink().buffer().use { output ->
    inputStream.source().buffer().use { input ->
      block(output, input)
    }
  }
}

fun BufferedSource.readResult(): OperationResult {
   return OperationResult.parse(readInt())
}

fun BufferedSink.writeResult(result: OperationResult): OperationResult {
  writeInt(result.id).emit()
  return result
}