package com.team2052.frckrawler.ui.game.detail

import com.team2052.frckrawler.data.local.Game

sealed class GameDetailState {
    data object Loading: GameDetailState()
    data class Content(
        val game: Game,
        val events: List<GameDetailEvent>,
        val metrics: List<GameDetailMetricSet>,
    ): GameDetailState()
}

class GameDetailEvent(
    val id: Int,
    val name: String,
    val teamCount: Int
)

class GameDetailMetricSet(
    val id: Int,
    val name: String,
    val metricCount: Int,
    val isMatchMetrics: Boolean,
    val isPitMetrics: Boolean,
)