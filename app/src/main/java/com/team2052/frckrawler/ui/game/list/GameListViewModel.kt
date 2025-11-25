package com.team2052.frckrawler.ui.game.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.data.local.Game
import com.team2052.frckrawler.data.local.GameDao
import com.team2052.frckrawler.di.viewmodel.ViewModelKey
import com.team2052.frckrawler.di.viewmodel.ViewModelScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@ContributesIntoMap(ViewModelScope::class)
@ViewModelKey(GameListViewModel::class)
@Inject
class GameListViewModel(
  private val gameDao: GameDao
) : ViewModel() {
  var state: StateFlow<GameListState> = gameDao.getAll().map { games ->
    GameListState.Content(games = games)
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.Lazily,
    initialValue = GameListState.Loading
  )

  fun createGame(name: String) {
    viewModelScope.launch {
      gameDao.insert(Game(name = name))
    }
  }
}