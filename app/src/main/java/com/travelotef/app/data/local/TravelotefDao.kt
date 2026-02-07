package com.travelotef.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Tours
 */
@Dao
interface TourDao {
    
    @Query("SELECT * FROM tours ORDER BY updatedAt DESC")
    fun getAllTours(): Flow<List<TourEntity>>
    
    @Query("SELECT * FROM tours WHERE id = :tourId")
    suspend fun getTourById(tourId: String): TourEntity?
    
    @Query("SELECT * FROM tours WHERE category = :category ORDER BY updatedAt DESC")
    fun getToursByCategory(category: String): Flow<List<TourEntity>>
    
    @Query("SELECT * FROM tours WHERE isAvailable = 1 ORDER BY rating DESC")
    fun getAvailableTours(): Flow<List<TourEntity>>
    
    @Query("SELECT * FROM tours WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchTours(query: String): Flow<List<TourEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTour(tour: TourEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTours(tours: List<TourEntity>)
    
    @Update
    suspend fun updateTour(tour: TourEntity)
    
    @Delete
    suspend fun deleteTour(tour: TourEntity)
    
    @Query("DELETE FROM tours")
    suspend fun deleteAllTours()
    
    @Transaction
    @Query("SELECT * FROM tours WHERE id = :tourId")
    suspend fun getTourWithLocations(tourId: String): TourWithLocations?
}

/**
 * Data Access Object for Locations
 */
@Dao
interface LocationDao {
    
    @Query("SELECT * FROM locations WHERE tourId = :tourId ORDER BY `order` ASC")
    suspend fun getLocationsByTourId(tourId: String): List<LocationEntity>
    
    @Query("SELECT * FROM locations WHERE id = :locationId")
    suspend fun getLocationById(locationId: String): LocationEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocations(locations: List<LocationEntity>)
    
    @Delete
    suspend fun deleteLocation(location: LocationEntity)
    
    @Query("DELETE FROM locations WHERE tourId = :tourId")
    suspend fun deleteLocationsByTourId(tourId: String)
}

/**
 * Data Access Object for User Tours
 */
@Dao
interface UserTourDao {
    
    @Query("SELECT * FROM user_tours WHERE userId = :userId ORDER BY purchaseDate DESC")
    fun getUserTours(userId: String): Flow<List<UserTourEntity>>
    
    @Query("SELECT * FROM user_tours WHERE tourId = :tourId AND userId = :userId")
    suspend fun getUserTour(tourId: String, userId: String): UserTourEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserTour(userTour: UserTourEntity)
    
    @Update
    suspend fun updateUserTour(userTour: UserTourEntity)
    
    @Delete
    suspend fun deleteUserTour(userTour: UserTourEntity)
}

/**
 * Data Access Object for Sync Metadata
 */
@Dao
interface SyncMetadataDao {
    
    @Query("SELECT * FROM sync_metadata WHERE key = :key")
    suspend fun getMetadata(key: String): SyncMetadataEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetadata(metadata: SyncMetadataEntity)
    
    @Query("DELETE FROM sync_metadata WHERE key = :key")
    suspend fun deleteMetadata(key: String)
}
