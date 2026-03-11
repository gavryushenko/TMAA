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
import com.matvii.application.theme.ApplicationTheme
import com.matvii.application.ui.screen.FavoritesScreen
import com.matvii.application.ui.screen.MainScreen
import com.matvii.application.ui.screen.Routes
import com.matvii.application.ui.screen.SearchScreen
import com.matvii.application.util.NotificationHelper

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
