package com.example.util

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.R
import com.example.data.model.Wallpaper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

object WallpaperManagerHelper {

    enum class TargetScreen(val labelAr: String, val labelEn: String) {
        HOME("الشاشة الرئيسية", "Home Screen"),
        LOCK("شاشة القفل", "Lock Screen"),
        BOTH("الشاشتان معاً", "Both Screens")
    }

    suspend fun setWallpaper(
        context: Context,
        wallpaper: Wallpaper,
        targetScreen: TargetScreen
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val bitmap = loadBitmapForWallpaper(context, wallpaper)
                ?: return@withContext Result.failure(Exception("عذراً، تعذر تحميل الصورة"))

            val wallpaperManager = WallpaperManager.getInstance(context.applicationContext)

            when (targetScreen) {
                TargetScreen.HOME -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM)
                    } else {
                        wallpaperManager.setBitmap(bitmap)
                    }
                }
                TargetScreen.LOCK -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
                    } else {
                        wallpaperManager.setBitmap(bitmap)
                    }
                }
                TargetScreen.BOTH -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK)
                    } else {
                        wallpaperManager.setBitmap(bitmap)
                    }
                }
            }

            // Recycles bitmap to keep memory light
            if (!bitmap.isRecycled) {
                bitmap.recycle()
            }

            Result.success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    private fun loadBitmapForWallpaper(context: Context, wallpaper: Wallpaper): Bitmap? {
        return try {
            if (wallpaper.isLocalAsset && wallpaper.localDrawableRes != null) {
                BitmapFactory.decodeResource(context.resources, wallpaper.localDrawableRes)
            } else if (!wallpaper.imageUrl.isNullOrEmpty()) {
                val url = URL(wallpaper.imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 8000
                connection.readTimeout = 8000
                connection.doInput = true
                connection.connect()
                val inputStream: InputStream = connection.inputStream
                BitmapFactory.decodeStream(inputStream)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to local app icon if error occurs
            BitmapFactory.decodeResource(context.resources, R.drawable.img_wallpaper_nature)
        }
    }

    fun showToast(context: Context, message: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}
