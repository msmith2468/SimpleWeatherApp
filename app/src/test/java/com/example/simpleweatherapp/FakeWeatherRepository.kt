package com.example.simpleweatherapp

import com.example.simpleweatherapp.domain.WeatherRepository
import com.example.simpleweatherapp.presentation.WeatherDisplay

class FakeWeatherRepository : WeatherRepository {

    var nextCityResult: Result<WeatherDisplay> = Result.failure(IllegalStateException("Not set"))
    var nextLatLonResult: Result<WeatherDisplay> = Result.failure(IllegalStateException("Not set"))

    var lastLatLonCall: Pair<Double, Double>? = null
    var lastCityCall: String? = null

    override suspend fun getWeatherByCityQuery(cityQuery: String): WeatherDisplay {
        lastCityCall = cityQuery
        return nextCityResult.getOrThrow()
    }

    override suspend fun getWeatherByLatLon(lat: Double, lon: Double): WeatherDisplay {
        lastLatLonCall = lat to lon
        return nextLatLonResult.getOrThrow()
    }
}