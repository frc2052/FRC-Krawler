package com.team2052.frckrawler.bluetooth

import android.bluetooth.BluetoothSocket
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.team2052.frckrawler.bluetooth.operation.OperationResult
import com.team2052.frckrawler.bluetooth.operation.OperationResult.ResultCode
import okio.BufferedSink
import okio.BufferedSource
import okio.buffer
import okio.sink
import okio.source
import java.nio.charset.Charset

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

inline fun <reified T> BufferedSource.readJSON(): T? {
  val moshi = Moshi.Builder().build()
  val adapter = moshi.adapter(T::class.java)
  return adapter.fromJson(readString(Charset.defaultCharset()))
}

inline fun <reified T>BufferedSink.writeJSON(t: T): String {
  val moshi = Moshi.Builder().build()
  val adapter: JsonAdapter<T> = moshi.adapter(T::class.java)
  val json = adapter.toJson(t)
  writeString(json, Charset.defaultCharset())
  return json
}

fun BufferedSource.readResult(): OperationResult<Any> {
   return OperationResult(ResultCode.parse(readInt()))
}

fun BufferedSink.writeResult(resultCode: ResultCode): OperationResult<Any> {
  writeInt(resultCode.id).emit()
  return OperationResult(resultCode)
}