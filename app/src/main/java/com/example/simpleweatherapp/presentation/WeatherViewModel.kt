package com.example.simpleweatherapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simpleweatherapp.data.local.LastCityStore
import com.example.simpleweatherapp.domain.DeviceLocationProvider
import com.example.simpleweatherapp.domain.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class WeatherViewModel(
    private val repo: WeatherRepository,
    private val lastCityStore: LastCityStore,
    private val deviceLocationProvider: DeviceLocationProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val last = lastCityStore.lastCityQuery.first()
            if (!last.isNullOrBlank() && _uiState.value.cityInput.isBlank()) {
                _uiState.update { it.copy(cityInput = last) }
                searchByCity()
            }
        }
    }

    fun onCityInputChanged(value: String) {
        _uiState.update { it.copy(cityInput = value, error = null) }
    }

    fun searchByCity() {
        val query = uiState.value.cityInput.trim()

        if (query.isBlank()) {
            // coroutine to load weather by device location
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, error = null) }
                try {
                    val latLon = deviceLocationProvider.getLastKnownLatLon()
                    if (latLon != null) {
                        val weather = repo.getWeatherByLatLon(latLon.lat, latLon.lon)
                        _uiState.update { it.copy(isLoading = false, weather = weather) }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = UiError.CityOrLocation
                            )
                        }
                    }
                } catch (t: Throwable) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = toUiError(t)
                        )
                    }
                }
            }
            return
        }
        // coroutine to load weather
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val weather = repo.getWeatherByCityQuery(query)
                lastCityStore.saveLastCityQuery(query)
                _uiState.update { it.copy(isLoading = false, weather = weather) }
            } catch (t: Throwable) {
                _uiState.update { it.copy(isLoading = false, error = toUiError(t)) }
            }
        }
    }

    fun loadByLatLon(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val weather = repo.getWeatherByLatLon(lat, lon)
                _uiState.update { it.copy(isLoading = false, weather = weather) }
            } catch (t: Throwable) {
                _uiState.update { it.copy(isLoading = false, error = toUiError(t)) }
            }
        }
    }

    private fun toUiError(t: Throwable): UiError {
        return when (t) {
            is IllegalArgumentException -> UiError.EnterCity
            is UnknownHostException -> UiError.NoInternet
            is SocketTimeoutException -> UiError.Timeout
            is HttpException -> when (t.code()) {
                401 -> UiError.ApiKeyRejected
                429 -> UiError.RateLimited
                else -> UiError.Server(t.code())
            }

            else -> UiError.Generic
        }
    }
}