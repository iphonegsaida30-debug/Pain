package com.example.data.model

import androidx.annotation.DrawableRes

enum class WallpaperCategory(val displayNameAr: String, val displayNameEn: String, val iconName: String) {
    ALL("الكل", "All", "AutoAwesome"),
    AMOLED("أموليد داكن", "AMOLED Dark", "Brightness3"),
    NATURE("طبيعة ساحرة", "Nature", "Forest"),
    ABSTRACT("تجريدي 3D", "Abstract", "Palette"),
    SPACE("فضاء ومجرات", "Space", "Public"),
    MINIMAL("بسيط وأنيق", "Minimal", "Minimalism"),
    CARS("سيارات وفاخرة", "Cars", "DirectionsCar"),
    ANIME("أنمي وفنون", "Anime & Art", "Brush"),
    ARCHITECTURE("عمارة ومدن", "Architecture", "LocationCity"),
    GRADIENT("تدرجات ملوّنة", "Gradients", "Gradient")
}

enum class WallpaperOrientation(val labelAr: String, val labelEn: String) {
    ALL("الكل", "All"),
    PORTRAIT("عمودي للهاتف", "Portrait"),
    HOMESCREEN("الشاشة الرئيسية", "Home Screen"),
    LOCKSCREEN("شاشة القفل", "Lock Screen")
}

data class Wallpaper(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val category: WallpaperCategory,
    val imageUrl: String? = null,
    @DrawableRes val localDrawableRes: Int? = null,
    val resolution: String = "2160 x 3840 (4K Ultra HD)",
    val fileSize: String = "1.2 MB",
    val downloadsCount: Int = 1250,
    val likesCount: Int = 430,
    val isLocalAsset: Boolean = false,
    val tags: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val dominantColorHex: String = "#0F172A"
)
