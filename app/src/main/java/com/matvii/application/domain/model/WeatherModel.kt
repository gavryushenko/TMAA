package com.matvii.application.domain.model

// Doménový model počasí používaný v UI vrstvě.
data class WeatherModel(
    val cityName: String,
    val temperatureC: Int
)
