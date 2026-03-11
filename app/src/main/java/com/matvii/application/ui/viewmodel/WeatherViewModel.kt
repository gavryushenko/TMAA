package com.matvii.application.ui.viewmodel

// ViewModel pro vyhledávání počasí a ukládání aktuálního města do oblíbených.
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.matvii.application.R
import com.matvii.application.data.local.database.AppDatabase
import com.matvii.application.data.repository.FavoritesRepository
import com.matvii.application.data.repository.WeatherRepository
import com.matvii.application.ui.state.WeatherUiState
import com.matvii.application.util.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext = application.applicationContext
    private val weatherRepository = WeatherRepository()
    private val favoritesRepository = FavoritesRepository(AppDatabase.favoriteCityDao(appContext))
    private val notificationHelper = NotificationHelper()

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    fun onCityNameChange(value: String) {
        _uiState.update { it.copy(cityName = value) }
    }

    fun searchWeather() {
        val query = _uiState.value.cityName.trim()
        if (query.isEmpty()) {
            _uiState.update {
                it.copy(errorMessage = appContext.getString(R.string.please_enter_city_name))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null, infoMessage = null, temperature = null)
            }

            runCatching {
                weatherRepository.getCurrentWeather(query)
            }.onSuccess { weather ->
                _uiState.update {
                    it.copy(
                        cityName = weather.cityName,
                        temperature = weather.temperatureC,
                        isLoading = false
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = appContext.getString(
                            R.string.error_with_message,
                            throwable.message ?: appContext.getString(R.string.unknown_error)
                        )
                    )
                }
            }
        }
    }

    fun addCurrentCityToFavorites() {
        val cityToSave = _uiState.value.cityName.trim()
        if (cityToSave.isEmpty()) return

        viewModelScope.launch {
            favoritesRepository.add(cityToSave)
            notificationHelper.showFavoriteAddedNotification(appContext, cityToSave)
            _uiState.update {
                it.copy(infoMessage = appContext.getString(R.string.added_to_favorites, cityToSave))
            }
        }
    }
}
