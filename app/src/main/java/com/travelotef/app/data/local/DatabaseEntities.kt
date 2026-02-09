package com.travelotef.app.data.local

import androidx.room.*

/**
 * Room entity for Tour (WooCommerce Product)
 */
@Entity(tableName = "tours")
data class TourEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val slug: String,
    val permalink: String,
    val shortDescription: String,
    val description: String,
    val price: String,
    val regularPrice: String,
    val salePrice: String,
    val currencySymbol: String,
    val priceHtml: String,
    val onSale: Boolean,
    val averageRating: String,
    val reviewCount: Int,
    val thumbnailUrl: String,
    val imageUrls: String,          // JSON array of image URLs
    val categoryIds: String,        // JSON array of category IDs
    val categoryNames: String,      // JSON array of category names
    val isPurchasable: Boolean,
    val isInStock: Boolean,
    val lastUpdated: Long
)

/**
 * Room entity for Product Category
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val slug: String,
    val description: String,
    val parentId: Int,
    val productCount: Int,
    val imageUrl: String?,
    val permalink: String
)

/**
 * Room entity for Favorite tours
 */
@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val tourId: Int,
    val addedAt: Long = System.currentTimeMillis()
)
