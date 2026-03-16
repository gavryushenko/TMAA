package com.matvii.application.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entita pro tabulku oblíbených měst v lokální SQLite databázi.
@Entity(tableName = "favorite_cities")
data class FavoriteCityEntity(
    @PrimaryKey
    val name: String
)
