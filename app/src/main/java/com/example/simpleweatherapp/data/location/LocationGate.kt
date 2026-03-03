package com.example.simpleweatherapp.data.location

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.simpleweatherapp.presentation.WeatherViewModel
import kotlinx.coroutines.launch

@Composable
fun RequestLocationAndLoadWeatherOnce(vm: WeatherViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Track so we don't repeatedly request every recomposition
    var hasTried by rememberSaveable { mutableStateOf(false) }

    val permission = Manifest.permission.ACCESS_COARSE_LOCATION

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasTried = true
        if (granted) {
            scope.launch {
                val provider = LocationProvider(context)
                val loc = provider.getLastKnownLocation()
                if (loc != null) {
                    vm.loadByLatLon(loc.latitude, loc.longitude)
                } else {
                    // Defensive: no cached fix; your "last city" autoload will still handle fallback.
                    // Optional: vm.showMessage("Location unavailable. You can search by city.")
                }
            }
        } else {
            // Permission denied -> do nothing. Last city autoload already covers fallback.
        }
    }

    LaunchedEffect(Unit) {
        // Only do this once per app run / process
        if (hasTried) return@LaunchedEffect

        val alreadyGranted = ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED

        if (alreadyGranted) {
            hasTried = true
            // Permission already granted -> load immediately
            scope.launch {
                val provider = LocationProvider(context)
                val loc = provider.getLastKnownLocation()
                if (loc != null) {
                    vm.loadByLatLon(loc.latitude, loc.longitude)
                }
            }
        } else {
            // Request it
            launcher.launch(permission)
        }
    }
}