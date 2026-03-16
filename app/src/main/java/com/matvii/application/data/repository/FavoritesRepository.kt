package com.matvii.application.data.repository

import com.matvii.application.data.local.dao.FavoriteCityDao
import com.matvii.application.data.local.entity.FavoriteCityEntity
import com.matvii.application.domain.model.FavoriteCity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepository(
    private val dao: FavoriteCityDao
) {
    // Převádí databázové entity na doménový model pro UI vrstvu.
    fun favoritesFlow(): Flow<List<FavoriteCity>> {
        return dao.favoritesFlow().map { entities ->
            entities.map { FavoriteCity(name = it.name) }
        }
    }

    // Uloží město jen pokud má smysluplnou (neprázdnou) hodnotu.
    suspend fun add(city: String) {
        val normalized = city.trim()
        if (normalized.isEmpty()) return
        dao.insert(FavoriteCityEntity(name = normalized))
    }

    suspend fun remove(city: String) {
        val normalized = city.trim()
        if (normalized.isEmpty()) return
        dao.deleteByName(normalized)
    }

    suspend fun clear() = dao.clearAll()
}
