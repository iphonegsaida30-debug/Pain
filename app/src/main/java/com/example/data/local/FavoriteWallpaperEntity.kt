package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_wallpapers")
data class FavoriteWallpaperEntity(
    @PrimaryKey val id: String,
    val titleAr: String,
    val titleEn: String,
    val categoryName: String,
    val imageUrl: String?,
    val localDrawableRes: Int?,
    val resolution: String,
    val fileSize: String,
    val isLocalAsset: Boolean,
    val addedTimestamp: Long = System.currentTimeMillis()
)
