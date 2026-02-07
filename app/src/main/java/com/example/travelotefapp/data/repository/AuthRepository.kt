package com.example.travelotefapp.data.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AuthRepository {
    
    private val auth:  FirebaseAuth = Firebase.auth
    
    val currentUser: FirebaseUser? 
        get() = auth.currentUser
    
    // Email/Password Sign Up
    suspend fun signUpWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Failed to create user"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Email/Password Sign In
    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Failed to sign in"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Google Sign In
    suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val result = auth.signInWithCredential(credential).await()
            result.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Failed to sign in with Google"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Sign Out
    fun signOut() {
        auth.signOut()
    }
    
    // Password Reset
    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e:  Exception) {
            Result.failure(e)
        }
    }
    
    // Check if user is logged in
    fun isUserLoggedIn(): Boolean {
        return currentUser != null
    }
}