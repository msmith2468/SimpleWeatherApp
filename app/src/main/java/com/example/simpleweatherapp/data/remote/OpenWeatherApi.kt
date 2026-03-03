package com.example.simpleweatherapp.data.remote

object OpenWeatherApi {
    val service: OpenWeatherService =
        NetworkClient.retrofit.create(OpenWeatherService::class.java)
}