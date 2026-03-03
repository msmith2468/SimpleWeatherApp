package com.example.simpleweatherapp.presentation

data class WeatherUiState(
    val cityInput: String = "",
    val isLoading: Boolean = false,
    val weather: WeatherDisplay? = null,
    val error: UiError? = null
)

data class WeatherDisplay(
    val locationLabel: String,
    val description: String,
    val iconUrl: String,
    val tempF: Int,
    val feelsLikeF: Int,
    val highF: Int,
    val lowF: Int,
    val humidityPct: Int,
    val windMph: Int
)

sealed class UiError {
    data object EnterCity : UiError()
    data object NoInternet : UiError()
    data object Timeout : UiError()
    data object ApiKeyRejected : UiError()
    data object RateLimited : UiError()
    data class Server(val code: Int) : UiError()
    data object CityOrLocation : UiError()
    data object Generic : UiError()
}