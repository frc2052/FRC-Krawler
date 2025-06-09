package com.team2052.frckrawler.ui.navigation

import android.R.attr.data
import android.R.attr.type
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Represents a unique screen consisting of properties for the route, title, and sub-screens
 */
sealed interface Screen : NavKey {

  @Serializable
  data object ModeSelect : Screen

  @Serializable
  data object RemoteScoutHome : Screen

  @Serializable
  data class MatchScout(
    val eventId: Int,
    val metricSetId: Int,
  ) : Screen

  @Serializable
  data class PitScout(
    val eventId: Int,
    val metricSetId: Int,
  ) : Screen

  @Serializable
  data object GameList : Screen

  @Serializable
  data class Server(
    val gameId: Int,
    val eventId: Int,
  ) : Screen

  @Serializable
  data class Analyze(
    val gameId: Int,
    val eventId: Int,
  ) : Screen

  @Serializable
  data class TeamData(
    val teamNumber: String,
  ) : Screen

  @Serializable
  data class Export(
    val gameId: Int,
    val eventId: Int,
  ) : Screen

  @Serializable
  data class Game(val gameId: Int) : Screen

  @Serializable
  data class MetricSet(val metricSetId: Int) : Screen

  @Serializable
  data class Event(val eventId: Int) : Screen
}