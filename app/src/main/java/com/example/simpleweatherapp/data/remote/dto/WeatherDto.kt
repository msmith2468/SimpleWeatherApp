package com.example.simpleweatherapp.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherDto(
    @Json(name = "name") val name: String?, // city name (sometimes null-ish in edge cases)
    @Json(name = "weather") val weather: List<WeatherConditionDto> = emptyList(),
    @Json(name = "main") val main: MainDto,
    @Json(name = "wind") val wind: WindDto? = null
)

@JsonClass(generateAdapter = true)
data class WeatherConditionDto(
    @Json(name = "main") val main: String?,            // e.g., "Rain"
    @Json(name = "description") val description: String?, // e.g., "light rain"
    @Json(name = "icon") val icon: String?             // e.g., "10d"
)

@JsonClass(generateAdapter = true)
data class MainDto(
    @Json(name = "temp") val temp: Double,
    @Json(name = "feels_like") val feelsLike: Double,
    @Json(name = "temp_min") val tempMin: Double,
    @Json(name = "temp_max") val tempMax: Double,
    @Json(name = "humidity") val humidity: Int
)

@JsonClass(generateAdapter = true)
data class WindDto(
    @Json(name = "speed") val speed: Double? = null // speed is in mph when units=imperial
)