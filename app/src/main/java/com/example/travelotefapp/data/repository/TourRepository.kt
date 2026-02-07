package com.example.travelotefapp.data.repository

import com.example.travelotefapp.data.model.Tour
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class TourRepository {
    
    private val db:  FirebaseFirestore = Firebase.firestore
    private val toursCollection = db.collection("tours")
    
    // Get all tours
    suspend fun getAllTours(): Result<List<Tour>> {
        return try {
            val snapshot = toursCollection.get().await()
            val tours = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Tour::class.java)?.copy(id = doc.id)
            }
            Result.success(tours)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Get tours by category
    suspend fun getToursByCategory(category: String): Result<List<Tour>> {
        return try {
            val snapshot = toursCollection
                .whereEqualTo("category", category)
                .get()
                .await()
            val tours = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Tour::class.java)?.copy(id = doc.id)
            }
            Result.success(tours)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Search tours
    suspend fun searchTours(query: String): Result<List<Tour>> {
        return try {
            val allTours = getAllTours().getOrThrow()
            val filtered = allTours.filter { tour ->
                tour.title.contains(query, ignoreCase = true) ||
                tour.description.contains(query, ignoreCase = true) ||
                tour.location.contains(query, ignoreCase = true)
            }
            Result.success(filtered)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Add tour (for testing)
    suspend fun addTour(tour: Tour): Result<String> {
        return try {
            val docRef = toursCollection.add(tour).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Update tour
    suspend fun updateTour(tour: Tour): Result<Unit> {
        return try {
            toursCollection.document(tour.id).set(tour).await()
            Result.success(Unit)
        } catch (e:  Exception) {
            Result.failure(e)
        }
    }
    
    // Delete tour
    suspend fun deleteTour(tourId: String): Result<Unit> {
        return try {
            toursCollection.document(tourId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}