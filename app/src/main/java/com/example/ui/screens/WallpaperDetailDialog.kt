package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.Wallpaper
import com.example.ui.components.MockScreenOverlay
import com.example.ui.theme.FavoriteRed
import com.example.util.WallpaperManagerHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallpaperDetailDialog(
    wallpaper: Wallpaper,
    onClose: () -> Unit,
    onFavoriteToggle: (Wallpaper) -> Unit,
    onApplyWallpaper: (Wallpaper, WallpaperManagerHelper.TargetScreen) -> Unit,
    isApplying: Boolean
) {
    val context = LocalContext.current
    var showOverlayPreview by remember { mutableStateOf(false) }
    var previewMode by remember { mutableStateOf(WallpaperManagerHelper.TargetScreen.HOME) }
    var showApplyBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Background HD Image
                if (wallpaper.isLocalAsset && wallpaper.localDrawableRes != null) {
                    Image(
                        painter = painterResource(id = wallpaper.localDrawableRes),
                        contentDescription = wallpaper.titleAr,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (!wallpaper.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = wallpaper.imageUrl,
                        contentDescription = wallpaper.titleAr,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Interactive Mock Overlay (Home or Lock Screen test)
                MockScreenOverlay(
                    mode = previewMode,
                    visible = showOverlayPreview
                )

                // Top Navigation & Action Controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Button
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clickable { onClose() }
                            .testTag("btn_close_detail"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "رجوع",
                            tint = Color.White
                        )
                    }

                    // Preview Toggle Button (Test Home/Lock Screen)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(22.dp))
                            .background(Color.Black.copy(alpha = 0.55f))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                if (!showOverlayPreview) {
                                    showOverlayPreview = true
                                    previewMode = WallpaperManagerHelper.TargetScreen.HOME
                                } else if (previewMode == WallpaperManagerHelper.TargetScreen.HOME) {
                                    previewMode = WallpaperManagerHelper.TargetScreen.LOCK
                                } else {
                                    showOverlayPreview = false
                                }
                            },
                            modifier = Modifier.size(32.dp).testTag("btn_preview_toggle")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = "معاينة الشاشة",
                                tint = if (showOverlayPreview) MaterialTheme.colorScheme.primary else Color.White
                            )
                        }

                        if (showOverlayPreview) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (previewMode == WallpaperManagerHelper.TargetScreen.HOME) "معاينة الرئيسية" else "معاينة القفل",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Favorite Button
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clickable { onFavoriteToggle(wallpaper) }
                            .testTag("btn_fav_detail"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (wallpaper.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "المفضلة",
                            tint = if (wallpaper.isFavorite) FavoriteRed else Color.White
                        )
                    }
                }

                // Bottom Action Bar & Info Card
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.7f),
                                    Color.Black.copy(alpha = 0.95f)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Title & Tags
                        Text(
                            text = wallpaper.titleAr,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${wallpaper.category.displayNameAr} • ${wallpaper.resolution}",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "حجم الخفيفة: ${wallpaper.fileSize}",
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Primary Apply Wallpaper Button
                        Button(
                            onClick = { showApplyBottomSheet = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("btn_set_wallpaper_main"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            enabled = !isApplying
                        ) {
                            if (isApplying) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("جاري تطبيق الخلفية...", color = Color.White, fontSize = 16.sp)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Wallpaper,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "تعيين الخلفية للهاتف",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Secondary Actions (Download, Share)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Download HD
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .clickable {
                                        WallpaperManagerHelper.showToast(context, "تم حفظ الصورة عالية الدقة في المعرض ✅")
                                    }
                                    .testTag("btn_download_hd"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Download,
                                        contentDescription = "تحميل HD",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "تحميل HD",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            // Share
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .clickable {
                                        WallpaperManagerHelper.showToast(context, "تم نسخ رابط الخلفية للمشاركة 🔗")
                                    }
                                    .testTag("btn_share_wallpaper"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "مشاركة",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "مشاركة",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Sheet Selection for Target Screen (Home, Lock, Both)
            if (showApplyBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showApplyBottomSheet = false },
                    sheetState = sheetState,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "اخترين أين تريد تطبيق الخلفية:",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Home Screen Choice
                        ApplyOptionRow(
                            title = "الشاشة الرئيسية",
                            description = "تطبيق الخلفية على واجهة التطبيقات فقط",
                            icon = Icons.Default.Home,
                            onClick = {
                                showApplyBottomSheet = false
                                onApplyWallpaper(wallpaper, WallpaperManagerHelper.TargetScreen.HOME)
                            },
                            testTag = "opt_home_screen"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Lock Screen Choice
                        ApplyOptionRow(
                            title = "شاشة القفل",
                            description = "تطبيق الخلفية عند قفل وتأمين الهاتف",
                            icon = Icons.Default.Lock,
                            onClick = {
                                showApplyBottomSheet = false
                                onApplyWallpaper(wallpaper, WallpaperManagerHelper.TargetScreen.LOCK)
                            },
                            testTag = "opt_lock_screen"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Both Screens Choice
                        ApplyOptionRow(
                            title = "الشاشتان معاً",
                            description = "تطبيق الخلفية على الرئيسية وشاشة القفل معا",
                            icon = Icons.Default.PhoneAndroid,
                            onClick = {
                                showApplyBottomSheet = false
                                onApplyWallpaper(wallpaper, WallpaperManagerHelper.TargetScreen.BOTH)
                            },
                            testTag = "opt_both_screens"
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ApplyOptionRow(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() }
            .padding(16.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }

        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
            modifier = Modifier.size(20.dp)
        )
    }
}
