# Build & Deployment Guide

## Prerequisites

### Required Software
- **Android Studio**: Ladybug | 2024.2.1 or newer
- **JDK**: 17
- **Android SDK**: 
  - Minimum SDK: 24 (Android 7.0)
  - Target SDK: 34 (Android 14)
  - Compile SDK: 34
- **Gradle**: 8.9 (via wrapper)

### Required Accounts
- Google Firebase account
- TryIt.co.il API access (contact for credentials)

## Setup Instructions

### 1. Clone the Repository
```bash
git clone https://github.com/lirish1973/travelotefapp.git
cd travelotefapp
```

### 2. Configure Firebase

#### A. Create Firebase Project
1. Go to [Firebase Console](https://console.firebase.google.com)
2. Click "Add Project"
3. Name it "Travelotef" or similar
4. Enable Google Analytics (optional)

#### B. Add Android App
1. In Firebase Console, click "Add app" → Android icon
2. Package name: `com.example.travelotefapp`
3. Download `google-services.json`
4. Place it in `app/` directory

#### C. Enable Firebase Services
1. **Authentication**:
   - Go to Authentication → Sign-in method
   - Enable Email/Password
   - Enable Google Sign-In:
     * Click on "Google" in the sign-in providers list
     * Toggle the "Enable" switch
     * Select a support email
     * Click "Save"
   - **Important**: After enabling Google Sign-In, download the updated `google-services.json` file and replace the existing one in the `app/` directory
   
2. **Firestore Database**:
   - Go to Firestore Database
   - Click "Create database"
   - Start in production mode
   - Choose region (e.g., us-central)

3. **Storage**:
   - Go to Storage
   - Click "Get started"
   - Use default rules

### 3. Configure TryIt.co.il API

Edit `app/src/main/java/com/travelotef/app/di/AppModule.kt`:

```kotlin
private const val BASE_URL = "https://api.tryit.co.il/" // Update with actual URL
```

If API requires authentication, add:
```kotlin
@Provides
@Singleton
fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer YOUR_API_KEY")
                .addHeader("X-API-Key", "YOUR_API_KEY")
                .build()
            chain.proceed(request)
        }
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
}
```

### 4. Build the Project

#### Using Android Studio
1. Open Android Studio
2. File → Open → Select `travelotefapp` directory
3. Wait for Gradle sync to complete
4. Build → Make Project (Ctrl+F9 / Cmd+F9)

#### Using Command Line
```bash
# Linux/Mac
./gradlew clean build

# Windows
gradlew.bat clean build
```

### 5. Run the Application

#### On Emulator
1. Tools → AVD Manager
2. Create a new Virtual Device (if needed)
3. Select a device definition (e.g., Pixel 4)
4. Select system image (API 24+ recommended)
5. Click Run (▶️) or Shift+F10

#### On Physical Device
1. Enable Developer Options on your Android device:
   - Settings → About Phone
   - Tap "Build Number" 7 times
2. Enable USB Debugging:
   - Settings → Developer Options → USB Debugging
3. Connect device via USB
4. Click Run (▶️) and select your device

## Troubleshooting

### Gradle Sync Issues
If Gradle sync fails:
```bash
# Clean and rebuild
./gradlew clean
./gradlew build --refresh-dependencies

# Delete cached files
rm -rf .gradle/
rm -rf ~/.gradle/caches/
```

### Build Errors

#### "google-services.json not found"
- Make sure you downloaded `google-services.json` from Firebase
- Place it in `app/` directory (same level as `build.gradle.kts`)

#### "SDK location not found"
Create `local.properties` file in project root:
```properties
sdk.dir=/path/to/Android/Sdk
```
- Windows: `C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk`
- Mac: `/Users/YourName/Library/Android/sdk`
- Linux: `/home/YourName/Android/Sdk`

#### "Cannot resolve symbol" errors
1. File → Invalidate Caches / Restart
2. Build → Clean Project
3. Build → Rebuild Project

#### Network/API Issues
If you see "dl.google.com" errors, you may need to:
1. Configure proxy settings in gradle.properties
2. Use a VPN if region-blocked
3. Check firewall settings

### Runtime Issues

#### "App crashes on startup"
Check LogCat for errors:
- View → Tool Windows → Logcat
- Filter by "Error" or "AndroidRuntime"

Common causes:
- Missing Firebase configuration
- Incorrect API endpoint
- Missing permissions in AndroidManifest.xml

#### "Sync not working"
1. Check internet connection
2. Verify TryIt API endpoint is correct
3. Check API authentication (if required)
4. View WorkManager logs:
```kotlin
// In HomeFragment or a debug screen
val workManager = WorkManager.getInstance(requireContext())
workManager.getWorkInfosForUniqueWork("tour_sync_work")
```

## Testing

### Run Unit Tests
```bash
./gradlew test
```

### Run Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

### Manual Testing Checklist
- [ ] App launches successfully
- [ ] Splash screen displays
- [ ] Home screen loads
- [ ] Tours load from database (even offline)
- [ ] Pull-to-refresh triggers sync
- [ ] Background sync runs (check after 24 hours)
- [ ] Search functionality works
- [ ] App works offline

## Deployment

### Generate Debug APK
```bash
./gradlew assembleDebug
```
Output: `app/build/outputs/apk/debug/app-debug.apk`

### Generate Release APK

1. Create keystore (if you don't have one):
```bash
keytool -genkey -v -keystore travelotef-release-key.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias travelotef
```

2. Configure signing in `app/build.gradle.kts`:
```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("path/to/travelotef-release-key.jks")
            storePassword = "your_store_password"
            keyAlias = "travelotef"
            keyPassword = "your_key_password"
        }
    }
    
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            // ... other release config
        }
    }
}
```

3. Build release APK:
```bash
./gradlew assembleRelease
```
Output: `app/build/outputs/apk/release/app-release.apk`

### Upload to Google Play Store

1. Create signed AAB (Android App Bundle):
```bash
./gradlew bundleRelease
```
Output: `app/build/outputs/bundle/release/app-release.aab`

2. Go to [Google Play Console](https://play.google.com/console)
3. Create a new app
4. Complete store listing details
5. Upload AAB file
6. Submit for review

## Monitoring & Maintenance

### Check Sync Status
Add debug UI to show sync status:
```kotlin
val lastSync = syncMetadataDao.getMetadata("last_sync_time")
Log.d("TravelotefSync", "Last sync: ${lastSync?.value}")
```

### Monitor Background Work
View WorkManager status:
```bash
adb shell dumpsys jobscheduler | grep travelotef
```

### Performance Monitoring
Consider adding Firebase Performance Monitoring:
```kotlin
// In app/build.gradle.kts
implementation("com.google.firebase:firebase-perf-ktx")

// In code
val trace = FirebasePerformance.getInstance().newTrace("sync_tours")
trace.start()
// ... sync operation
trace.stop()
```

## Support

### Common Resources
- **Project Issues**: https://github.com/lirish1973/travelotefapp/issues
- **Firebase Docs**: https://firebase.google.com/docs
- **Android Docs**: https://developer.android.com
- **Hilt Guide**: https://developer.android.com/training/dependency-injection/hilt-android
- **Room Guide**: https://developer.android.com/training/data-storage/room

### Getting Help
1. Check LogCat for error messages
2. Search existing GitHub issues
3. Create a new issue with:
   - Error message/stack trace
   - Steps to reproduce
   - Device info (Android version, device model)
   - Screenshots if applicable

## License
This project is licensed under the MIT License. See LICENSE file for details.
