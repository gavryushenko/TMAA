package com.matvii.application.ui.screen

// Obrazovka zobrazuje oblíbená města, jejich teplotu a akce nad seznamem (backup/clear).
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.matvii.application.R
import com.matvii.application.ui.state.CityWeatherUiState
import com.matvii.application.ui.viewmodel.FavoritesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    favoritesViewModel: FavoritesViewModel = viewModel()
) {
    val uiState by favoritesViewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(stringResource(R.string.favorite_cities_title)) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            },
            actions = {
                if (uiState.favorites.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = favoritesViewModel::backupFavoritesToCloud,
                            enabled = !uiState.isBackupInProgress
                        ) {
                            if (uiState.isBackupInProgress) {
                                CircularProgressIndicator(modifier = Modifier.height(18.dp))
                            } else {
                                Text(stringResource(R.string.backup))
                            }
                        }

                        Button(
                            onClick = favoritesViewModel::clear,
                            enabled = !uiState.isBackupInProgress
                        ) {
                            Text(stringResource(R.string.clear))
                        }
                    }
                }
            }
        )

        uiState.backupMessage?.let { message ->
            Text(
                text = message,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                color = if (uiState.isBackupError) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
        }

        if (uiState.favorites.isEmpty()) {
            Text(
                text = stringResource(R.string.no_favorite_cities_yet),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                textAlign = TextAlign.Center
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.favorites, key = { it.name }) { city ->
                    LaunchedEffect(city.name) {
                        favoritesViewModel.loadCityWeather(city.name)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = stringResource(R.string.city_name))
                            Text(
                                text = city.name,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(text = stringResource(R.string.current_temperature))

                            when (val weatherState = uiState.weatherByCity[city.name] ?: CityWeatherUiState.Loading) {
                                is CityWeatherUiState.Loading -> {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CircularProgressIndicator(modifier = Modifier.height(18.dp))
                                        Text(
                                            text = stringResource(R.string.loading),
                                            modifier = Modifier.padding(start = 8.dp)
                                        )
                                    }
                                }

                                is CityWeatherUiState.Success -> {
                                    Text(
                                        text = stringResource(R.string.temperature_celsius, weatherState.tempC),
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                }

                                is CityWeatherUiState.Error -> {
                                    Text(
                                        text = stringResource(R.string.error),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }

                        IconButton(onClick = { favoritesViewModel.remove(city.name) }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = stringResource(R.string.delete)
                            )
                        }
                    }
                }
            }
        }
    }
}
