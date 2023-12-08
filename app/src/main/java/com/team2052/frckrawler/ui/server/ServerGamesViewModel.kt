package com.team2052.frckrawler.ui.server

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.data.local.Game
import com.team2052.frckrawler.data.local.GameDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServerGamesViewModel @Inject constructor(
    private val gameDao: GameDao
): ViewModel() {
    var games: List<Game> by mutableStateOf(emptyList())

    fun loadGames() {
        viewModelScope.launch {
            gameDao.getAll().collect() {
                games = it
            }
        }
    }

    fun makeGame(name: String) {
        viewModelScope.launch {
            gameDao.insert(Game(name = name))
        }
        loadGames()
    }

}