package com.team2052.frckrawler.data.local.prefs

import android.R.attr.data
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.team2052.frckrawler.di.ApplicationContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class FrcKrawlerDatastorePreferences(
  @ApplicationContext private val context: Context
) : FrcKrawlerPreferences {
  companion object {
    private val HAS_DISMISSED_NOTIFICATION_PROMPT = booleanPreferencesKey("dismissed_notification_prompt")
    private val LAST_EVENT_ID_KEY = intPreferencesKey("last_event_id")
    private val LAST_GAME_ID_KEY = intPreferencesKey("last_game_id")
    private val EXPORT_INCLUDE_TEAM_NAMES = booleanPreferencesKey("export_include_team_names")
    private val EXPORT_INCLUDE_MATCH_METRICS = booleanPreferencesKey("export_include_match_metrics")
    private val EXPORT_INCLUDE_PIT_METRICS = booleanPreferencesKey("export_include_pit_metrics")
    private val EXPORT_INCLUDE_DELETED = booleanPreferencesKey("export_include_deleted")
  }

  override val hasDismissedNotificationPrompt: Flow<Boolean> = context.dataStore.data
    .map { data -> data[HAS_DISMISSED_NOTIFICATION_PROMPT] ?: true }

  override suspend fun setHasDismissedNotificationPrompt(dismissed: Boolean) {
    context.dataStore.edit { data ->
      data[HAS_DISMISSED_NOTIFICATION_PROMPT] = dismissed
    }
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

  override val exportIncludeTeamNames: Flow<Boolean> = context.dataStore.data
    .map { data -> data[EXPORT_INCLUDE_TEAM_NAMES] ?: true }

  override suspend fun setExportIncludeTeamNames(includeNames: Boolean) {
    context.dataStore.edit { data ->
      data[EXPORT_INCLUDE_TEAM_NAMES] = includeNames
    }
  }

  override val exportIncludeMatchMetrics: Flow<Boolean> = context.dataStore.data
  .map { data -> data[EXPORT_INCLUDE_MATCH_METRICS] ?: true }

  override suspend fun setExportIncludeMatchMetrics(includeMatchMetrics: Boolean) {
    context.dataStore.edit { data ->
      data[EXPORT_INCLUDE_MATCH_METRICS] = includeMatchMetrics
    }
  }

  override val exportIncludePitMetrics: Flow<Boolean> = context.dataStore.data
    .map { data -> data[EXPORT_INCLUDE_PIT_METRICS] ?: true }

  override suspend fun setExportIncludePitMetrics(includePitMetrics: Boolean) {
    context.dataStore.edit { data ->
      data[EXPORT_INCLUDE_PIT_METRICS] = includePitMetrics
    }
  }

  override val exportIncludeDeleted: Flow<Boolean> = context.dataStore.data
  .map { data -> data[EXPORT_INCLUDE_DELETED] ?: false }


  override suspend fun setExportIncludeDeleted(includeDeleted: Boolean) {
    context.dataStore.edit { data ->
      data[EXPORT_INCLUDE_DELETED] = includeDeleted
    }
  }
}