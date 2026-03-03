package com.example.simpleweatherapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LastCityStore(
    private val dataStore: DataStore<Preferences>
) {
    private val KEY_LAST_CITY = stringPreferencesKey("last_city_query")

    val lastCityQuery: Flow<String?> = dataStore.data.map { prefs ->
        prefs[KEY_LAST_CITY]
    }

    suspend fun saveLastCityQuery(query: String) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return

        dataStore.edit { prefs ->
            prefs[KEY_LAST_CITY] = trimmed
        }
    }
}