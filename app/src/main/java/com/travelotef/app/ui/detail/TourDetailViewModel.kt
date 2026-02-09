package com.travelotef.app.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelotef.app.data.repository.TourRepository
import com.travelotef.app.domain.model.Tour
import com.travelotef.app.utils.Resource
import com.travelotef.app.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TourDetailViewModel @Inject constructor(
    private val tourRepository: TourRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val tourId: Int = savedStateHandle.get<Int>("tourId") ?: -1

    private val _tourState = MutableStateFlow<UiState<Tour>>(UiState.Loading)
    val tourState: StateFlow<UiState<Tour>> = _tourState.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    init {
        loadTour()
        observeFavorite()
    }

    private fun loadTour() {
        viewModelScope.launch {
            tourRepository.getTourById(tourId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _tourState.value = UiState.Loading
                    is Resource.Success -> {
                        resource.data?.let { _tourState.value = UiState.Success(it) }
                    }
                    is Resource.Error -> {
                        _tourState.value = UiState.Error(resource.message ?: "שגיאה בטעינת הסיור")
                    }
                }
            }
        }
    }

    private fun observeFavorite() {
        viewModelScope.launch {
            tourRepository.isFavorite(tourId).collect { _isFavorite.value = it }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            tourRepository.toggleFavorite(tourId)
        }
    }

    fun getBookingUrl(): String? {
        return (_tourState.value as? UiState.Success)?.data?.permalink
    }
}
