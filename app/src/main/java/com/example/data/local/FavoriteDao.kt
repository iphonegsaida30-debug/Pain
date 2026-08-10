package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite_wallpapers ORDER BY addedTimestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteWallpaperEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_wallpapers WHERE id = :wallpaperId)")
    fun isFavorite(wallpaperId: String): Flow<Boolean>

    @Query("SELECT id FROM favorite_wallpapers")
    fun getFavoriteIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(wallpaper: FavoriteWallpaperEntity)

    @Query("DELETE FROM favorite_wallpapers WHERE id = :wallpaperId")
    suspend fun deleteFavorite(wallpaperId: String)

    @Query("DELETE FROM favorite_wallpapers")
    suspend fun clearAllFavorites()
}
