package com.example.simpleweatherapp.data.remote

import com.example.simpleweatherapp.data.remote.dto.GeocodeDto
import com.example.simpleweatherapp.data.remote.dto.WeatherDto
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherService {

    @GET("geo/1.0/direct")
    suspend fun geocode(
        @Query("q") query: String,
        @Query("limit") limit: Int = 1,
        @Query("appid") apiKey: String
    ): List<GeocodeDto>

    @GET("data/2.5/weather")
    suspend fun currentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "imperial",
        @Query("appid") apiKey: String
    ): WeatherDto
}
