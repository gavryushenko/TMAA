// Soubor s hlavní funkcionalitou aplikace

package com.matvii.application

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.matvii.application.ui.theme.ApplicationTheme
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import kotlin.math.round

// ------------------------------------------------------------
// Navigační cesty - názvy obrazovek pro NavHost
// ------------------------------------------------------------
object Routes {
    const val MAIN = "main"
    const val SEARCH = "search"
    const val FAVORITES = "favorites"
}


// ------------------------------------------------------------
// MainActivity - vstupní bod aplikace: notifikace + Compose UI + navigace
// ------------------------------------------------------------
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Příprava notifikací: kanál + případné oprávnění
        createNotificationChannel()
        requestNotificationPermissionIfNeeded()

        // Moderní vykreslení UI pod systémové lišty
        enableEdgeToEdge()

        // Start Compose UI
        setContent {
            ApplicationTheme {
                // Základní layout aplikace (padding pro systémové prvky + obsah)
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()

                    // Navigace mezi obrazovkami
                    NavHost(
                        navController = navController,
                        startDestination = Routes.MAIN,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Routes.MAIN) {
                            MainScreen(navController = navController)
                        }
                        composable(Routes.SEARCH) {
                            SearchScreen(onBackClick = { navController.popBackStack() })
                        }
                        composable(Routes.FAVORITES) {
                            FavoritesScreen(onBackClick = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }

    // Notifikační kanál (nutné pro Android 8+)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "favorites_channel",
            "Favorites Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifications when a city is added to favorites"
        }

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    // Oprávnění pro notifikace (Android 13 / Tiramisu a vyšší)
    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }
}




// ------------------------------------------------------------
//                         MAIN SCREEN
// ------------------------------------------------------------
@Composable
fun MainScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome to my Weather Forecast App!")

        Spacer(modifier = Modifier.height(16.dp))

        // Navigace na obrazovku vyhledávání
        Button(onClick = { navController.navigate(Routes.SEARCH) }) {
            Text("Search")
        }

        // Navigace na obrazovku oblíbených měst
        Button(onClick = { navController.navigate(Routes.FAVORITES) }) {
            Text("Favorites")
        }
    }
}




// ------------------------------------------------------------
//                        SEARCH SCREEN
// ------------------------------------------------------------
data class WeatherResponse(
    val name: String,
    val main: MainDto
)

data class MainDto(
    val temp: Double
)

// Retrofit API - aktuální počasí pro město
interface OpenWeatherApi {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse
}

// Jednoduchý Retrofit klient
object ApiClient {
    private const val BASE_URL = "https://api.openweathermap.org/"

    val api: OpenWeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenWeatherApi::class.java)
    }
}

// Vyhledání teploty + uložení do Favorites + notifikace
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Stav UI (Compose State) - vstup, výsledek, loading, chyby
    var cityName by remember { mutableStateOf("") }
    var temperature by remember { mutableStateOf<Int?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope() // Coroutine scope pro spouštění síťových požadavků
    val apiKey = "cadfcab8aaacfb3a853ce158ad58aee1" // API klíč
    val context = LocalContext.current // Kontext je potřeba pro DataStore i notifikace
    var infoMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Horní lišta s tlačítkem zpět
        TopAppBar(
            title = { Text("Search City") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Vstup pro název města
        OutlinedTextField(
            value = cityName,
            onValueChange = { cityName = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            label = { Text("Type city name...") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Spuštění vyhledávání
        Button(
            onClick = {
                val query = cityName.trim()
                if (query.isEmpty()) {
                    errorMessage = "Please enter a city name."
                    return@Button
                }

                scope.launch {
                    try {
                        isLoading = true
                        errorMessage = null
                        temperature = null

                        val response = ApiClient.api.getCurrentWeather(
                            city = query,
                            apiKey = apiKey
                        )

                        temperature = kotlin.math.round(response.main.temp).toInt()

                    } catch (e: Exception) {
                        errorMessage = "Error: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Searching..." else "Search")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Pokud je známá teplota, zobrazíme výsledek + tlačítko pro uložení
        temperature?.let { temp ->

            Text(
                text = "Current temperature in $cityName:",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$temp°C",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            // Přidání do oblíbených + notifikace
            Button(
                onClick = {
                    val cityToSave = cityName.trim()
                    scope.launch {
                        FavoritesStore.add(context, cityToSave)
                        showFavoriteAddedNotification(context, cityToSave)
                        infoMessage = "Added to favorites: $cityToSave"
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Add to Favorites")
            }
        }

        // Zobrazení chyby (např. špatné město / síť)
        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Informační hláška (např. po přidání do favorites)
        infoMessage?.let {
            Text(
                text = it,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}


// Notifikace po přidání města do favorites
fun showFavoriteAddedNotification(context: Context, city: String) {

    // Na Android 13+ je nutné mít runtime oprávnění, jinak notifikaci neposíláme
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (!granted) return
    }

    val notification = NotificationCompat.Builder(context, "favorites_channel")
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle("City added to favorites")
        .setContentText("$city has been added to your favorites.")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()

    // Unikátní ID - díky tomu se notifikace nepřepisují navzájem
    NotificationManagerCompat.from(context)
        .notify(System.currentTimeMillis().toInt(), notification)
}




// ------------------------------------------------------------
//                        SEARCH SCREEN
// ------------------------------------------------------------

// UI stav pro načítání teploty
private sealed class CityWeatherUiState {
    data object Loading : CityWeatherUiState()
    data class Success(val tempC: Int) : CityWeatherUiState()
    data class Error(val message: String) : CityWeatherUiState()
}

// Seznam oblíbených měst + načtení teplot pro každé město
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // DataStore (Flow) -> Compose State: seznam oblíbených měst
    val favorites by FavoritesStore
        .favoritesFlow(context)
        .collectAsState(initial = emptyList())

    // Cache stavů počasí pro jednotlivá města (aby se nenačítalo stále dokola)
    val weatherStates: SnapshotStateMap<String, CityWeatherUiState> = remember {
        mutableStateMapOf()
    }

    val apiKey = "cadfcab8aaacfb3a853ce158ad58aee1"

    // Když se favorites změní, odstraníme z cache města, která už tam nejsou
    LaunchedEffect(favorites) {
        val set = favorites.toSet()
        val toRemove = weatherStates.keys.filter { it !in set }
        toRemove.forEach { weatherStates.remove(it) }
    }

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Favorite cities") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                // Tlačítko "Clear" se ukáže jen když je co mazat
                if (favorites.isNotEmpty()) {
                    Button(onClick = {
                        scope.launch {
                            FavoritesStore.clear(context)
                            weatherStates.clear()
                        }
                    }) {
                        Text("Clear")
                    }
                }
            }
        )

        // Prázdný stav
        if (favorites.isEmpty()) {
            Text(
                text = "No favorite cities yet.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                textAlign = TextAlign.Center
            )
        } else {
            // Seznam měst
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favorites, key = { it }) { city ->

                    // Pro každé město načteme teplotu jen jednou (pokud není v cache)
                    LaunchedEffect(city) {
                        if (weatherStates[city] == null) {
                            weatherStates[city] = CityWeatherUiState.Loading
                            try {
                                val response = ApiClient.api.getCurrentWeather(
                                    city = city,
                                    apiKey = apiKey
                                )
                                val tempInt = round(response.main.temp).toInt()
                                weatherStates[city] = CityWeatherUiState.Success(tempInt)
                            } catch (e: Exception) {
                                weatherStates[city] = CityWeatherUiState.Error(e.message ?: "Unknown error")
                            }
                        }
                    }

                    // Řádek s městem + teplota + tlačítko smazat
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "City name:")
                            Text(
                                text = city,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(text = "Current temperature:")

                            when (val st = weatherStates[city] ?: CityWeatherUiState.Loading) {
                                is CityWeatherUiState.Loading -> {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CircularProgressIndicator(modifier = Modifier.height(18.dp))
                                        Spacer(modifier = Modifier.height(0.dp))
                                        Text(
                                            text = " Loading...",
                                            modifier = Modifier.padding(start = 8.dp)
                                        )
                                    }
                                }

                                is CityWeatherUiState.Success -> {
                                    Text(
                                        text = "${st.tempC}°C",
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                }

                                is CityWeatherUiState.Error -> {
                                    Text(
                                        text = "Error",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }

                        // Odstranění konkrétního města z favorites
                        IconButton(
                            onClick = {
                                scope.launch {
                                    FavoritesStore.remove(context, city)
                                    weatherStates.remove(city)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete"
                            )
                        }
                    }
                }
            }
        }
    }
}