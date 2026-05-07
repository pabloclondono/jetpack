package com.sherlock.parcialcorte2.clima

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    var state by mutableStateOf(WeatherState())
        private set

    init {
        fetchWeather("Cali")
    }

    fun fetchWeather(city: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getWeather(city, OPENWEATHER_API_KEY)
                state = WeatherState(
                    condition = mapCondition(response.weather.firstOrNull()?.main ?: "", response.weather.firstOrNull()?.icon ?: ""),
                    temperature = response.main.temp.toInt(),
                    city = response.name,
                    humidity = response.main.humidity,
                    windKmh = (response.wind.speed * 3.6).toInt(),
                    uvIndex = 0, // El API gratuito de weather a veces no trae UV en la respuesta simple
                    conditionLabel = response.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: "N/A"
                )
            } catch (e: Exception) {
                // Manejar error
                e.printStackTrace()
            }
        }
    }

    private fun mapCondition(main: String, icon: String): WeatherCondition {
        return when {
            main.contains("Rain", ignoreCase = true) || main.contains("Drizzle", ignoreCase = true) || main.contains("Thunderstorm", ignoreCase = true) -> WeatherCondition.RAIN
            icon.contains("n") -> WeatherCondition.NIGHT
            else -> WeatherCondition.SUNNY
        }
    }
}
