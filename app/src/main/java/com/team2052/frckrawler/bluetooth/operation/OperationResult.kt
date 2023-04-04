package com.team2052.frckrawler.bluetooth.operation

data class OperationResult<T>(val resultCode: ResultCode, val resultData: T? = null) {
  enum class ResultCode(val id: Int) {
    Success(1),

    VersionMismatch(-1),
    EventMismatch(-2),

    Unknown(Int.MIN_VALUE);

    companion object {
      fun parse(value: Int): ResultCode {
        return values().firstOrNull { it.id == value } ?: Unknown
      }
    }
  }
}