package com.team2052.frckrawler.data.local.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class FrcKrawlerDatastorePreferences @Inject constructor(
  @ApplicationContext private val context: Context
) : FrcKrawlerPreferences {
  companion object {
    private val LAST_EVENT_ID_KEY = intPreferencesKey("last_event_id")
    private val LAST_GAME_ID_KEY = intPreferencesKey("last_game_id")
  }

  override val lastEventId: Flow<Int?> = context.dataStore.data
    .map { data ->
      data[LAST_EVENT_ID_KEY]
    }

  override suspend fun setLastEventId(id: Int?) {
    context.dataStore.edit { data ->
      if (id == null) {
        data.remove(LAST_EVENT_ID_KEY)
      } else {
        data[LAST_EVENT_ID_KEY] = id
      }
    }
  }

  override val lastGameId: Flow<Int?> = context.dataStore.data
    .map { data ->
      data[LAST_GAME_ID_KEY]
    }

  override suspend fun setLastGameId(id: Int?) {
    context.dataStore.edit { data ->
      if (id == null) {
        data.remove(LAST_GAME_ID_KEY)
      } else {
        data[LAST_GAME_ID_KEY] = id
      }
    }
  }
}