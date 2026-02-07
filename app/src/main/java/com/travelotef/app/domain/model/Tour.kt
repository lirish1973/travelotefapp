package com.travelotef.app.domain.model

import java.util.Date

/**
 * Domain model representing a Tour
 */
data class Tour(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val duration: String,
    val coverImageUrl: String,
    val category: String,
    val locations: List<Location>,
    val difficulty: TourDifficulty,
    val rating: Double,
    val reviewsCount: Int,
    val isAvailable: Boolean,
    val createdAt: Date,
    val updatedAt: Date
)

/**
 * Difficulty levels for tours
 */
enum class TourDifficulty {
    EASY,
    MODERATE,
    CHALLENGING,
    EXTREME
}

/**
 * Domain model representing a Location/Point of Interest
 */
data class Location(
    val id: String,
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val videoUrl: String?,
    val images: List<String>,
    val story: String,
    val links: List<Link>,
    val order: Int,
    val estimatedDuration: Int // in minutes
)

/**
 * External link model
 */
data class Link(
    val title: String,
    val url: String,
    val type: LinkType
)

enum class LinkType {
    ARTICLE,
    VIDEO,
    WEBSITE,
    SOCIAL_MEDIA,
    OTHER
}

/**
 * User's purchased tour
 */
data class UserTour(
    val tourId: String,
    val userId: String,
    val purchaseDate: Date,
    val expiryDate: Date?,
    val progress: TourProgress,
    val lastAccessedDate: Date?
)

/**
 * Track user's progress in a tour
 */
data class TourProgress(
    val completedLocations: List<String>,
    val currentLocationId: String?,
    val percentComplete: Int
)
