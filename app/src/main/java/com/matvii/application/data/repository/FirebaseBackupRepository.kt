package com.matvii.application.data.repository

import android.content.Context
import com.matvii.application.data.remote.firebase.FirebaseBackupService
import com.matvii.application.domain.model.FavoriteCity

class FirebaseBackupRepository(
    private val backupService: FirebaseBackupService = FirebaseBackupService()
) {
    // Repository odděluje ViewModel od konkrétní implementace Firebase vrstvy.
    suspend fun backupFavorites(context: Context, cities: List<FavoriteCity>) {
        backupService.backupFavorites(context, cities)
    }
}
