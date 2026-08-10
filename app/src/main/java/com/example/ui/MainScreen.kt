package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AiStudioScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WallpaperDetailDialog
import com.example.ui.theme.FavoriteRed
import com.example.ui.viewmodel.ApplyingWallpaperState
import com.example.ui.viewmodel.WallpaperViewModel

enum class MainTab(
    val titleAr: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    HOME("الرئيسية", Icons.Filled.Home, Icons.Outlined.Home, "tab_home"),
    FAVORITES("المفضلة", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder, "tab_favorites"),
    AI_STUDIO("استوديو AI", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome, "tab_ai_studio"),
    SETTINGS("الإعدادات", Icons.Filled.Settings, Icons.Outlined.Settings, "tab_settings")
}

@Composable
fun MainScreen(
    viewModel: WallpaperViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val favoriteWallpapers by viewModel.favoriteWallpapers.collectAsState()
    val selectedDetailWallpaper by viewModel.selectedWallpaperForDetail.collectAsState()
    val aiGeneratedPrompt by viewModel.aiGeneratedPrompt.collectAsState()

    var currentTab by remember { mutableStateOf(MainTab.HOME) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .testTag("main_navigation_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                MainTab.entries.forEach { tab ->
                    val isSelected = tab == currentTab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.titleAr,
                                tint = if (isSelected) {
                                    if (tab == MainTab.FAVORITES) FavoriteRed else MaterialTheme.colorScheme.primary
                                } else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        label = {
                            Text(
                                text = tab.titleAr,
                                fontSize = 11.sp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.testTag(tab.testTag),
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Crossfade(
            targetState = currentTab,
            modifier = Modifier.padding(innerPadding),
            label = "tabCrossfade"
        ) { tab ->
            when (tab) {
                MainTab.HOME -> {
                    HomeScreen(
                        uiState = uiState,
                        onCategorySelected = { viewModel.selectCategory(it) },
                        onSearchQueryChanged = { viewModel.setSearchQuery(it) },
                        onToggleGridColumns = { viewModel.toggleGridColumns() },
                        onWallpaperClick = { viewModel.openWallpaperDetail(it) },
                        onFavoriteToggle = { viewModel.toggleFavorite(it) }
                    )
                }

                MainTab.FAVORITES -> {
                    FavoritesScreen(
                        favoriteWallpapers = favoriteWallpapers,
                        onWallpaperClick = { viewModel.openWallpaperDetail(it) },
                        onFavoriteToggle = { viewModel.toggleFavorite(it) },
                        gridColumns = uiState.gridColumns
                    )
                }

                MainTab.AI_STUDIO -> {
                    AiStudioScreen(
                        aiGeneratedPrompt = aiGeneratedPrompt,
                        onGenerateIdea = { viewModel.generateAiIdea(it) }
                    )
                }

                MainTab.SETTINGS -> {
                    SettingsScreen()
                }
            }
        }

        // Fullscreen Detail & Wallpaper Application Dialog
        selectedDetailWallpaper?.let { wallpaper ->
            val isApplying = uiState.applyingState is ApplyingWallpaperState.Applying

            WallpaperDetailDialog(
                wallpaper = wallpaper,
                onClose = { viewModel.closeWallpaperDetail() },
                onFavoriteToggle = { viewModel.toggleFavorite(it) },
                onApplyWallpaper = { wp, target ->
                    viewModel.applyWallpaper(wp, target)
                },
                isApplying = isApplying
            )
        }
    }
}
