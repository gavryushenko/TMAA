package com.matvii.application.data.remote.firebase

import android.content.Context
import android.provider.Settings
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.matvii.application.domain.model.FavoriteCity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseBackupService {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Uloží snapshot oblíbených měst do Firestore jako cloudovou zálohu.
    suspend fun backupFavorites(context: Context, cities: List<FavoriteCity>) {
        val deviceId = resolveDeviceId(context)
        val normalizedCities = cities
            .map { it.name.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .sorted()

        val payload = mapOf(
            "cities" to normalizedCities,
            "cityCount" to normalizedCities.size,
            "updatedAt" to FieldValue.serverTimestamp(),
            "updatedAtEpochMs" to System.currentTimeMillis()
        )

        firestore
            .collection("backups")
            .document(deviceId)
            .set(payload)
            .await()
    }

    private fun resolveDeviceId(context: Context): String {
        val rawId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        return rawId?.takeIf { it.isNotBlank() } ?: "unknown-device"
    }
}

// Jednoduché await bez dodatečné knihovny coroutines-play-services.
private suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { continuation ->
    addOnSuccessListener { result ->
        if (continuation.isActive) continuation.resume(result)
    }

    addOnFailureListener { error ->
        if (continuation.isActive) continuation.resumeWithException(error)
    }

    addOnCanceledListener {
        if (continuation.isActive) continuation.cancel()
    }
}
