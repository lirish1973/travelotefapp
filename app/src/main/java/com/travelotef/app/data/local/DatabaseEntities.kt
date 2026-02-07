package com.travelotef.app.data.local

import androidx.room.*
import java.util.Date

/**
 * Room entity for Tour
 */
@Entity(tableName = "tours")
data class TourEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val duration: String,
    val coverImageUrl: String,
    val category: String,
    val difficulty: String,
    val rating: Double,
    val reviewsCount: Int,
    val isAvailable: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val isSynced: Boolean = false,
    val lastSyncTime: Long = 0
)

/**
 * Room entity for Location
 */
@Entity(
    tableName = "locations",
    foreignKeys = [
        ForeignKey(
            entity = TourEntity::class,
            parentColumns = ["id"],
            childColumns = ["tourId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tourId")]
)
data class LocationEntity(
    @PrimaryKey
    val id: String,
    val tourId: String,
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val videoUrl: String?,
    val images: String, // JSON string of image URLs
    val story: String,
    val links: String, // JSON string of links
    val order: Int,
    val estimatedDuration: Int
)

/**
 * Room entity for User's purchased tours
 */
@Entity(tableName = "user_tours")
data class UserTourEntity(
    @PrimaryKey
    val id: String,
    val tourId: String,
    val userId: String,
    val purchaseDate: Long,
    val expiryDate: Long?,
    val completedLocations: String, // JSON string of completed location IDs
    val currentLocationId: String?,
    val percentComplete: Int,
    val lastAccessedDate: Long?
)

/**
 * Room entity for tracking sync metadata
 */
@Entity(tableName = "sync_metadata")
data class SyncMetadataEntity(
    @PrimaryKey
    val key: String,
    val value: String,
    val lastUpdated: Long
)

/**
 * Data class for Tour with its locations
 */
data class TourWithLocations(
    @Embedded val tour: TourEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "tourId"
    )
    val locations: List<LocationEntity>
)
