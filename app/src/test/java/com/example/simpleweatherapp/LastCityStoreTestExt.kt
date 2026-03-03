package com.example.simpleweatherapp

import com.example.simpleweatherapp.data.local.LastCityStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

fun LastCityStore.lastCityQueryValueForTest(): String? = runBlocking {
    lastCityQuery.first()
}