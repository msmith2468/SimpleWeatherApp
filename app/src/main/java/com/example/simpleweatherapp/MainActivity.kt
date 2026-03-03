package com.example.simpleweatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simpleweatherapp.data.local.LastCityStore
import com.example.simpleweatherapp.data.local.lastCityDataStore
import com.example.simpleweatherapp.data.location.DeviceLocationProviderImpl
import com.example.simpleweatherapp.data.repository.WeatherRepositoryImpl
import com.example.simpleweatherapp.presentation.SimpleVmFactory
import com.example.simpleweatherapp.presentation.WeatherViewModel
import com.example.simpleweatherapp.presentation.screens.WeatherScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repo = WeatherRepositoryImpl(apiKey = BuildConfig.OPEN_WEATHER_API_KEY)
        val lastCityStore = LastCityStore(applicationContext.lastCityDataStore)
        val deviceLocationProvider = DeviceLocationProviderImpl(applicationContext)

        setContent {
            val vm: WeatherViewModel =
                viewModel(factory = SimpleVmFactory {
                    WeatherViewModel(
                        repo,
                        lastCityStore,
                        deviceLocationProvider
                    )
                })

            MaterialTheme {
                Surface {
                    WeatherScreen(vm)
                }
            }
        }
    }
}
