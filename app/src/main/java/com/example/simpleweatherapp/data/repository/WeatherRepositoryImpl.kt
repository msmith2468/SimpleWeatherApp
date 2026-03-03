package com.example.simpleweatherapp.data.repository

import com.example.simpleweatherapp.data.remote.OpenWeatherApi
import com.example.simpleweatherapp.domain.WeatherRepository
import com.example.simpleweatherapp.presentation.WeatherDisplay
import com.example.simpleweatherapp.presentation.openWeatherIconUrl
import kotlin.math.roundToInt

class WeatherRepositoryImpl(
    private val apiKey: String
) : WeatherRepository {

    override suspend fun getWeatherByCityQuery(cityQuery: String): WeatherDisplay {
        val trimmed = cityQuery.trim()
        require(trimmed.isNotEmpty()) { "City cannot be empty" }

        val geos = OpenWeatherApi.service.geocode(
            query = trimmed,
            limit = 1,
            apiKey = apiKey
        )

        val geo = geos.firstOrNull()
            ?: throw IllegalArgumentException("No results found for \"$trimmed\"")

        return getWeatherByLatLon(geo.lat, geo.lon)
    }

    override suspend fun getWeatherByLatLon(lat: Double, lon: Double): WeatherDisplay {
        val dto = OpenWeatherApi.service.currentWeather(
            lat = lat,
            lon = lon,
            units = "imperial",
            apiKey = apiKey
        )

        val condition = dto.weather.firstOrNull()
        val description = condition?.description?.ifBlank { null } ?: "No description"
        val iconCode = condition?.icon ?: "01d" // fallback: clear sky
        val iconUrl = openWeatherIconUrl(iconCode)

        val windMph = dto.wind?.speed?.roundToInt() ?: 0

        val cityName = dto.name?.takeIf { it.isNotBlank() } ?: "Selected Location"

        return WeatherDisplay(
            locationLabel = cityName,
            description = description,
            iconUrl = iconUrl,
            tempF = dto.main.temp.roundToInt(),
            feelsLikeF = dto.main.feelsLike.roundToInt(),
            highF = dto.main.tempMax.roundToInt(),
            lowF = dto.main.tempMin.roundToInt(),
            humidityPct = dto.main.humidity,
            windMph = windMph
        )
    }
}