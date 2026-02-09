package com.travelotef.app

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.firebase.FirebaseApp
import com.travelotef.app.data.sync.TourSyncWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application class for Travelotef
 * Initializes Firebase, Hilt, and WorkManager for background sync with TryIt.co.il
 */
@HiltAndroidApp
class TravelotefApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Schedule background sync with WooCommerce Store API
        TourSyncWorker.schedule(this)

        // Set instance
        instance = this

        Log.d(TAG, "TravelotefApp initialized - syncing with tryit.co.il")
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.INFO)
            .build()

    companion object {
        private const val TAG = "TravelotefApp"
        lateinit var instance: TravelotefApp
            private set
    }
}
