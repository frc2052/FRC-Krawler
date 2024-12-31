package com.team2052.frckrawler.ui.game.list

import com.team2052.frckrawler.data.local.Game

sealed interface GameListState {
  data object Loading : GameListState
  data class Content(val games: List<Game>) : GameListState
}