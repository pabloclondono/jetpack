package com.sherlock.parcialcorte2.clima

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// COLOCA TU API KEY AQUÍ
const val OPENWEATHER_API_KEY = "7b4dab9e9a862c327bfb0b37834fa9a2"

data class WeatherResponse(
    val main: MainData,
    val weather: List<WeatherData>,
    val wind: WindData,
    val name: String
)

data class MainData(
    val temp: Float,
    val humidity: Int
)

data class WeatherData(
    val main: String,
    val description: String,
    val icon: String
)

data class WindData(
    val speed: Float
)

interface WeatherApiService {
    @GET("weather")
    suspend fun getWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "es"
    ): WeatherResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    val instance: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}
