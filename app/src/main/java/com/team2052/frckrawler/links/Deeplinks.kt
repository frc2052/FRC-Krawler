package com.team2052.frckrawler.links

import android.net.Uri
import androidx.core.net.toUri
import androidx.navigation3.runtime.NavKey
import com.team2052.frckrawler.links.Deeplinks.Params
import com.team2052.frckrawler.ui.navigation.Screen

object Deeplinks {
  val ServerHome = "frckrawler://server".toUri()

  object Params {
    const val GameId = "game_id"
    const val EventId = "event_id"
  }
}

fun Uri.toNavKey(): NavKey? {
  return when {
    matchesRoute(Deeplinks.ServerHome) -> {
      Screen.Server(
        gameId = this.getQueryParameter(Params.GameId)?.toIntOrNull() ?: return null,
        eventId = this.getQueryParameter(Params.EventId)?.toIntOrNull() ?: return null,
      )
    }
    else -> null
  }
}

private fun Uri.matchesRoute(other: Uri): Boolean {
  return this.scheme == other.scheme
    && this.host == other.host
    && this.pathSegments == other.pathSegments
}