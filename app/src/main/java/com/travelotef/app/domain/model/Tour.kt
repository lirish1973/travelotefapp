package com.travelotef.app.domain.model

/**
 * Domain model representing a Tour (product from TryIt.co.il)
 */
data class Tour(
    val id: Int,
    val name: String,
    val permalink: String,
    val shortDescription: String,
    val description: String,
    val price: String,
    val regularPrice: String,
    val isOnSale: Boolean,
    val currencySymbol: String,
    val thumbnailUrl: String,
    val imageUrls: List<String>,
    val categories: List<Category>,
    val isInStock: Boolean,
    val isPurchasable: Boolean,
    val averageRating: Float,
    val reviewCount: Int,
    val isFavorite: Boolean = false
) {
    /**
     * Formatted price string with currency symbol
     */
    val formattedPrice: String
        get() {
            val priceNum = price.toDoubleOrNull()
            return if (priceNum != null) {
                "$currencySymbol${priceNum.toInt()}"
            } else {
                "$currencySymbol$price"
            }
        }

    /**
     * First category name or empty string
     */
    val primaryCategory: String
        get() = categories.firstOrNull()?.name ?: ""
}

/**
 * Domain model representing a product Category
 */
data class Category(
    val id: Int,
    val name: String,
    val slug: String,
    val description: String = "",
    val productCount: Int = 0,
    val imageUrl: String? = null
)
