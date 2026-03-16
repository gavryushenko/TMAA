package com.matvii.application.ui.state

// UI stav pro vyhledávání počasí na obrazovce Search.
data class WeatherUiState(
    val cityName: String = "",
    val temperature: Int? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null
)
