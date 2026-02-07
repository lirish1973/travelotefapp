package com.travelotef.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room Database for Travelotef app
 * Provides local caching for tours and user data
 */
@Database(
    entities = [
        TourEntity::class,
        LocationEntity::class,
        UserTourEntity::class,
        SyncMetadataEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class TravelotefDatabase : RoomDatabase() {
    
    abstract fun tourDao(): TourDao
    abstract fun locationDao(): LocationDao
    abstract fun userTourDao(): UserTourDao
    abstract fun syncMetadataDao(): SyncMetadataDao
    
    companion object {
        @Volatile
        private var INSTANCE: TravelotefDatabase? = null
        
        private const val DATABASE_NAME = "travelotef_database"
        
        fun getInstance(context: Context): TravelotefDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TravelotefDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
