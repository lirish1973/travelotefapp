package com.travelotef.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * API response model for TryIt.co.il tours
 */
data class ApiTourResponse(
    @SerializedName("tours")
    val tours: List<ApiTour>,
    @SerializedName("total")
    val total: Int,
    @SerializedName("page")
    val page: Int,
    @SerializedName("pageSize")
    val pageSize: Int
)

/**
 * API Tour model from TryIt
 */
data class ApiTour(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("duration")
    val duration: String,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("difficulty")
    val difficulty: String,
    @SerializedName("rating")
    val rating: Double,
    @SerializedName("reviews_count")
    val reviewsCount: Int,
    @SerializedName("available")
    val isAvailable: Boolean,
    @SerializedName("locations")
    val locations: List<ApiLocation>?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)

/**
 * API Location model
 */
data class ApiLocation(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("lat")
    val latitude: Double,
    @SerializedName("lng")
    val longitude: Double,
    @SerializedName("video_url")
    val videoUrl: String?,
    @SerializedName("images")
    val images: List<String>,
    @SerializedName("story")
    val story: String,
    @SerializedName("links")
    val links: List<ApiLink>,
    @SerializedName("order")
    val order: Int,
    @SerializedName("duration_minutes")
    val estimatedDuration: Int
)

/**
 * API Link model
 */
data class ApiLink(
    @SerializedName("title")
    val title: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("type")
    val type: String
)

/**
 * Generic API Response wrapper
 */
data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: T?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("error")
    val error: ApiError?
)

/**
 * API Error model
 */
data class ApiError(
    @SerializedName("code")
    val code: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("details")
    val details: Map<String, Any>?
)

/**
 * Sync status model
 */
data class SyncStatus(
    @SerializedName("last_sync")
    val lastSync: String,
    @SerializedName("tours_count")
    val toursCount: Int,
    @SerializedName("updates_available")
    val updatesAvailable: Boolean
)
