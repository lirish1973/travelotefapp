package com.travelotef.app.data.api

import com.travelotef.app.data.model.ApiResponse
import com.travelotef.app.data.model.ApiTour
import com.travelotef.app.data.model.ApiTourResponse
import com.travelotef.app.data.model.SyncStatus
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API service for TryIt.co.il integration
 * This is a template - actual endpoints need to be configured based on TryIt API documentation
 */
interface TryItApiService {
    
    /**
     * Get all available tours
     */
    @GET("api/tours")
    suspend fun getTours(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("category") category: String? = null,
        @Query("difficulty") difficulty: String? = null
    ): Response<ApiResponse<ApiTourResponse>>
    
    /**
     * Get tour by ID
     */
    @GET("api/tours/{id}")
    suspend fun getTourById(
        @Path("id") tourId: String
    ): Response<ApiResponse<ApiTour>>
    
    /**
     * Search tours by keyword
     */
    @GET("api/tours/search")
    suspend fun searchTours(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): Response<ApiResponse<ApiTourResponse>>
    
    /**
     * Get sync status
     */
    @GET("api/sync/status")
    suspend fun getSyncStatus(): Response<ApiResponse<SyncStatus>>
    
    /**
     * Get updated tours since last sync
     */
    @GET("api/sync/updates")
    suspend fun getUpdates(
        @Query("since") lastSyncTimestamp: String
    ): Response<ApiResponse<ApiTourResponse>>
}
