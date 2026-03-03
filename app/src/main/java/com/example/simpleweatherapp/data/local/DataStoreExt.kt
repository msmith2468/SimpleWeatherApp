package com.example.simpleweatherapp.data.local

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.lastCityDataStore by preferencesDataStore(name = "last_city_prefs")