package com.matvii.application.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.matvii.application.data.local.entity.FavoriteCityEntity
import kotlinx.coroutines.flow.Flow

// DAO vrstva pro práci s tabulkou oblíbených měst.
@Dao
interface FavoriteCityDao {
    @Query("SELECT * FROM favorite_cities ORDER BY name COLLATE NOCASE ASC")
    fun favoritesFlow(): Flow<List<FavoriteCityEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(city: FavoriteCityEntity)

    @Query("DELETE FROM favorite_cities WHERE name = :cityName")
    suspend fun deleteByName(cityName: String)

    @Query("DELETE FROM favorite_cities")
    suspend fun clearAll()
}
