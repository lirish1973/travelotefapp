package com.travelotef.app.ui.mytours

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelotef.app.data.repository.TourRepository
import com.travelotef.app.domain.model.Tour
import com.travelotef.app.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for My Tours (Favorites) screen
 * Manages favorite tours list and removal actions
 */
@HiltViewModel
class MyToursViewModel @Inject constructor(
    private val tourRepository: TourRepository
) : ViewModel() {

    private val _toursState = MutableStateFlow<UiState<List<Tour>>>(UiState.Loading)
    val toursState: StateFlow<UiState<List<Tour>>> = _toursState.asStateFlow()

    init {
        loadFavoriteTours()
    }

    /**
     * Load all favorite tours from local database
     */
    fun loadFavoriteTours() {
        viewModelScope.launch {
            _toursState.value = UiState.Loading
            try {
                tourRepository.getFavoriteTours()
                    .catch { e ->
                        _toursState.value = UiState.Error(e.message ?: "שגיאה בטעינת המועדפים")
                    }
                    .collect { tours ->
                        if (tours.isEmpty()) {
                            _toursState.value = UiState.Empty
                        } else {
                            _toursState.value = UiState.Success(tours)
                        }
                    }
            } catch (e: Exception) {
                _toursState.value = UiState.Error(e.message ?: "שגיאה בטעינת המועדפים")
            }
        }
    }

    /**
     * Remove a tour from favorites
     */
    fun removeFromFavorites(tourId: Int) {
        viewModelScope.launch {
            tourRepository.toggleFavorite(tourId)
            // The Flow from getFavoriteTours will automatically update
        }
    }
}
