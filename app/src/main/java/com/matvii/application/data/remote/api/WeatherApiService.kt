package com.matvii.application.data.remote.api

// Retrofit kontrakt a DTO modely pro endpoint aktuálního počasí.
import retrofit2.http.GET
import retrofit2.http.Query

data class WeatherResponseDto(
    val name: String,
    val main: MainDto
)

data class MainDto(
    val temp: Double
)

interface WeatherApiService {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("q") city: String,
        @Query("units") units: String = "metric"
    ): WeatherResponseDto
}
