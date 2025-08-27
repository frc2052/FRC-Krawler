package com.team2052.frckrawler.bluetooth

enum class OperationResult(val id: Int) {
  Success(1),

  /**
   * Application versions do not match
   */
  VersionMismatch(-1),

  /**
   * Server configurations do not match
   */
  ServerConfigurationMismatch(-2),

  /**
   * Couldn't save server configuration on scout device
   */
  FailedToSaveConfiguration(-3),

  Unknown(Int.MIN_VALUE);

  companion object {
    fun parse(value: Int): OperationResult {
      return entries.firstOrNull { it.id == value } ?: Unknown
    }
  }
}