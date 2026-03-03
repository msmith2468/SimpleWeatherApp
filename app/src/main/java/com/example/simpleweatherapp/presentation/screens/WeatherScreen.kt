package com.example.simpleweatherapp.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.simpleweatherapp.R
import com.example.simpleweatherapp.data.location.RequestLocationAndLoadWeatherOnce
import com.example.simpleweatherapp.presentation.UiError
import com.example.simpleweatherapp.presentation.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    vm: WeatherViewModel
) {
    RequestLocationAndLoadWeatherOnce(vm)
    val state by vm.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.app_name)) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.cityInput,
                onValueChange = vm::onCityInputChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.hint_city_input)) },
                singleLine = true
            )
            if (state.cityInput.isBlank()) {
                Text(
                    text = stringResource(R.string.hint_blank_uses_location),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = vm::searchByCity,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.isLoading) stringResource(R.string.cta_loading) else stringResource(R.string.cta_search))
            }

            val error = state.error
            if (error != null) {

                val errorText = when (error) {
                    UiError.EnterCity -> stringResource(R.string.message_enter_city)
                    UiError.NoInternet -> stringResource(R.string.error_no_internet)
                    UiError.Timeout -> stringResource(R.string.error_timeout)
                    UiError.ApiKeyRejected -> stringResource(R.string.error_api_key_rejected)
                    UiError.RateLimited -> stringResource(R.string.error_rate_limited)
                    is UiError.Server -> stringResource(R.string.error_server_generic, error.code)
                    UiError.CityOrLocation -> stringResource(R.string.error_city_or_location)
                    UiError.Generic -> stringResource(R.string.error_generic)
                }

                Text(
                    text = errorText,
                    color = MaterialTheme.colorScheme.error
                )
            }

            val weather = state.weather

            if (weather != null) {
                WeatherCard(
                    location = weather.locationLabel,
                    description = weather.description,
                    iconUrl = weather.iconUrl,
                    tempF = weather.tempF,
                    feelsLikeF = weather.feelsLikeF,
                    highF = weather.highF,
                    lowF = weather.lowF,
                    humidityPct = weather.humidityPct,
                    windMph = weather.windMph
                )
            } else if (!state.isLoading && state.error == null) {
                Text(stringResource(R.string.label_idle))
            }
        }
    }
}

@Composable
private fun WeatherCard(
    location: String,
    description: String,
    iconUrl: String,
    tempF: Int,
    feelsLikeF: Int,
    highF: Int,
    lowF: Int,
    humidityPct: Int,
    windMph: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WeatherIcon(
                iconUrl = iconUrl,
                description = description
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(location, style = MaterialTheme.typography.titleMedium)
                Text(description, style = MaterialTheme.typography.bodyMedium)

                Text(
                    "${tempF}°F (feels ${feelsLikeF}°F)",
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    "H: ${highF}°  L: ${lowF}°  Humidity: ${humidityPct}%  Wind: ${windMph} mph",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun WeatherIcon(iconUrl: String, description: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(iconUrl)
            .crossfade(true)
            .build(),
        contentDescription = description,
        modifier = Modifier.size(64.dp)
    )
}