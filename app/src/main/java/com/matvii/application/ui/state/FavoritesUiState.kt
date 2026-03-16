package com.matvii.application.ui.state

// UI stav pro obrazovku oblíbených měst včetně stavu cloudové zálohy.
import com.matvii.application.domain.model.FavoriteCity

sealed class CityWeatherUiState {
    data object Loading : CityWeatherUiState()
    data class Success(val tempC: Int) : CityWeatherUiState()
    data class Error(val message: String) : CityWeatherUiState()
}

data class FavoritesUiState(
    val favorites: List<FavoriteCity> = emptyList(),
    val weatherByCity: Map<String, CityWeatherUiState> = emptyMap(),
    val isBackupInProgress: Boolean = false,
    val backupMessage: String? = null,
    val isBackupError: Boolean = false
)
