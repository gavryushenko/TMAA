// Soubor pro ukládání, načítání a správě oblíbených měst pomocí DataStore
// DataStore - je lokální úložiště v Androidu

package com.matvii.application

// Import potřebných tříd pro práci s DataStore a Kotlin Flow
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Vytvoření instance DataStore pro ukládání dat
private val Context.dataStore by preferencesDataStore(name = "favorites")

// Objekt, který obsahuje logiku pro práci s oblíbenými městy
object FavoritesStore {
    // Definice klíče pro ukládání množiny oblíbených měst
    private val KEY_FAVORITES = stringSetPreferencesKey("favorite_cities")


    // Funkce vrací seznam oblíbených měst jako Flow (reaktivní proud dat)
    fun favoritesFlow(context: Context): Flow<List<String>> {
        // Načtení uložených dat z DataStore
        return context.dataStore.data.map { prefs ->
            // Pokud nejsou žádná data, vrátí se prázdná množina
            (prefs[KEY_FAVORITES] ?: emptySet())
                // Převedení na seznam a seřazení podle abecedy
                .toList()
                .sortedBy { it.lowercase() }
        }
    }

    // Funkce pro přidání města do oblíbených
    suspend fun add(context: Context, city: String) {
        // Odstranění mezer na začátku a na konci textu
        val normalized = city.trim()
        // Pokud je text prázdný, funkce se ukončí
        if (normalized.isEmpty()) return

        // Úprava uložených dat
        context.dataStore.edit { prefs ->
            // Přidání nového města do existující množiny
            val current = prefs[KEY_FAVORITES] ?: emptySet()
            prefs[KEY_FAVORITES] = current + normalized
        }
    }

    // Funkce pro odstranění města z oblíbených
    suspend fun remove(context: Context, city: String) {
        val normalized = city.trim()
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_FAVORITES] ?: emptySet()
            // Odebrání města z množiny
            prefs[KEY_FAVORITES] = current - normalized
        }
    }

    // Funkce pro vymazání všech oblíbených měst
    suspend fun clear(context: Context) {
        context.dataStore.edit { prefs ->
            // Nastavení prázdné množiny
            prefs[KEY_FAVORITES] = emptySet()
        }
    }
}