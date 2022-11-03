package com.team2052.frckrawler.bluetooth

enum class OperationResult(val id: Int) {
  Success(1),

  VersionMismatch(-1),
  EventMismatch(-2),

  Unknown(Int.MIN_VALUE);

  companion object {
    fun parse(value: Int): OperationResult {
      return OperationResult.values().firstOrNull { it.id == value } ?: Unknown
    }
  }
}