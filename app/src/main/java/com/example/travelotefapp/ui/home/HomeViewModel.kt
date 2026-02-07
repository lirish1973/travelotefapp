package com.example.travelotefapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelotefapp.data.model.Tour
import com.example.travelotefapp.data.repository.TourRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    
    private val repository = TourRepository()
    
    private val _tours = MutableLiveData<List<Tour>>()
    val tours: LiveData<List<Tour>> = _tours
    
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    init {
        loadTours()
    }
    
    fun loadTours() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            
            repository.getAllTours()
                .onSuccess { toursList ->
                    _tours.value = toursList
                    _loading.value = false
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "שגיאה בטעינת טיולים"
                    _loading.value = false
                    // Load mock data as fallback
                    loadMockData()
                }
        }
    }
    
    fun searchTours(query: String) {
        if (query.isEmpty()) {
            loadTours()
            return
        }
        
        viewModelScope.launch {
            _loading.value = true
            repository.searchTours(query)
                .onSuccess { toursList ->
                    _tours.value = toursList
                    _loading.value = false
                }
                .onFailure { exception ->
                    _error.value = exception.message
                    _loading.value = false
                }
        }
    }
    
    fun filterByCategory(category: String) {
        viewModelScope.launch {
            _loading.value = true
            repository.getToursByCategory(category)
                .onSuccess { toursList ->
                    _tours.value = toursList
                    _loading.value = false
                }
                .onFailure { exception ->
                    _error.value = exception.message
                    _loading.value = false
                }
        }
    }
    
    fun toggleFavorite(tour: Tour) {
        viewModelScope.launch {
            val updatedTour = tour.copy(isFavorite = !tour.isFavorite)
            repository.updateTour(updatedTour)
                .onSuccess {
                    // Refresh the list
                    val currentTours = _tours.value?.toMutableList() ?: mutableListOf()
                    val index = currentTours.indexOfFirst { it.id == tour.id }
                    if (index != -1) {
                        currentTours[index] = updatedTour
                        _tours.value = currentTours
                    }
                }
                .onFailure { exception ->
                    _error.value = exception.message
                }
        }
    }
    
    // Mock data for testing when Firebase is empty
    private fun loadMockData() {
        _tours.value = listOf(
            Tour(
                id = "mock1",
                title = "טיול בגליל העליון",
                description = "גלו את היופי של הגליל העליון במסלול מרהיב עם נופים עוצרי נשימה",
                duration = "3 שעות",
                rating = 4.8f,
                category = "טבע",
                location = "גליל עליון",
                price = 150.0
            ),
            Tour(
                id = "mock2",
                title = "סיור בעיר העתיקה ירושלים",
                description = "צאו למסע מרתק בעיר העתיקה, עם סיפורים והיסטוריה מרתקים",
                duration = "4 שעות",
                rating = 4.9f,
                category = "היסטוריה",
                location = "ירושלים",
                price = 200.0
            ),
            Tour(
                id = "mock3",
                title = "טיול יינות בגולן",
                description = "סיור באחת היקבים המובילים בארץ עם טעימות יין ונופים מדהימים",
                duration = "5 שעות",
                rating = 4.7f,
                category = "אוכל",
                location = "רמת הגולן",
                price = 250.0
            ),
            Tour(
                id = "mock4",
                title = "טיפוס וטיול בנחל דוד",
                description = "הרפתקה מרגשת עם טיפוס, מפלים ונופים מדהימים באזור עין גדי",
                duration = "6 שעות",
                rating = 4.6f,
                category = "הרפתקאות",
                location = "עין גדי",
                price = 180.0
            )
        )
    }
}