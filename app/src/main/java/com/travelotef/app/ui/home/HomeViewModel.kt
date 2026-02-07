package com.travelotef.app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelotef.app.data.repository.TourRepository
import com.travelotef.app.domain.model.Tour
import com.travelotef.app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Home Screen
 * Manages tour data and UI state
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tourRepository: TourRepository
) : ViewModel() {
    
    private val _tours = MutableLiveData<Resource<List<Tour>>>()
    val tours: LiveData<Resource<List<Tour>>> = _tours
    
    private val _searchResults = MutableLiveData<Resource<List<Tour>>>()
    val searchResults: LiveData<Resource<List<Tour>>> = _searchResults
    
    init {
        loadTours()
    }
    
    /**
     * Load all tours
     */
    fun loadTours(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            tourRepository.getAllTours(forceRefresh).collectLatest { resource ->
                _tours.value = resource
            }
        }
    }
    
    /**
     * Search tours by keyword
     */
    fun searchTours(query: String) {
        if (query.isBlank()) {
            _searchResults.value = _tours.value
            return
        }
        
        viewModelScope.launch {
            tourRepository.searchTours(query).collectLatest { resource ->
                _searchResults.value = resource
            }
        }
    }
    
    /**
     * Sync tours from remote API
     */
    fun syncTours() {
        viewModelScope.launch {
            _tours.value = Resource.Loading()
            val result = tourRepository.syncTours()
            if (result is Resource.Success) {
                loadTours(forceRefresh = true)
            } else if (result is Resource.Error) {
                _tours.value = Resource.Error(result.message ?: "Sync failed")
            }
        }
    }
    
    /**
     * Refresh tours (pull to refresh)
     */
    fun refreshTours() {
        syncTours()
    }
}
