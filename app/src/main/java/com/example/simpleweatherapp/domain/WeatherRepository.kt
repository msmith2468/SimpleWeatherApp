package com.example.simpleweatherapp.domain

import com.example.simpleweatherapp.presentation.WeatherDisplay

interface WeatherRepository {
    suspend fun getWeatherByCityQuery(cityQuery: String): WeatherDisplay
    suspend fun getWeatherByLatLon(lat: Double, lon: Double): WeatherDisplay
}