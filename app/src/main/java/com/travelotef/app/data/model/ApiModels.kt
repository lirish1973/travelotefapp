package com.travelotef.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * WooCommerce Store API - Product model
 * Matches the response from: /wp-json/wc/store/v1/products
 */
data class WcProduct(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("permalink")
    val permalink: String,
    @SerializedName("short_description")
    val shortDescription: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("on_sale")
    val onSale: Boolean,
    @SerializedName("prices")
    val prices: WcPrices,
    @SerializedName("price_html")
    val priceHtml: String?,
    @SerializedName("average_rating")
    val averageRating: String,
    @SerializedName("review_count")
    val reviewCount: Int,
    @SerializedName("images")
    val images: List<WcImage>,
    @SerializedName("categories")
    val categories: List<WcCategoryRef>,
    @SerializedName("tags")
    val tags: List<WcTag>?,
    @SerializedName("attributes")
    val attributes: List<WcAttribute>?,
    @SerializedName("variations")
    val variations: List<WcVariation>?,
    @SerializedName("is_purchasable")
    val isPurchasable: Boolean,
    @SerializedName("is_in_stock")
    val isInStock: Boolean,
    @SerializedName("add_to_cart")
    val addToCart: WcAddToCart?
)

/**
 * Price information
 */
data class WcPrices(
    @SerializedName("price")
    val price: String,
    @SerializedName("regular_price")
    val regularPrice: String,
    @SerializedName("sale_price")
    val salePrice: String,
    @SerializedName("currency_code")
    val currencyCode: String,
    @SerializedName("currency_symbol")
    val currencySymbol: String,
    @SerializedName("currency_prefix")
    val currencyPrefix: String?,
    @SerializedName("currency_suffix")
    val currencySuffix: String?
)

/**
 * Product image
 */
data class WcImage(
    @SerializedName("id")
    val id: Int,
    @SerializedName("src")
    val src: String,
    @SerializedName("thumbnail")
    val thumbnail: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("alt")
    val alt: String
)

/**
 * Category reference within a product
 */
data class WcCategoryRef(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("slug")
    val slug: String
)

/**
 * Full category model from /products/categories endpoint
 */
data class WcCategoryFull(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("parent")
    val parent: Int,
    @SerializedName("count")
    val count: Int,
    @SerializedName("image")
    val image: WcImage?,
    @SerializedName("permalink")
    val permalink: String?
)

/**
 * Product tag
 */
data class WcTag(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("slug")
    val slug: String
)

/**
 * Product attribute (e.g., date selection for tours)
 */
data class WcAttribute(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("has_variations")
    val hasVariations: Boolean,
    @SerializedName("terms")
    val terms: List<WcTerm>?
)

/**
 * Attribute term
 */
data class WcTerm(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("slug")
    val slug: String
)

/**
 * Product variation
 */
data class WcVariation(
    @SerializedName("id")
    val id: Int,
    @SerializedName("attributes")
    val attributes: List<WcVariationAttribute>?
)

/**
 * Variation attribute value
 */
data class WcVariationAttribute(
    @SerializedName("name")
    val name: String,
    @SerializedName("value")
    val value: String
)

/**
 * Add to cart info
 */
data class WcAddToCart(
    @SerializedName("text")
    val text: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("url")
    val url: String?
)
