package com.example.simpleweatherapp

import com.example.simpleweatherapp.data.local.LastCityStore
import com.example.simpleweatherapp.domain.LatLon
import com.example.simpleweatherapp.presentation.WeatherDisplay
import com.example.simpleweatherapp.presentation.WeatherViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.net.UnknownHostException
import com.example.simpleweatherapp.presentation.UiError

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun sampleWeather() = WeatherDisplay(
        locationLabel = "Rochester",
        description = "clear sky",
        iconUrl = "https://openweathermap.org/img/wn/01d@2x.png",
        tempF = 72,
        feelsLikeF = 70,
        highF = 75,
        lowF = 65,
        humidityPct = 40,
        windMph = 8
    )

    @Test
    fun `searchByCity with blank input and no location sets CityOrLocation error`() = runTest {
        val repo = FakeWeatherRepository()
        val store = LastCityStore(FakePreferencesDataStore())
        val loc = FakeDeviceLocationProvider(next = null)

        val vm = WeatherViewModel(repo, store, loc)

        vm.onCityInputChanged("   ")
        vm.searchByCity()

        advanceUntilIdle()

        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertEquals(UiError.CityOrLocation, state.error)
        assertNull(state.weather)
    }

    @Test
    fun `searchByCity success updates weather and clears error`() = runTest {
        val repo = FakeWeatherRepository().apply {
            nextCityResult = Result.success(sampleWeather())
        }
        val store = LastCityStore(FakePreferencesDataStore())
        val loc = FakeDeviceLocationProvider(next = null)

        val vm = WeatherViewModel(repo, store, loc)

        vm.onCityInputChanged("Rochester,NY,US")
        vm.searchByCity()

        advanceUntilIdle()

        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertNotNull(state.weather)
        assertEquals("Rochester", state.weather!!.locationLabel)
    }

    @Test
    fun `searchByCity UnknownHostException maps to NoInternet`() = runTest {
        val repo = FakeWeatherRepository().apply {
            nextCityResult = Result.failure(UnknownHostException("Unable to resolve host"))
        }
        val store = LastCityStore(FakePreferencesDataStore())
        val loc = FakeDeviceLocationProvider(next = null)

        val vm = WeatherViewModel(repo, store, loc)

        vm.onCityInputChanged("Rochester,NY,US")
        vm.searchByCity()

        advanceUntilIdle()

        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertEquals(UiError.NoInternet, state.error)
        assertNull(state.weather)
    }

    @Test
    fun `pressing search with empty city uses device location`() = runTest {
        val repo = FakeWeatherRepository().apply {
            nextLatLonResult = Result.success(sampleWeather())
        }
        val store = LastCityStore(FakePreferencesDataStore())
        val loc = FakeDeviceLocationProvider(next = LatLon(42.8864, -78.8784))

        val vm = WeatherViewModel(repo, store, loc)

        vm.onCityInputChanged("")
        vm.searchByCity()

        advanceUntilIdle()

        assertNotNull(repo.lastLatLonCall)
        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertNotNull(state.weather)
    }

    @Test
    fun `empty city and no location sets CityOrLocation error`() = runTest {
        val repo = FakeWeatherRepository()
        val store = LastCityStore(FakePreferencesDataStore())
        val loc = FakeDeviceLocationProvider(next = null)

        val vm = WeatherViewModel(repo, store, loc)

        vm.onCityInputChanged("")
        vm.searchByCity()

        advanceUntilIdle()

        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertEquals(UiError.CityOrLocation, state.error)
        assertNull(state.weather)
    }
}