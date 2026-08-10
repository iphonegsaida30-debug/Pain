package com.example.data.repository

import com.example.R
import com.example.data.local.FavoriteDao
import com.example.data.local.FavoriteWallpaperEntity
import com.example.data.model.Wallpaper
import com.example.data.model.WallpaperCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WallpaperRepository(private val favoriteDao: FavoriteDao) {

    // Pre-loaded curated HD wallpapers
    private val initialWallpapers = listOf(
        // Featured Local Assets (4K HD Generated)
        Wallpaper(
            id = "wp_loc_1",
            titleAr = "بحيرة سحرية والفقيرات الضوئية",
            titleEn = "Aurora Lake Night",
            category = WallpaperCategory.NATURE,
            localDrawableRes = R.drawable.img_wallpaper_nature,
            isLocalAsset = true,
            resolution = "3840 x 2160 (4K HD)",
            fileSize = "1.8 MB",
            downloadsCount = 4280,
            likesCount = 1890,
            tags = listOf("طبيعة", "بحيرة", "شفق", "جبال", "ليل", "Nature", "Aurora"),
            dominantColorHex = "#0D323A"
        ),
        Wallpaper(
            id = "wp_loc_2",
            titleAr = "قنديل نيون أموليد داكن",
            titleEn = "Neon Cyan Jellyfish",
            category = WallpaperCategory.AMOLED,
            localDrawableRes = R.drawable.img_wallpaper_amoled,
            isLocalAsset = true,
            resolution = "2160 x 3840 (OLED 4K)",
            fileSize = "1.1 MB",
            downloadsCount = 5910,
            likesCount = 2740,
            tags = listOf("أموليد", "نيون", "أسود", "AMOLED", "Dark", "Neon", "Void"),
            dominantColorHex = "#050B14"
        ),
        Wallpaper(
            id = "wp_loc_3",
            titleAr = "أمواج الحرير الذهبِي والباستيل",
            titleEn = "3D Pastel Silk Waves",
            category = WallpaperCategory.ABSTRACT,
            localDrawableRes = R.drawable.img_wallpaper_abstract,
            isLocalAsset = true,
            resolution = "2160 x 3840 (4K HD)",
            fileSize = "1.4 MB",
            downloadsCount = 3120,
            likesCount = 1420,
            tags = listOf("تجريدي", "حرير", "ذهبي", "3D", "Abstract", "Gold"),
            dominantColorHex = "#2A1F3D"
        ),
        Wallpaper(
            id = "wp_loc_4",
            titleAr = "مجرة وسديم كوني متلألئ",
            titleEn = "Cosmic Purple Galaxy",
            category = WallpaperCategory.SPACE,
            localDrawableRes = R.drawable.img_wallpaper_space,
            isLocalAsset = true,
            resolution = "2160 x 3840 (4K HD)",
            fileSize = "1.6 MB",
            downloadsCount = 6840,
            likesCount = 3110,
            tags = listOf("فضاء", "مجرة", "نجوم", "سديم", "Space", "Cosmos", "Nebula"),
            dominantColorHex = "#120B2E"
        ),

        // Online Curated High-Res HD Wallpapers
        Wallpaper(
            id = "wp_net_1",
            titleAr = "غابة ضبابية شتوية هادئة",
            titleEn = "Misty Forest Mountain",
            category = WallpaperCategory.NATURE,
            imageUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=1080&q=80",
            resolution = "2160 x 3840 (HD)",
            fileSize = "1.3 MB",
            downloadsCount = 2890,
            likesCount = 980,
            tags = listOf("غابة", "ضباب", "جبل", "هدوء", "Forest", "Mist"),
            dominantColorHex = "#1E293B"
        ),
        Wallpaper(
            id = "wp_net_2",
            titleAr = "ثقب أسود وأضواء نيون غامضة",
            titleEn = "Cyberpunk Dark City Portal",
            category = WallpaperCategory.AMOLED,
            imageUrl = "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?auto=format&fit=crop&w=1080&q=80",
            resolution = "2160 x 3840 (OLED)",
            fileSize = "1.0 MB",
            downloadsCount = 4720,
            likesCount = 1830,
            tags = listOf("أموليد", "سايبربانك", "نيون", "Cyberpunk", "Dark"),
            dominantColorHex = "#0B0F19"
        ),
        Wallpaper(
            id = "wp_net_3",
            titleAr = "سيارة رياضية فاخرة ليلاً",
            titleEn = "Supercar Night Reflection",
            category = WallpaperCategory.CARS,
            imageUrl = "https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&w=1080&q=80",
            resolution = "2160 x 3840 (4K)",
            fileSize = "1.5 MB",
            downloadsCount = 8200,
            likesCount = 4100,
            tags = listOf("سيارات", "سباق", "ليل", "Car", "Porsche", "Supercar"),
            dominantColorHex = "#18181B"
        ),
        Wallpaper(
            id = "wp_net_4",
            titleAr = "تدرج نيون أرجواني متوهج",
            titleEn = "Glowing Purple Gradient Wave",
            category = WallpaperCategory.GRADIENT,
            imageUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=1080&q=80",
            resolution = "2160 x 3840 (HD)",
            fileSize = "0.9 MB",
            downloadsCount = 1950,
            likesCount = 760,
            tags = listOf("تدرج", "أرجواني", "بسيط", "Gradient", "Purple"),
            dominantColorHex = "#312E81"
        ),
        Wallpaper(
            id = "wp_net_5",
            titleAr = "ناطحات سحاب وعمارة حديثة",
            titleEn = "Futuristic City Tower",
            category = WallpaperCategory.ARCHITECTURE,
            imageUrl = "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?auto=format&fit=crop&w=1080&q=80",
            resolution = "2160 x 3840 (4K)",
            fileSize = "1.4 MB",
            downloadsCount = 2340,
            likesCount = 890,
            tags = listOf("عمارة", "مدن", "ناطحة سحاب", "Architecture", "Building"),
            dominantColorHex = "#0F172A"
        ),
        Wallpaper(
            id = "wp_net_6",
            titleAr = "فن تجريدي برتقالي وأزرق دافئ",
            titleEn = "Minimalist Abstract Geometry",
            category = WallpaperCategory.MINIMAL,
            imageUrl = "https://images.unsplash.com/photo-1541701494587-cb58502866ab?auto=format&fit=crop&w=1080&q=80",
            resolution = "2160 x 3840 (HD)",
            fileSize = "1.1 MB",
            downloadsCount = 3400,
            likesCount = 1290,
            tags = listOf("بسيط", "تجريدي", "فن", "Minimal", "Art"),
            dominantColorHex = "#431407"
        ),
        Wallpaper(
            id = "wp_net_7",
            titleAr = "رائد فضاء يسبح في المحيط الكوني",
            titleEn = "Astronaut Floating in Deep Cosmos",
            category = WallpaperCategory.SPACE,
            imageUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=1080&q=80",
            resolution = "2160 x 3840 (4K)",
            fileSize = "1.7 MB",
            downloadsCount = 9120,
            likesCount = 5230,
            tags = listOf("رائد فضاء", "فضاء", "كواكب", "Space", "Astronaut"),
            dominantColorHex = "#090D16"
        ),
        Wallpaper(
            id = "wp_net_8",
            titleAr = "لوحة أنمي يابانية يدوية للغروب",
            titleEn = "Anime Sunset Horizon",
            category = WallpaperCategory.ANIME,
            imageUrl = "https://images.unsplash.com/photo-1579783902614-a3fb3927b675?auto=format&fit=crop&w=1080&q=80",
            resolution = "2160 x 3840 (HD)",
            fileSize = "1.2 MB",
            downloadsCount = 6100,
            likesCount = 2840,
            tags = listOf("أنمي", "فن", "غروب", "Anime", "Art", "Sunset"),
            dominantColorHex = "#451A03"
        ),
        Wallpaper(
            id = "wp_net_9",
            titleAr = "صحراء ذهبية وتحت سماء زرقاء",
            titleEn = "Golden Sand Dunes",
            category = WallpaperCategory.NATURE,
            imageUrl = "https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?auto=format&fit=crop&w=1080&q=80",
            resolution = "2160 x 3840 (4K)",
            fileSize = "1.3 MB",
            downloadsCount = 3880,
            likesCount = 1760,
            tags = listOf("صحراء", "رمال", "شمس", "Desert", "Dunes"),
            dominantColorHex = "#78350F"
        ),
        Wallpaper(
            id = "wp_net_10",
            titleAr = "خلفية سوداء نادرة بلمسة جولد",
            titleEn = "Black & Gold Luxury Texture",
            category = WallpaperCategory.AMOLED,
            imageUrl = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=1080&q=80",
            resolution = "2160 x 3840 (OLED)",
            fileSize = "0.9 MB",
            downloadsCount = 7430,
            likesCount = 3980,
            tags = listOf("أموليد", "أسود", "ذهبي", "AMOLED", "Gold", "Luxury"),
            dominantColorHex = "#0A0A0A"
        )
    )

    fun getAllWallpapers(): List<Wallpaper> = initialWallpapers

    fun getFavoriteIdsFlow(): Flow<List<String>> = favoriteDao.getFavoriteIds()

    fun getFavoritesFlow(): Flow<List<Wallpaper>> {
        return favoriteDao.getAllFavorites().map { entities ->
            entities.map { entity ->
                val category = try {
                    WallpaperCategory.valueOf(entity.categoryName)
                } catch (e: Exception) {
                    WallpaperCategory.ALL
                }
                Wallpaper(
                    id = entity.id,
                    titleAr = entity.titleAr,
                    titleEn = entity.titleEn,
                    category = category,
                    imageUrl = entity.imageUrl,
                    localDrawableRes = entity.localDrawableRes,
                    resolution = entity.resolution,
                    fileSize = entity.fileSize,
                    isLocalAsset = entity.isLocalAsset,
                    isFavorite = true
                )
            }
        }
    }

    suspend fun toggleFavorite(wallpaper: Wallpaper) {
        val favoriteEntity = FavoriteWallpaperEntity(
            id = wallpaper.id,
            titleAr = wallpaper.titleAr,
            titleEn = wallpaper.titleEn,
            categoryName = wallpaper.category.name,
            imageUrl = wallpaper.imageUrl,
            localDrawableRes = wallpaper.localDrawableRes,
            resolution = wallpaper.resolution,
            fileSize = wallpaper.fileSize,
            isLocalAsset = wallpaper.isLocalAsset
        )
        if (wallpaper.isFavorite) {
            favoriteDao.deleteFavorite(wallpaper.id)
        } else {
            favoriteDao.insertFavorite(favoriteEntity)
        }
    }

    fun getDailyHighlight(): Wallpaper {
        return initialWallpapers.first { it.id == "wp_loc_1" }
    }
}
