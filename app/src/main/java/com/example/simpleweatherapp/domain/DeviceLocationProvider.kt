package com.example.simpleweatherapp.domain

data class LatLon(val lat: Double, val lon: Double)

interface DeviceLocationProvider {
    suspend fun getLastKnownLatLon(): LatLon?
}