package com.matvii.application.data.repository

// Repository vrstva pro načtení počasí z API a převod do doménového modelu.
import com.matvii.application.data.remote.api.ApiClient
import com.matvii.application.data.remote.api.WeatherApiService
import com.matvii.application.domain.model.WeatherModel
import kotlin.math.round

class WeatherRepository(
    private val apiService: WeatherApiService = ApiClient.weatherApi
) {
    suspend fun getCurrentWeather(city: String): WeatherModel {
        val response = apiService.getCurrentWeather(city = city)
        return WeatherModel(
            cityName = response.name,
            temperatureC = round(response.main.temp).toInt()
        )
    }
}
