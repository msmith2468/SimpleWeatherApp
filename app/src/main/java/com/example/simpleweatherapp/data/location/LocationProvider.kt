package com.example.simpleweatherapp.data.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationProvider(context: Context) {

    private val client = LocationServices.getFusedLocationProviderClient(context)

    /**
     * Returns last known location if available.
     * Note: lastLocation may be null if location is off, permission just granted,
     * or device has no cached fix yet.
     */
    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(): android.location.Location? =
        suspendCancellableCoroutine { cont ->
            client.lastLocation
                .addOnSuccessListener { loc -> cont.resume(loc) }
                .addOnFailureListener { cont.resume(null) }
        }
}