package com.example.simpleweatherapp.presentation

fun openWeatherIconUrl(iconCode: String, size2x: Boolean = true): String {
    val suffix = if (size2x) "@2x" else ""
    return "https://openweathermap.org/img/wn/$iconCode$suffix.png"
}