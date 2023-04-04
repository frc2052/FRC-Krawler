package com.team2052.frckrawler.bluetooth.operation.send

import com.team2052.frckrawler.bluetooth.operation.OperationResult
import com.team2052.frckrawler.bluetooth.operation.OperationResult.ResultCode
import com.team2052.frckrawler.bluetooth.operation.SyncOperation
import com.team2052.frckrawler.bluetooth.writeJSON
import com.team2052.frckrawler.data.model.Event
import okio.*

class SendEvent : SyncOperation {
    override fun execute(output: BufferedSink, input: BufferedSource): OperationResult<Unit> {
        output.writeJSON(Event.fakeEvent)
        return OperationResult(ResultCode.Success)
    }
}