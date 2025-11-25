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
import kotlinx.coroutines.launch

@ContributesIntoMap(ViewModelScope::class)
@ViewModelKey(GameListViewModel::class)
@Inject
class GameListViewModel(
  private val gameDao: GameDao
) : ViewModel() {
  var state: GameListState by mutableStateOf(GameListState.Loading)
    private set

  fun loadGames() {
    viewModelScope.launch {
      gameDao.getAll().collect { games ->
        state = GameListState.Content(games = games)
      }
    }
  }

  fun createGame(name: String) {
    viewModelScope.launch {
      gameDao.insert(Game(name = name))
    }
  }
}