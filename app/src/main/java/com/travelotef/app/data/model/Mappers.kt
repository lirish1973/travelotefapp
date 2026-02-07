package com.travelotef.app.data.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.travelotef.app.data.local.LocationEntity
import com.travelotef.app.data.local.TourEntity
import com.travelotef.app.domain.model.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Mappers to convert between different model layers
 */

private val gson = Gson()
private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}

/**
 * Convert API Tour to Domain Tour
 */
fun ApiTour.toDomain(): Tour {
    return Tour(
        id = this.id,
        title = this.title,
        description = this.description,
        price = this.price,
        duration = this.duration,
        coverImageUrl = this.imageUrl,
        category = this.category,
        locations = this.locations?.map { it.toDomain() } ?: emptyList(),
        difficulty = parseDifficulty(this.difficulty),
        rating = this.rating,
        reviewsCount = this.reviewsCount,
        isAvailable = this.isAvailable,
        createdAt = parseDate(this.createdAt),
        updatedAt = parseDate(this.updatedAt)
    )
}

/**
 * Convert API Location to Domain Location
 */
fun ApiLocation.toDomain(): Location {
    return Location(
        id = this.id,
        name = this.name,
        description = this.description,
        latitude = this.latitude,
        longitude = this.longitude,
        videoUrl = this.videoUrl,
        images = this.images,
        story = this.story,
        links = this.links.map { it.toDomain() },
        order = this.order,
        estimatedDuration = this.estimatedDuration
    )
}

/**
 * Convert API Link to Domain Link
 */
fun ApiLink.toDomain(): Link {
    return Link(
        title = this.title,
        url = this.url,
        type = parseLinkType(this.type)
    )
}

/**
 * Convert Domain Tour to Entity
 */
fun Tour.toEntity(): TourEntity {
    return TourEntity(
        id = this.id,
        title = this.title,
        description = this.description,
        price = this.price,
        duration = this.duration,
        coverImageUrl = this.coverImageUrl,
        category = this.category,
        difficulty = this.difficulty.name,
        rating = this.rating,
        reviewsCount = this.reviewsCount,
        isAvailable = this.isAvailable,
        createdAt = this.createdAt.time,
        updatedAt = this.updatedAt.time,
        isSynced = true,
        lastSyncTime = System.currentTimeMillis()
    )
}

/**
 * Convert Domain Location to Entity
 */
fun Location.toEntity(tourId: String): LocationEntity {
    return LocationEntity(
        id = this.id,
        tourId = tourId,
        name = this.name,
        description = this.description,
        latitude = this.latitude,
        longitude = this.longitude,
        videoUrl = this.videoUrl,
        images = gson.toJson(this.images),
        story = this.story,
        links = gson.toJson(this.links),
        order = this.order,
        estimatedDuration = this.estimatedDuration
    )
}

/**
 * Convert Entity to Domain Tour
 */
fun TourEntity.toDomain(locations: List<Location> = emptyList()): Tour {
    return Tour(
        id = this.id,
        title = this.title,
        description = this.description,
        price = this.price,
        duration = this.duration,
        coverImageUrl = this.coverImageUrl,
        category = this.category,
        locations = locations,
        difficulty = TourDifficulty.valueOf(this.difficulty),
        rating = this.rating,
        reviewsCount = this.reviewsCount,
        isAvailable = this.isAvailable,
        createdAt = Date(this.createdAt),
        updatedAt = Date(this.updatedAt)
    )
}

/**
 * Convert Entity to Domain Location
 */
fun LocationEntity.toDomain(): Location {
    val imagesList: List<String> = try {
        gson.fromJson(this.images, object : TypeToken<List<String>>() {}.type)
    } catch (e: Exception) {
        emptyList()
    }
    
    val linksList: List<Link> = try {
        gson.fromJson(this.links, object : TypeToken<List<Link>>() {}.type)
    } catch (e: Exception) {
        emptyList()
    }
    
    return Location(
        id = this.id,
        name = this.name,
        description = this.description,
        latitude = this.latitude,
        longitude = this.longitude,
        videoUrl = this.videoUrl,
        images = imagesList,
        story = this.story,
        links = linksList,
        order = this.order,
        estimatedDuration = this.estimatedDuration
    )
}

/**
 * Helper functions
 */
private fun parseDifficulty(difficulty: String): TourDifficulty {
    return try {
        TourDifficulty.valueOf(difficulty.uppercase())
    } catch (e: Exception) {
        TourDifficulty.MODERATE
    }
}

private fun parseLinkType(type: String): LinkType {
    return try {
        LinkType.valueOf(type.uppercase())
    } catch (e: Exception) {
        LinkType.OTHER
    }
}

private fun parseDate(dateString: String): Date {
    return try {
        dateFormat.parse(dateString) ?: Date()
    } catch (e: Exception) {
        Date()
    }
}
