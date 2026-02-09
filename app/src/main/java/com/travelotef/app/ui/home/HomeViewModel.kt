package com.travelotef.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelotef.app.data.repository.TourRepository
import com.travelotef.app.domain.model.Category
import com.travelotef.app.domain.model.Tour
import com.travelotef.app.utils.Resource
import com.travelotef.app.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Home Screen
 * Manages tours, categories, search, and filter state
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tourRepository: TourRepository
) : ViewModel() {

    // Tours state
    private val _toursState = MutableStateFlow<UiState<List<Tour>>>(UiState.Loading)
    val toursState: StateFlow<UiState<List<Tour>>> = _toursState.asStateFlow()

    // Categories state
    private val _categoriesState = MutableStateFlow<UiState<List<Category>>>(UiState.Loading)
    val categoriesState: StateFlow<UiState<List<Category>>> = _categoriesState.asStateFlow()

    // Selected category filter
    private val _selectedCategory = MutableStateFlow<Int?>(null)
    val selectedCategory: StateFlow<Int?> = _selectedCategory.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Refresh state
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadTours()
        loadCategories()
    }

    /**
     * Load tours, optionally filtered by category
     */
    fun loadTours(categoryId: Int? = _selectedCategory.value) {
        viewModelScope.launch {
            tourRepository.getTours(categoryId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _toursState.value = UiState.Loading
                    is Resource.Success -> {
                        val tours = resource.data ?: emptyList()
                        _toursState.value = if (tours.isEmpty()) UiState.Empty else UiState.Success(tours)
                    }
                    is Resource.Error -> {
                        _toursState.value = UiState.Error(resource.message ?: "שגיאה בטעינה")
                    }
                }
            }
        }
    }

    /**
     * Load categories from API/cache
     */
    fun loadCategories() {
        viewModelScope.launch {
            tourRepository.getCategories().collect { resource ->
                when (resource) {
                    is Resource.Loading -> { /* Keep current state */ }
                    is Resource.Success -> {
                        val categories = resource.data ?: emptyList()
                        // Add "All" as first category
                        val allCategory = Category(id = -1, name = "הכל", slug = "all")
                        _categoriesState.value = UiState.Success(listOf(allCategory) + categories)
                    }
                    is Resource.Error -> {
                        _categoriesState.value = UiState.Error(resource.message ?: "שגיאה")
                    }
                }
            }
        }
    }

    /**
     * Select a category filter
     */
    fun selectCategory(categoryId: Int?) {
        _selectedCategory.value = categoryId
        _searchQuery.value = ""
        loadTours(categoryId)
    }

    /**
     * Search tours with debounce
     */
    fun searchTours(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()

        if (query.isBlank()) {
            loadTours()
            return
        }

        searchJob = viewModelScope.launch {
            delay(300) // Debounce
            tourRepository.searchTours(query).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _toursState.value = UiState.Loading
                    is Resource.Success -> {
                        val tours = resource.data ?: emptyList()
                        _toursState.value = if (tours.isEmpty()) UiState.Empty else UiState.Success(tours)
                    }
                    is Resource.Error -> {
                        _toursState.value = UiState.Error(resource.message ?: "שגיאה בחיפוש")
                    }
                }
            }
        }
    }

    /**
     * Toggle favorite for a tour
     */
    fun toggleFavorite(tour: Tour) {
        viewModelScope.launch {
            tourRepository.toggleFavorite(tour.id)
            // Reload to update UI
            if (_searchQuery.value.isNotBlank()) {
                searchTours(_searchQuery.value)
            } else {
                loadTours()
            }
        }
    }

    /**
     * Pull to refresh
     */
    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            tourRepository.refreshAll()
            loadTours()
            loadCategories()
            _isRefreshing.value = false
        }
    }
}
