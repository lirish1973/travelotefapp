package com.example.travelotefapp.data.model

data class Tour(
    val id:  String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val duration: String = "",
    val rating: Float = 0f,
    val category: String = "",
    val location: String = "",
    val price: Double = 0.0,
    val isFavorite: Boolean = false
)