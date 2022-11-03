package com.team2052.frckrawler.bluetooth

import okio.BufferedSink
import okio.BufferedSource

/**
 * An individual operation in a client/server sync process
 */
interface SyncOperation {
  /**
   * Perform the operation.
   *
   * @param output A sink for writing data to the connected device
   * @param input A source for reading data from the connected device
   */
  fun execute(output: BufferedSink, input: BufferedSource): OperationResult
}