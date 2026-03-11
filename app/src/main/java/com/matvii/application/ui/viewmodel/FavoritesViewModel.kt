package com.matvii.application.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.matvii.application.R
import com.matvii.application.data.local.database.AppDatabase
import com.matvii.application.data.repository.FavoritesRepository
import com.matvii.application.data.repository.FirebaseBackupRepository
import com.matvii.application.data.repository.WeatherRepository
import com.matvii.application.ui.state.CityWeatherUiState
import com.matvii.application.ui.state.FavoritesUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext = application.applicationContext
    private val favoritesRepository = FavoritesRepository(AppDatabase.favoriteCityDao(appContext))
    private val weatherRepository = WeatherRepository()
    private val firebaseBackupRepository = FirebaseBackupRepository()

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoritesRepository.favoritesFlow().collectLatest { cities ->
                _uiState.update { current ->
                    current.copy(
                        favorites = cities,
                        weatherByCity = current.weatherByCity.filterKeys { key ->
                            cities.any { it.name == key }
                        }
                    )
                }
            }
        }
    }

    fun loadCityWeather(city: String) {
        val existing = _uiState.value.weatherByCity[city]
        if (existing is CityWeatherUiState.Loading || existing is CityWeatherUiState.Success) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(weatherByCity = it.weatherByCity + (city to CityWeatherUiState.Loading))
            }

            runCatching {
                weatherRepository.getCurrentWeather(city)
            }.onSuccess { weather ->
                _uiState.update {
                    it.copy(weatherByCity = it.weatherByCity + (city to CityWeatherUiState.Success(weather.temperatureC)))
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        weatherByCity = it.weatherByCity + (
                            city to CityWeatherUiState.Error(
                                throwable.message ?: appContext.getString(R.string.unknown_error)
                            )
                        )
                    )
                }
            }
        }
    }

    fun remove(city: String) {
        viewModelScope.launch {
            favoritesRepository.remove(city)
            _uiState.update {
                it.copy(weatherByCity = it.weatherByCity - city)
            }
        }
    }

    fun clear() {
        viewModelScope.launch {
            favoritesRepository.clear()
            _uiState.update { it.copy(weatherByCity = emptyMap()) }
        }
    }

    fun backupFavoritesToCloud() {
        val favoritesSnapshot = _uiState.value.favorites
        if (favoritesSnapshot.isEmpty()) {
            _uiState.update {
                it.copy(
                    backupMessage = appContext.getString(R.string.no_favorite_cities_to_backup),
                    isBackupError = true
                )
            }
            return
        }

        if (_uiState.value.isBackupInProgress) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isBackupInProgress = true,
                    backupMessage = null,
                    isBackupError = false
                )
            }

            // Záloha je manuální akce uživatele: uloží aktuální seznam oblíbených měst do cloudu.
            runCatching {
                firebaseBackupRepository.backupFavorites(appContext, favoritesSnapshot)
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isBackupInProgress = false,
                        backupMessage = appContext.getString(
                            R.string.backup_success_with_count,
                            favoritesSnapshot.size
                        ),
                        isBackupError = false
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isBackupInProgress = false,
                        backupMessage = appContext.getString(
                            R.string.backup_failed_with_message,
                            throwable.message ?: appContext.getString(R.string.unknown_error)
                        ),
                        isBackupError = true
                    )
                }
            }
        }
    }
}
