package com.example.simpleweatherapp

import com.example.simpleweatherapp.domain.DeviceLocationProvider
import com.example.simpleweatherapp.domain.LatLon

class FakeDeviceLocationProvider(
    var next: LatLon? = null
) : DeviceLocationProvider {
    override suspend fun getLastKnownLatLon(): LatLon? = next
}