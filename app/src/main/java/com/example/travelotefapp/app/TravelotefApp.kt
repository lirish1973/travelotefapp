package com.travelotef.app

import android.app.Application
import com.google.firebase.FirebaseApp

/**
 * Application class for Travelotef
 * Initializes Firebase and other app-wide configurations
 */
class TravelotefApp : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        // Set instance
        instance = this
    }

    companion object {
        lateinit var instance: TravelotefApp
            private set
    }
}