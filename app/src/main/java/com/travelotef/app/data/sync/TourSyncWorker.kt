package com.travelotef.app.data.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.travelotef.app.data.repository.TourRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

/**
 * Background sync worker using WorkManager
 * Periodically syncs tour data and categories from TryIt.co.il WooCommerce Store API
 * Runs every 12 hours when connected to network
 */
@HiltWorker
class TourSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val tourRepository: TourRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "Starting tour sync from tryit.co.il...")

        return try {
            // Sync all tours and categories from WooCommerce Store API
            tourRepository.refreshAll()

            Log.d(TAG, "Tour sync completed successfully")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Tour sync failed: ${e.message}", e)

            // Retry on error with exponential backoff
            if (runAttemptCount < MAX_RETRY_ATTEMPTS) {
                Log.d(TAG, "Retrying sync (attempt ${runAttemptCount + 1}/$MAX_RETRY_ATTEMPTS)")
                Result.retry()
            } else {
                Log.e(TAG, "Max retry attempts reached, sync failed")
                Result.failure()
            }
        }
    }

    companion object {
        private const val TAG = "TourSyncWorker"
        private const val MAX_RETRY_ATTEMPTS = 3
        private const val WORK_NAME = "tour_sync_work"
        private const val SYNC_INTERVAL_HOURS = 12L

        /**
         * Schedule periodic sync work
         * Runs every 12 hours with a 30-minute flex window
         */
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()

            val syncRequest = PeriodicWorkRequestBuilder<TourSyncWorker>(
                SYNC_INTERVAL_HOURS,
                TimeUnit.HOURS,
                30, // flex interval
                TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    10,
                    TimeUnit.MINUTES
                )
                .addTag("tour_sync")
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    syncRequest
                )

            Log.d(TAG, "Tour sync scheduled every $SYNC_INTERVAL_HOURS hours")
        }

        /**
         * Request immediate one-time sync
         */
        fun syncNow(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncRequest = OneTimeWorkRequestBuilder<TourSyncWorker>()
                .setConstraints(constraints)
                .addTag("tour_sync_immediate")
                .build()

            WorkManager.getInstance(context)
                .enqueue(syncRequest)

            Log.d(TAG, "Immediate tour sync requested")
        }

        /**
         * Cancel scheduled sync work
         */
        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
            Log.d(TAG, "Tour sync cancelled")
        }
    }
}
