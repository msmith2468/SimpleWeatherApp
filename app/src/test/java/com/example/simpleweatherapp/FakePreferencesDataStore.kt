package com.example.simpleweatherapp

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakePreferencesDataStore : DataStore<Preferences> {
    private val state = MutableStateFlow<Preferences>(emptyPreferences())

    override val data: Flow<Preferences> = state.asStateFlow()

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
        val newValue = transform(state.value)
        state.value = newValue
        return newValue
    }
}