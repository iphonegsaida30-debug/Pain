package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.WallpaperDatabase
import com.example.data.model.Wallpaper
import com.example.data.model.WallpaperCategory
import com.example.data.model.WallpaperOrientation
import com.example.data.repository.WallpaperRepository
import com.example.util.WallpaperManagerHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val wallpapers: List<Wallpaper> = emptyList(),
    val favoriteIds: Set<String> = emptySet(),
    val selectedCategory: WallpaperCategory = WallpaperCategory.ALL,
    val searchQuery: String = "",
    val selectedOrientation: WallpaperOrientation = WallpaperOrientation.ALL,
    val gridColumns: Int = 2,
    val dailyWallpaper: Wallpaper? = null,
    val isLoading: Boolean = false,
    val applyingState: ApplyingWallpaperState = ApplyingWallpaperState.Idle
)

sealed class ApplyingWallpaperState {
    object Idle : ApplyingWallpaperState()
    object Applying : ApplyingWallpaperState()
    data class Success(val message: String) : ApplyingWallpaperState()
    data class Error(val errorMsg: String) : ApplyingWallpaperState()
}

class WallpaperViewModel(application: Application) : AndroidViewModel(application) {

    private val db = WallpaperDatabase.getDatabase(application)
    private val repository = WallpaperRepository(db.favoriteDao())

    private val _selectedCategory = MutableStateFlow(WallpaperCategory.ALL)
    val selectedCategory: StateFlow<WallpaperCategory> = _selectedCategory

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedOrientation = MutableStateFlow(WallpaperOrientation.ALL)
    val selectedOrientation: StateFlow<WallpaperOrientation> = _selectedOrientation

    private val _gridColumns = MutableStateFlow(2)
    val gridColumns: StateFlow<Int> = _gridColumns

    private val _selectedWallpaperForDetail = MutableStateFlow<Wallpaper?>(null)
    val selectedWallpaperForDetail: StateFlow<Wallpaper?> = _selectedWallpaperForDetail

    private val _applyingState = MutableStateFlow<ApplyingWallpaperState>(ApplyingWallpaperState.Idle)
    val applyingState: StateFlow<ApplyingWallpaperState> = _applyingState

    private val _aiGeneratedPrompt = MutableStateFlow("")
    val aiGeneratedPrompt: StateFlow<String> = _aiGeneratedPrompt

    val favoriteWallpapers: StateFlow<List<Wallpaper>> = repository.getFavoritesFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private data class FilterParams(
        val category: WallpaperCategory,
        val query: String,
        val columns: Int,
        val orientation: WallpaperOrientation
    )

    private val _filterState = combine(_selectedCategory, _searchQuery, _gridColumns, _selectedOrientation) { cat, query, cols, orientation ->
        FilterParams(cat, query, cols, orientation)
    }

    val uiState: StateFlow<HomeUiState> = combine(
        _filterState,
        repository.getFavoriteIdsFlow(),
        _applyingState
    ) { filter, favoriteIdsList, applying ->
        val favSet = favoriteIdsList.toSet()
        val allList = repository.getAllWallpapers()

        val filtered = allList.filter { wp ->
            val matchesCategory = (filter.category == WallpaperCategory.ALL || wp.category == filter.category)
            val matchesQuery = filter.query.isBlank() ||
                    wp.titleAr.contains(filter.query, ignoreCase = true) ||
                    wp.titleEn.contains(filter.query, ignoreCase = true) ||
                    wp.tags.any { it.contains(filter.query, ignoreCase = true) }

            matchesCategory && matchesQuery
        }.map { wp ->
            wp.copy(isFavorite = favSet.contains(wp.id))
        }

        HomeUiState(
            wallpapers = filtered,
            favoriteIds = favSet,
            selectedCategory = filter.category,
            searchQuery = filter.query,
            selectedOrientation = filter.orientation,
            gridColumns = filter.columns,
            dailyWallpaper = repository.getDailyHighlight(),
            applyingState = applying
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(wallpapers = repository.getAllWallpapers())
    )

    fun selectCategory(category: WallpaperCategory) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleGridColumns() {
        _gridColumns.value = if (_gridColumns.value == 2) 3 else 2
    }

    fun toggleFavorite(wallpaper: Wallpaper) {
        viewModelScope.launch {
            repository.toggleFavorite(wallpaper)
        }
    }

    fun openWallpaperDetail(wallpaper: Wallpaper) {
        _selectedWallpaperForDetail.value = wallpaper
    }

    fun closeWallpaperDetail() {
        _selectedWallpaperForDetail.value = null
    }

    fun applyWallpaper(wallpaper: Wallpaper, targetScreen: WallpaperManagerHelper.TargetScreen) {
        viewModelScope.launch {
            _applyingState.value = ApplyingWallpaperState.Applying
            val result = WallpaperManagerHelper.setWallpaper(getApplication(), wallpaper, targetScreen)
            result.fold(
                onSuccess = {
                    val msg = "تم تعيين الخلفية بنجاح على ${targetScreen.labelAr}"
                    _applyingState.value = ApplyingWallpaperState.Success(msg)
                    WallpaperManagerHelper.showToast(getApplication(), msg)
                },
                onFailure = { err ->
                    val errMsg = "فشل تعيين الخلفية: ${err.localizedMessage ?: "خطأ غير معروف"}"
                    _applyingState.value = ApplyingWallpaperState.Error(errMsg)
                    WallpaperManagerHelper.showToast(getApplication(), errMsg)
                }
            )
        }
    }

    fun resetApplyingState() {
        _applyingState.value = ApplyingWallpaperState.Idle
    }

    fun generateAiIdea(userPreference: String) {
        val ideas = listOf(
            "خلفية 4K لمدينة نيوتك ساحرة تحت مطر النيون بأسلوب السايبربانك",
            "سديم فضائي بنفسجي يتوهج خلف كوكب بلوري أزرق شفاف دقيق التفاصيل",
            "طبيعة يابانية هادئة مع أشجار الساكورا وشلال كستنائي في شروق الشمس",
            "لوحة تجريدية 3D بحواف ذهبية مصقولة وأمواج من الحرير الأسود والأرجواني",
            "سيارة رياضية فائقة ذات هيكل كربوني لامع على طريق ساحلي عند الغروب"
        )
        _aiGeneratedPrompt.value = ideas.random()
    }
}
