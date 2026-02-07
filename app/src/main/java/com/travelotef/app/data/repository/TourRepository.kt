package com.travelotef.app.data.repository

import com.travelotef.app.data.api.TryItApiService
import com.travelotef.app.data.local.*
import com.travelotef.app.data.model.toDomain
import com.travelotef.app.data.model.toEntity
import com.travelotef.app.domain.model.Tour
import com.travelotef.app.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing tour data
 * Implements offline-first strategy with sync capabilities
 */
@Singleton
class TourRepository @Inject constructor(
    private val apiService: TryItApiService,
    private val tourDao: TourDao,
    private val locationDao: LocationDao,
    private val syncMetadataDao: SyncMetadataDao
) {
    
    /**
     * Get all tours from local database
     * Automatically updates from remote if cache is stale
     */
    fun getAllTours(forceRefresh: Boolean = false): Flow<Resource<List<Tour>>> = flow {
        // Emit loading state
        emit(Resource.Loading())
        
        try {
            // First, emit cached data
            val cachedTours = tourDao.getAllTours().map { entities ->
                entities.map { it.toDomain() }
            }
            
            // Emit cached data immediately
            cachedTours.collect { tours ->
                if (tours.isNotEmpty()) {
                    emit(Resource.Success(tours))
                }
            }
            
            // Check if we need to refresh from remote
            if (forceRefresh || shouldSync()) {
                syncTours()
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error occurred"))
        }
    }
    
    /**
     * Get tour by ID with locations
     */
    suspend fun getTourById(tourId: String): Resource<Tour> {
        return try {
            // Try to get from local database first
            val localTour = tourDao.getTourWithLocations(tourId)
            if (localTour != null) {
                val locations = localTour.locations.map { it.toDomain() }
                Resource.Success(localTour.tour.toDomain(locations))
            } else {
                // Fetch from remote if not in local database
                val response = apiService.getTourById(tourId)
                if (response.isSuccessful && response.body()?.success == true) {
                    val apiTour = response.body()?.data
                    if (apiTour != null) {
                        val tour = apiTour.toDomain()
                        // Cache the tour
                        cacheTour(tour)
                        Resource.Success(tour)
                    } else {
                        Resource.Error("Tour not found")
                    }
                } else {
                    Resource.Error(response.message() ?: "Failed to fetch tour")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }
    
    /**
     * Search tours by keyword
     */
    fun searchTours(query: String): Flow<Resource<List<Tour>>> = flow {
        emit(Resource.Loading())
        
        try {
            // Search in local database first
            tourDao.searchTours(query).collect { entities ->
                val tours = entities.map { it.toDomain() }
                emit(Resource.Success(tours))
            }
            
            // Also search remotely (optional enhancement)
            try {
                val response = apiService.searchTours(query)
                if (response.isSuccessful && response.body()?.success == true) {
                    val apiTours = response.body()?.data?.tours
                    if (apiTours != null) {
                        val remoteTours = apiTours.map { it.toDomain() }
                        // Cache remote results
                        remoteTours.forEach { cacheTour(it) }
                    }
                }
            } catch (e: Exception) {
                // Ignore remote search errors, we have local results
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Search failed"))
        }
    }
    
    /**
     * Sync tours from remote API
     */
    suspend fun syncTours(): Resource<Unit> {
        return try {
            val response = apiService.getTours(page = 1, pageSize = 100)
            if (response.isSuccessful && response.body()?.success == true) {
                val apiTours = response.body()?.data?.tours
                if (apiTours != null) {
                    // Convert and cache tours
                    apiTours.forEach { apiTour ->
                        val tour = apiTour.toDomain()
                        cacheTour(tour)
                    }
                    
                    // Update sync metadata
                    updateSyncTimestamp()
                    Resource.Success(Unit)
                } else {
                    Resource.Error("No tours data received")
                }
            } else {
                Resource.Error(response.message() ?: "Sync failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Sync failed")
        }
    }
    
    /**
     * Cache a tour and its locations to local database
     */
    private suspend fun cacheTour(tour: Tour) {
        // Insert tour
        tourDao.insertTour(tour.toEntity())
        
        // Insert locations
        val locationEntities = tour.locations.map { it.toEntity(tour.id) }
        locationDao.insertLocations(locationEntities)
    }
    
    /**
     * Check if we should sync based on last sync time
     */
    private suspend fun shouldSync(): Boolean {
        val lastSync = syncMetadataDao.getMetadata("last_sync_time")
        if (lastSync == null) return true
        
        val lastSyncTime = lastSync.value.toLongOrNull() ?: return true
        val now = System.currentTimeMillis()
        val hoursSinceLastSync = (now - lastSyncTime) / (1000 * 60 * 60)
        
        // Sync if more than 24 hours since last sync
        return hoursSinceLastSync >= 24
    }
    
    /**
     * Update sync timestamp
     */
    private suspend fun updateSyncTimestamp() {
        val metadata = SyncMetadataEntity(
            key = "last_sync_time",
            value = System.currentTimeMillis().toString(),
            lastUpdated = System.currentTimeMillis()
        )
        syncMetadataDao.insertMetadata(metadata)
    }
}
