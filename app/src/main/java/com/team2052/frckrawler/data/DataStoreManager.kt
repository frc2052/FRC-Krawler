package com.team2052.frckrawler.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class DataStoreManager (val context: Context) {
    private val Context.preferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "FRCKrawler")

    private val darkTheme = booleanPreferencesKey("dark_theme")
    private val startingRoute = stringPreferencesKey("starting_route")

    fun isDarkTheme(): Flow<Boolean?> {
        return context.preferencesDataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { it[darkTheme] }
    }

    suspend fun setDarkTheme(darkTheme: Boolean) {
        context.preferencesDataStore.edit { preferences ->
            preferences[this.darkTheme] = darkTheme
        }
    }

    fun getStartingScreen(): Flow<String?> {
        return context.preferencesDataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { it[startingRoute] }
    }

    suspend fun setStartingScreen(route: String) {
        context.preferencesDataStore.edit { preferences ->
            preferences[startingRoute] = route
        }
    }
}