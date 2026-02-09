package com.travelotef.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room Database for Travelotef app
 * Provides local caching for tours, categories, and favorites
 */
@Database(
    entities = [
        TourEntity::class,
        CategoryEntity::class,
        FavoriteEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class TravelotefDatabase : RoomDatabase() {

    abstract fun tourDao(): TourDao
    abstract fun categoryDao(): CategoryDao
    abstract fun favoriteDao(): FavoriteDao

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
