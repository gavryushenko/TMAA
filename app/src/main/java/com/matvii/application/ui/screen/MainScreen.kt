package com.matvii.application.ui.screen

// Úvodní obrazovka aplikace s přechody na vyhledávání a seznam oblíbených měst.
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.matvii.application.R

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
        Text(text = stringResource(R.string.main_welcome))

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate(Routes.SEARCH) }) {
            Text(stringResource(R.string.search))
        }

        Button(onClick = { navController.navigate(Routes.FAVORITES) }) {
            Text(stringResource(R.string.favorites))
        }
    }
}
