package com.matvii.application.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.matvii.application.data.local.dao.FavoriteCityDao
import com.matvii.application.data.local.entity.FavoriteCityEntity

// Hlavní Room databáze aplikace (SQLite + Room).
@Database(
    entities = [FavoriteCityEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteCityDao(): FavoriteCityDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        // Vrací jednu sdílenou instanci databáze pro celou aplikaci.
        private fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build().also { created ->
                    instance = created
                }
            }
        }

        fun favoriteCityDao(context: Context): FavoriteCityDao {
            return getDatabase(context).favoriteCityDao()
        }
    }
}
