package com.team2052.frckrawler.bluetooth.operation.recieve

import com.team2052.frckrawler.bluetooth.operation.OperationResult
import com.team2052.frckrawler.bluetooth.operation.OperationResult.ResultCode
import com.team2052.frckrawler.bluetooth.operation.SyncOperation
import com.team2052.frckrawler.bluetooth.readJSON
import com.team2052.frckrawler.data.model.Event
import okio.BufferedSink
import okio.BufferedSource

class ReceiveEvent: SyncOperation {
    override fun execute(output: BufferedSink, input: BufferedSource): OperationResult<Event> {
        val event = input.readJSON<Event>()
        return OperationResult(ResultCode.Success, event)
    }
}