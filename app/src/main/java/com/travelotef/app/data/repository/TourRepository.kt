package com.travelotef.app.data.repository

import com.travelotef.app.data.api.TryItApiService
import com.travelotef.app.data.local.*
import com.travelotef.app.data.model.toDomain
import com.travelotef.app.data.model.toEntity
import com.travelotef.app.domain.model.Category
import com.travelotef.app.domain.model.Tour
import com.travelotef.app.utils.Resource
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing tour data from TryIt.co.il
 * Implements offline-first strategy: Room cache -> WooCommerce API -> update cache
 */
@Singleton
class TourRepository @Inject constructor(
    private val apiService: TryItApiService,
    private val tourDao: TourDao,
    private val categoryDao: CategoryDao,
    private val favoriteDao: FavoriteDao
) {

    /**
     * Get all tours, optionally filtered by category
     * Offline-first: emit cache, then fetch from API
     */
    fun getTours(categoryId: Int? = null): Flow<Resource<List<Tour>>> = flow {
        emit(Resource.Loading())

        // Get favorite IDs for marking
        val favoriteIds = try {
            favoriteDao.getAllFavoriteIds().first()
        } catch (e: Exception) {
            emptyList()
        }

        // Emit cached data first
        try {
            val cachedFlow = if (categoryId != null) {
                tourDao.getToursByCategory(categoryId)
            } else {
                tourDao.getAllTours()
            }

            val cached = cachedFlow.first()
            if (cached.isNotEmpty()) {
                val tours = cached.map { entity ->
                    entity.toDomain(isFavorite = entity.id in favoriteIds)
                }
                emit(Resource.Success(tours))
            }
        } catch (e: Exception) {
            // Cache read failed, continue to fetch from API
        }

        // Fetch from API
        try {
            val products = apiService.getProducts(
                page = 1,
                perPage = 100,
                categoryId = categoryId
            )

            // Save to cache
            val entities = products.map { it.toEntity() }
            tourDao.insertAll(entities)

            // Emit fresh data
            val tours = products.map { product ->
                product.toDomain(isFavorite = product.id in favoriteIds)
            }
            emit(Resource.Success(tours))
        } catch (e: Exception) {
            // If we didn't emit cached data above, emit error
            val cached = tourDao.getCount()
            if (cached == 0) {
                emit(Resource.Error(e.message ?: "שגיאה בטעינת הסיורים"))
            }
        }
    }

    /**
     * Get a single tour by ID
     */
    fun getTourById(tourId: Int): Flow<Resource<Tour>> = flow {
        emit(Resource.Loading())

        val isFav = favoriteDao.isFavorite(tourId)

        // Try cache first
        val cached = tourDao.getTourById(tourId)
        if (cached != null) {
            emit(Resource.Success(cached.toDomain(isFavorite = isFav)))
        }

        // Fetch from API
        try {
            val product = apiService.getProduct(tourId)
            tourDao.insertTour(product.toEntity())
            emit(Resource.Success(product.toDomain(isFavorite = isFav)))
        } catch (e: Exception) {
            if (cached == null) {
                emit(Resource.Error(e.message ?: "שגיאה בטעינת הסיור"))
            }
        }
    }

    /**
     * Get all categories
     */
    fun getCategories(): Flow<Resource<List<Category>>> = flow {
        emit(Resource.Loading())

        // Emit cached categories first
        try {
            val cached = categoryDao.getAllCategories().first()
            if (cached.isNotEmpty()) {
                emit(Resource.Success(cached.map { it.toDomain() }))
            }
        } catch (e: Exception) {
            // Cache read failed
        }

        // Fetch from API
        try {
            val categories = apiService.getCategories()
            val entities = categories.map { it.toEntity() }
            categoryDao.insertAll(entities)

            val domainCategories = categories
                .filter { it.count > 0 }
                .map { it.toDomain() }
            emit(Resource.Success(domainCategories))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "שגיאה בטעינת הקטגוריות"))
        }
    }

    /**
     * Search tours by keyword
     */
    fun searchTours(query: String): Flow<Resource<List<Tour>>> = flow {
        emit(Resource.Loading())

        val favoriteIds = try {
            favoriteDao.getAllFavoriteIds().first()
        } catch (e: Exception) {
            emptyList()
        }

        // Search locally first
        try {
            val localResults = tourDao.searchTours(query).first()
            if (localResults.isNotEmpty()) {
                val tours = localResults.map { it.toDomain(isFavorite = it.id in favoriteIds) }
                emit(Resource.Success(tours))
            }
        } catch (e: Exception) {
            // Local search failed
        }

        // Search via API
        try {
            val products = apiService.getProducts(search = query)
            if (products.isNotEmpty()) {
                tourDao.insertAll(products.map { it.toEntity() })
                val tours = products.map { it.toDomain(isFavorite = it.id in favoriteIds) }
                emit(Resource.Success(tours))
            } else {
                val localResults = tourDao.searchTours(query).first()
                if (localResults.isEmpty()) {
                    emit(Resource.Success(emptyList()))
                }
            }
        } catch (e: Exception) {
            val localResults = tourDao.searchTours(query).first()
            if (localResults.isEmpty()) {
                emit(Resource.Error(e.message ?: "שגיאה בחיפוש"))
            }
        }
    }

    /**
     * Toggle favorite status for a tour
     */
    suspend fun toggleFavorite(tourId: Int) {
        val isFav = favoriteDao.isFavorite(tourId)
        if (isFav) {
            favoriteDao.delete(tourId)
        } else {
            favoriteDao.insert(FavoriteEntity(tourId = tourId))
        }
    }

    /**
     * Get all favorite tours
     */
    fun getFavoriteTours(): Flow<List<Tour>> {
        return favoriteDao.getFavoriteTours().map { entities ->
            entities.map { it.toDomain(isFavorite = true) }
        }
    }

    /**
     * Check if a tour is favorite
     */
    fun isFavorite(tourId: Int): Flow<Boolean> {
        return favoriteDao.isFavoriteFlow(tourId)
    }

    /**
     * Force refresh all data from API
     */
    suspend fun refreshAll() {
        try {
            val products = apiService.getProducts(page = 1, perPage = 100)
            tourDao.insertAll(products.map { it.toEntity() })

            val categories = apiService.getCategories()
            categoryDao.insertAll(categories.map { it.toEntity() })
        } catch (e: Exception) {
            // Refresh failed silently
        }
    }
}
