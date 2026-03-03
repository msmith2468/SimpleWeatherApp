package com.example.simpleweatherapp.data.location

import android.content.Context
import com.example.simpleweatherapp.domain.DeviceLocationProvider
import com.example.simpleweatherapp.domain.LatLon

class DeviceLocationProviderImpl(
    private val context: Context
) : DeviceLocationProvider {

    private val provider = LocationProvider(context)

    override suspend fun getLastKnownLatLon(): LatLon? {
        val loc = provider.getLastKnownLocation() ?: return null
        return LatLon(loc.latitude, loc.longitude)
    }
}