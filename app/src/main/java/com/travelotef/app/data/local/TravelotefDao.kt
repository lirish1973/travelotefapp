package com.travelotef.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Tours
 */
@Dao
interface TourDao {

    @Query("SELECT * FROM tours ORDER BY lastUpdated DESC")
    fun getAllTours(): Flow<List<TourEntity>>

    @Query("SELECT * FROM tours WHERE id = :tourId")
    suspend fun getTourById(tourId: Int): TourEntity?

    @Query("SELECT * FROM tours WHERE categoryIds LIKE '%' || :categoryId || '%' ORDER BY lastUpdated DESC")
    fun getToursByCategory(categoryId: Int): Flow<List<TourEntity>>

    @Query("SELECT * FROM tours WHERE name LIKE '%' || :query || '%' OR shortDescription LIKE '%' || :query || '%'")
    fun searchTours(query: String): Flow<List<TourEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tours: List<TourEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTour(tour: TourEntity)

    @Query("DELETE FROM tours")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM tours")
    suspend fun getCount(): Int
}

/**
 * Data Access Object for Categories
 */
@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories WHERE productCount > 0 ORDER BY name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: Int): CategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Query("DELETE FROM categories")
    suspend fun deleteAll()
}

/**
 * Data Access Object for Favorites
 */
@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT tourId FROM favorites")
    fun getAllFavoriteIds(): Flow<List<Int>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE tourId = :tourId)")
    suspend fun isFavorite(tourId: Int): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE tourId = :tourId)")
    fun isFavoriteFlow(tourId: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE tourId = :tourId")
    suspend fun delete(tourId: Int)

    @Query("SELECT t.* FROM tours t INNER JOIN favorites f ON t.id = f.tourId ORDER BY f.addedAt DESC")
    fun getFavoriteTours(): Flow<List<TourEntity>>
}
