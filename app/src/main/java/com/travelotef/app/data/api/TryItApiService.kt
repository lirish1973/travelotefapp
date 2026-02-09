package com.travelotef.app.data.api

import com.travelotef.app.data.model.WcCategoryFull
import com.travelotef.app.data.model.WcProduct
import retrofit2.http.*

/**
 * Retrofit API service for TryIt.co.il WooCommerce Store API
 * Base URL: https://www.tryit.co.il/wp-json/wc/store/v1/
 */
interface TryItApiService {

    /**
     * Get all available products (tours)
     */
    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("category") categoryId: Int? = null,
        @Query("search") search: String? = null,
        @Query("orderby") orderBy: String? = null,
        @Query("order") order: String? = null
    ): List<WcProduct>

    /**
     * Get a single product (tour) by ID
     */
    @GET("products/{id}")
    suspend fun getProduct(
        @Path("id") productId: Int
    ): WcProduct

    /**
     * Get all product categories
     */
    @GET("products/categories")
    suspend fun getCategories(
        @Query("per_page") perPage: Int = 100
    ): List<WcCategoryFull>
}
