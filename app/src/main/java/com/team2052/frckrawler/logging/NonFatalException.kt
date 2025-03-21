package com.team2052.frckrawler.logging

import android.util.Log

class NonFatalException(
  message: String,
  cause: Throwable? = null,
  tag: String?,
  priority: Int,
) : Throwable(
  message = "${priority.toLevelString()} | ${tag ?: "untagged"} | $message",
  cause = cause,
)

private fun Int.toLevelString(): String = when (this) {
  Log.WARN -> "warning"
  Log.ERROR -> "error"
  else -> "other"
}