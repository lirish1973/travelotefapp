package com.travelotef.app.data.model

import android.text.Html
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.travelotef.app.data.local.CategoryEntity
import com.travelotef.app.data.local.TourEntity
import com.travelotef.app.domain.model.Category
import com.travelotef.app.domain.model.Tour

/**
 * Mappers to convert between WooCommerce API models, Room entities, and Domain models
 */

private val gson = Gson()

// ==================== WcProduct -> Domain ====================

/**
 * Convert WooCommerce Product to Domain Tour
 */
fun WcProduct.toDomain(isFavorite: Boolean = false): Tour {
    return Tour(
        id = this.id,
        name = stripHtml(this.name),
        permalink = this.permalink,
        shortDescription = stripHtml(this.shortDescription),
        description = stripHtml(this.description),
        price = this.prices.price,
        regularPrice = this.prices.regularPrice,
        isOnSale = this.onSale,
        currencySymbol = this.prices.currencySymbol,
        thumbnailUrl = this.images.firstOrNull()?.thumbnail ?: "",
        imageUrls = this.images.map { it.src },
        categories = this.categories.map { it.toDomain() },
        isInStock = this.isInStock,
        isPurchasable = this.isPurchasable,
        averageRating = this.averageRating.toFloatOrNull() ?: 0f,
        reviewCount = this.reviewCount,
        isFavorite = isFavorite
    )
}

/**
 * Convert WooCommerce Category Reference to Domain Category
 */
fun WcCategoryRef.toDomain(): Category {
    return Category(
        id = this.id,
        name = stripHtml(this.name),
        slug = this.slug
    )
}

/**
 * Convert Full WooCommerce Category to Domain Category
 */
fun WcCategoryFull.toDomain(): Category {
    return Category(
        id = this.id,
        name = stripHtml(this.name),
        slug = this.slug,
        description = stripHtml(this.description ?: ""),
        productCount = this.count,
        imageUrl = this.image?.src
    )
}

// ==================== WcProduct -> Entity ====================

/**
 * Convert WooCommerce Product to Room Entity
 */
fun WcProduct.toEntity(): TourEntity {
    return TourEntity(
        id = this.id,
        name = stripHtml(this.name),
        slug = this.slug,
        permalink = this.permalink,
        shortDescription = this.shortDescription,
        description = this.description,
        price = this.prices.price,
        regularPrice = this.prices.regularPrice,
        salePrice = this.prices.salePrice,
        currencySymbol = this.prices.currencySymbol,
        priceHtml = this.priceHtml ?: "",
        onSale = this.onSale,
        averageRating = this.averageRating,
        reviewCount = this.reviewCount,
        thumbnailUrl = this.images.firstOrNull()?.thumbnail ?: "",
        imageUrls = gson.toJson(this.images.map { it.src }),
        categoryIds = gson.toJson(this.categories.map { it.id }),
        categoryNames = gson.toJson(this.categories.map { stripHtml(it.name) }),
        isPurchasable = this.isPurchasable,
        isInStock = this.isInStock,
        lastUpdated = System.currentTimeMillis()
    )
}

/**
 * Convert Full WooCommerce Category to Room Entity
 */
fun WcCategoryFull.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = this.id,
        name = stripHtml(this.name),
        slug = this.slug,
        description = stripHtml(this.description ?: ""),
        parentId = this.parent,
        productCount = this.count,
        imageUrl = this.image?.src,
        permalink = this.permalink ?: ""
    )
}

// ==================== Entity -> Domain ====================

/**
 * Convert Tour Entity to Domain Tour
 */
fun TourEntity.toDomain(isFavorite: Boolean = false): Tour {
    val imageList: List<String> = try {
        gson.fromJson(this.imageUrls, object : TypeToken<List<String>>() {}.type)
    } catch (e: Exception) {
        emptyList()
    }

    val catIds: List<Int> = try {
        gson.fromJson(this.categoryIds, object : TypeToken<List<Int>>() {}.type)
    } catch (e: Exception) {
        emptyList()
    }

    val catNames: List<String> = try {
        gson.fromJson(this.categoryNames, object : TypeToken<List<String>>() {}.type)
    } catch (e: Exception) {
        emptyList()
    }

    val categories = catIds.zip(catNames).map { (id, name) ->
        Category(id = id, name = name, slug = "")
    }

    return Tour(
        id = this.id,
        name = this.name,
        permalink = this.permalink,
        shortDescription = stripHtml(this.shortDescription),
        description = stripHtml(this.description),
        price = this.price,
        regularPrice = this.regularPrice,
        isOnSale = this.onSale,
        currencySymbol = this.currencySymbol,
        thumbnailUrl = this.thumbnailUrl,
        imageUrls = imageList,
        categories = categories,
        isInStock = this.isInStock,
        isPurchasable = this.isPurchasable,
        averageRating = this.averageRating.toFloatOrNull() ?: 0f,
        reviewCount = this.reviewCount,
        isFavorite = isFavorite
    )
}

/**
 * Convert Category Entity to Domain Category
 */
fun CategoryEntity.toDomain(): Category {
    return Category(
        id = this.id,
        name = this.name,
        slug = this.slug,
        description = this.description,
        productCount = this.productCount,
        imageUrl = this.imageUrl
    )
}

// ==================== Utility ====================

/**
 * Strip HTML tags from a string
 */
fun stripHtml(html: String): String {
    if (html.isBlank()) return ""
    return try {
        Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString().trim()
    } catch (e: Exception) {
        html.replace(Regex("<[^>]*>"), "").trim()
    }
}
