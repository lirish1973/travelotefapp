package com.travelotef.app.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.travelotef.app.data.api.TryItApiService
import com.travelotef.app.data.local.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt module for providing app-wide dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    /**
     * Base URL for TryIt API
     * TODO: Replace with actual TryIt.co.il API endpoint
     */
    private const val BASE_URL = "https://api.tryit.co.il/"
    
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    @Provides
    @Singleton
    fun provideTryItApiService(retrofit: Retrofit): TryItApiService {
        return retrofit.create(TryItApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideTravelotefDatabase(@ApplicationContext context: Context): TravelotefDatabase {
        return TravelotefDatabase.getInstance(context)
    }
    
    @Provides
    @Singleton
    fun provideTourDao(database: TravelotefDatabase): TourDao {
        return database.tourDao()
    }
    
    @Provides
    @Singleton
    fun provideLocationDao(database: TravelotefDatabase): LocationDao {
        return database.locationDao()
    }
    
    @Provides
    @Singleton
    fun provideUserTourDao(database: TravelotefDatabase): UserTourDao {
        return database.userTourDao()
    }
    
    @Provides
    @Singleton
    fun provideSyncMetadataDao(database: TravelotefDatabase): SyncMetadataDao {
        return database.syncMetadataDao()
    }
}
