# TryIt.co.il API Integration Guide

## Overview
This document describes the integration between Travelotef app and TryIt.co.il API for tour synchronization.

## Architecture

### Offline-First Strategy
The app implements an offline-first architecture:
1. **Local Database (Room)**: Primary data source
2. **Remote API (TryIt.co.il)**: Secondary data source for sync
3. **Background Sync (WorkManager)**: Automatic updates every 24 hours

### Data Flow
```
┌─────────────┐       ┌──────────────┐       ┌─────────────┐
│   TryIt     │◄──────│  Repository  │◄──────│     UI      │
│  API (REST) │       │   (Offline)  │       │  (ViewModel)│
└─────────────┘       └──────────────┘       └─────────────┘
                             │
                             ▼
                      ┌──────────────┐
                      │    Room DB   │
                      │   (SQLite)   │
                      └──────────────┘
```

## API Configuration

### Base URL
The API base URL is configured in `AppModule.kt`:
```kotlin
private const val BASE_URL = "https://api.tryit.co.il/"
```

**Important**: Update this URL with the actual TryIt.co.il API endpoint.

### API Endpoints

#### 1. Get All Tours
```
GET /api/tours
Parameters:
  - page: Int (default: 1)
  - pageSize: Int (default: 20)
  - category: String? (optional)
  - difficulty: String? (optional)

Response: ApiResponse<ApiTourResponse>
```

#### 2. Get Tour by ID
```
GET /api/tours/{id}
Parameters:
  - id: String (tour ID)

Response: ApiResponse<ApiTour>
```

#### 3. Search Tours
```
GET /api/tours/search
Parameters:
  - q: String (search query)
  - page: Int (default: 1)
  - pageSize: Int (default: 20)

Response: ApiResponse<ApiTourResponse>
```

#### 4. Get Sync Status
```
GET /api/sync/status

Response: ApiResponse<SyncStatus>
```

#### 5. Get Updates
```
GET /api/sync/updates
Parameters:
  - since: String (ISO 8601 timestamp)

Response: ApiResponse<ApiTourResponse>
```

## Data Models

### API Models (JSON)
Located in `data/model/ApiModels.kt`:
- `ApiTour`: Tour data from API
- `ApiLocation`: Location/POI data
- `ApiLink`: External links
- `ApiResponse<T>`: Generic API response wrapper

### Domain Models
Located in `domain/model/Tour.kt`:
- `Tour`: Business logic model
- `Location`: POI model
- `Link`: External link model

### Database Entities
Located in `data/local/DatabaseEntities.kt`:
- `TourEntity`: Room entity for tours
- `LocationEntity`: Room entity for locations
- `UserTourEntity`: User's purchased tours

## Synchronization

### Manual Sync
Users can manually trigger sync by pulling to refresh:
```kotlin
viewModel.refreshTours()
```

### Automatic Sync
Background sync runs every 24 hours using WorkManager:
- Checks for updates from TryIt API
- Downloads new/updated tours
- Updates local database
- Notifies user of new content (optional)

### Sync Configuration
Configure sync behavior in `TourSyncWorker.kt`:
```kotlin
private const val SYNC_INTERVAL_HOURS = 24L // Change sync frequency
```

## Authentication

### API Keys (If Required)
If TryIt API requires authentication, add interceptor in `AppModule.kt`:

```kotlin
@Provides
@Singleton
fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer YOUR_API_KEY")
                .build()
            chain.proceed(request)
        }
        .build()
}
```

## Error Handling

### Network Errors
- App works offline using cached data
- Shows error message when sync fails
- Retries failed requests automatically

### API Errors
Handled in Repository layer:
```kotlin
when (response.code()) {
    401 -> // Unauthorized
    404 -> // Not found
    500 -> // Server error
    else -> // Other errors
}
```

## Testing

### API Testing
Use Postman or similar tool to test TryIt API endpoints before integration.

### Local Testing
Use mock data or MockWebServer for testing:
1. Create mock responses in `test/` directory
2. Test Repository with mocked API service
3. Test ViewModel with mocked Repository

## Migration from Firebase

If migrating existing data from Firebase to TryIt API:

1. **Export Firebase Data**
   ```kotlin
   // Export tours from Firestore
   val tours = firestore.collection("tours").get()
   ```

2. **Transform Data**
   ```kotlin
   // Convert Firebase data to TryIt format
   val apiTours = tours.map { it.toApiTour() }
   ```

3. **Upload to TryIt** (if API supports)
   ```kotlin
   // POST tours to TryIt API
   apiService.uploadTours(apiTours)
   ```

## Performance Optimization

### Caching Strategy
- Tours cached for 24 hours
- Images cached using Glide
- API responses cached with OkHttp

### Pagination
Implement pagination for large datasets:
```kotlin
fun loadMoreTours(page: Int) {
    repository.getTours(page = page, pageSize = 20)
}
```

### Background Sync
- Only syncs on WiFi (configurable)
- Only syncs when battery is not low
- Uses exponential backoff for retries

## Monitoring

### Sync Status
Check sync status in app:
```kotlin
val lastSync = syncMetadataDao.getMetadata("last_sync_time")
```

### Error Tracking
Add Firebase Crashlytics or similar:
```kotlin
try {
    syncTours()
} catch (e: Exception) {
    FirebaseCrashlytics.getInstance().recordException(e)
}
```

## Future Enhancements

1. **Real-time Updates**: Use WebSockets for live updates
2. **Conflict Resolution**: Handle conflicts between local and remote data
3. **Selective Sync**: Only sync selected categories
4. **Push Notifications**: Notify users of new tours
5. **Analytics**: Track popular tours and user behavior

## Support

For API documentation and support:
- TryIt.co.il API Documentation: [URL]
- Contact: [Email]
- Issue Tracker: [GitHub Issues]

## License

Integration code is part of Travelotef app under MIT License.
