package com.matvii.application

// Hlavní Activity aplikace: nastavuje navigaci mezi obrazovkami Compose.
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
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

// some modification
// ------------------------------------------------------------
// MainActivity - vstupní bod aplikace: notifikace + Compose UI + navigace
// ------------------------------------------------------------
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NotificationHelper.createNotificationChannel(this)
        NotificationHelper.requestNotificationPermissionIfNeeded(this)

        enableEdgeToEdge()

        setContent {
            ApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()

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
}
