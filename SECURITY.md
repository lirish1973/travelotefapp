# Security Considerations

## Overview
This document outlines security best practices and considerations for the Travelotef application.

## API Security

### 1. API Key Management

**NEVER** commit API keys to version control. Use one of these approaches:

#### Option A: Environment Variables
```kotlin
// In local.properties (not tracked by git)
tryit.api.key=your_api_key_here

// In app/build.gradle.kts
android {
    defaultConfig {
        buildConfigField("String", "TRYIT_API_KEY", "\"${project.findProperty("tryit.api.key")}\"")
    }
}

// In AppModule.kt
.addHeader("Authorization", "Bearer ${BuildConfig.TRYIT_API_KEY}")
```

#### Option B: Firebase Remote Config
```kotlin
// Fetch API key from Firebase Remote Config
val remoteConfig = Firebase.remoteConfig
remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
    if (task.isSuccessful) {
        val apiKey = remoteConfig.getString("tryit_api_key")
        // Use apiKey
    }
}
```

### 2. Network Security

#### Certificate Pinning
To prevent man-in-the-middle attacks:

```kotlin
val certificatePinner = CertificatePinner.Builder()
    .add("api.tryit.co.il", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
    .build()

val client = OkHttpClient.Builder()
    .certificatePinner(certificatePinner)
    .build()
```

#### Network Security Configuration
Create `res/xml/network_security_config.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="false">
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </base-config>
    <domain-config cleartextTrafficPermitted="false">
        <domain includeSubdomains="true">api.tryit.co.il</domain>
        <pin-set>
            <pin digest="SHA-256">AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=</pin>
        </pin-set>
    </domain-config>
</network-security-config>
```

Reference in AndroidManifest.xml:
```xml
<application
    android:networkSecurityConfig="@xml/network_security_config">
```

## Data Security

### 1. Database Encryption

Encrypt Room database using SQLCipher:

```kotlin
// In app/build.gradle.kts
implementation("net.zetetic:android-database-sqlcipher:4.5.4")
implementation("androidx.sqlite:sqlite-ktx:2.4.0")

// In TravelotefDatabase.kt
val passphrase = SQLiteDatabase.getBytes("your-secure-passphrase".toCharArray())
val factory = SupportFactory(passphrase)

Room.databaseBuilder(context, TravelotefDatabase::class.java, DATABASE_NAME)
    .openHelperFactory(factory)
    .build()
```

### 2. Shared Preferences Encryption

Use EncryptedSharedPreferences for sensitive data:

```kotlin
val masterKey = MasterKey.Builder(context)
    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
    .build()

val sharedPreferences = EncryptedSharedPreferences.create(
    context,
    "secret_shared_prefs",
    masterKey,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
)
```

### 3. Sensitive Data in Memory

Avoid keeping sensitive data in memory longer than necessary:

```kotlin
// Use CharArray instead of String for passwords
fun authenticate(password: CharArray) {
    try {
        // Use password
        // ...
    } finally {
        // Clear password from memory
        password.fill('0')
    }
}
```

## Firebase Security

### 1. Firestore Security Rules

Update Firestore security rules:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can only read/write their own data
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Tours are read-only for all authenticated users
    match /tours/{tourId} {
      allow read: if request.auth != null;
      allow write: if false; // Only admins via console
    }
    
    // User tours
    match /user_tours/{userTourId} {
      allow read, write: if request.auth != null && 
        request.auth.uid == resource.data.userId;
    }
  }
}
```

### 2. Storage Security Rules

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    // Public read for tour media
    match /tours/{allPaths=**} {
      allow read: if true;
      allow write: if false;
    }
    
    // User-specific uploads
    match /users/{userId}/{allPaths=**} {
      allow read, write: if request.auth != null && 
        request.auth.uid == userId;
    }
  }
}
```

### 3. Authentication Best Practices

```kotlin
// Enable email verification
val user = FirebaseAuth.getInstance().currentUser
if (user != null && !user.isEmailVerified) {
    user.sendEmailVerification()
}

// Use strong password requirements
fun isPasswordStrong(password: String): Boolean {
    return password.length >= 8 &&
           password.any { it.isUpperCase() } &&
           password.any { it.isLowerCase() } &&
           password.any { it.isDigit() } &&
           password.any { !it.isLetterOrDigit() }
}
```

## ProGuard/R8 Rules

Add to `proguard-rules.pro`:

```proguard
# Keep model classes for Gson/Retrofit
-keep class com.travelotef.app.data.model.** { *; }
-keep class com.travelotef.app.domain.model.** { *; }

# Keep Retrofit interfaces
-keep interface com.travelotef.app.data.api.** { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Hilt
-keepclasseswithmembers class * {
    @dagger.* <methods>;
}
-keep class dagger.* { *; }
-keep class javax.inject.* { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponent
```

## Input Validation

### 1. User Input Sanitization

```kotlin
fun sanitizeInput(input: String): String {
    return input
        .trim()
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#x27;")
        .replace("/", "&#x2F;")
}
```

### 2. SQL Injection Prevention

Room already prevents SQL injection, but if using raw queries:

```kotlin
// WRONG - vulnerable to SQL injection
tourDao.rawQuery("SELECT * FROM tours WHERE id = $userInput")

// CORRECT - use parameterized queries
@Query("SELECT * FROM tours WHERE id = :tourId")
suspend fun getTourById(tourId: String): TourEntity?
```

### 3. API Response Validation

```kotlin
suspend fun getTourById(tourId: String): Resource<Tour> {
    return try {
        val response = apiService.getTourById(tourId)
        
        // Validate response
        if (!response.isSuccessful) {
            return Resource.Error("HTTP ${response.code()}")
        }
        
        val body = response.body()
        if (body?.success != true) {
            return Resource.Error(body?.error?.message ?: "Unknown error")
        }
        
        val tour = body.data
        if (tour == null) {
            return Resource.Error("Invalid response data")
        }
        
        // Validate tour data
        if (tour.id.isBlank() || tour.title.isBlank()) {
            return Resource.Error("Invalid tour data")
        }
        
        Resource.Success(tour.toDomain())
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Unknown error")
    }
}
```

## Permissions

### Request Only Necessary Permissions

In `AndroidManifest.xml`:
```xml
<!-- Only request permissions you actually use -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

<!-- Location permissions (only if needed for maps) -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

<!-- Storage (only if needed) -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" 
    android:maxSdkVersion="32" />
```

### Runtime Permission Handling

```kotlin
// Check and request permissions
if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
    != PackageManager.PERMISSION_GRANTED) {
    
    ActivityCompat.requestPermissions(
        activity,
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
        LOCATION_PERMISSION_REQUEST_CODE
    )
}
```

## Logging

### 1. Remove Sensitive Information from Logs

```kotlin
// WRONG - logs sensitive data
Log.d("Auth", "Password: $password")
Log.d("API", "API Key: $apiKey")

// CORRECT - log without sensitive data
Log.d("Auth", "Authentication attempt")
Log.d("API", "API call successful")

// Use BuildConfig to disable logs in release
if (BuildConfig.DEBUG) {
    Log.d("Debug", "Debug information")
}
```

### 2. Use Timber with Custom Tree

```kotlin
// In app/build.gradle.kts
implementation("com.jakewharton.timber:timber:5.0.1")

// In TravelotefApp
if (BuildConfig.DEBUG) {
    Timber.plant(Timber.DebugTree())
} else {
    Timber.plant(ReleaseTree())
}

class ReleaseTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // Only log errors to crash reporting
        if (priority == Log.ERROR || priority == Log.WARN) {
            // Send to Firebase Crashlytics
            FirebaseCrashlytics.getInstance().recordException(
                t ?: Exception(message)
            )
        }
    }
}
```

## WebView Security (If Used)

If you add WebViews later:

```kotlin
webView.settings.apply {
    javaScriptEnabled = false // Only enable if absolutely necessary
    allowFileAccess = false
    allowContentAccess = false
    setSupportMultipleWindows(false)
    setGeolocationEnabled(false)
}

// Only load trusted URLs
webView.webViewClient = object : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val url = request?.url?.toString() ?: return true
        return !url.startsWith("https://tryit.co.il/")
    }
}
```

## Code Obfuscation

Enable in `app/build.gradle.kts`:

```kotlin
buildTypes {
    release {
        isMinifyEnabled = true
        isShrinkResources = true
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
}
```

## Security Checklist

Before releasing:
- [ ] All API keys removed from source code
- [ ] ProGuard/R8 enabled for release builds
- [ ] Certificate pinning implemented
- [ ] Firebase security rules configured
- [ ] Database encryption enabled (if storing sensitive data)
- [ ] Network security configuration added
- [ ] Sensitive logs removed
- [ ] Input validation implemented
- [ ] HTTPS only for all network calls
- [ ] Permissions justified and minimized
- [ ] Security scan completed (use Google Play Console)

## Vulnerability Scanning

### Use Android Studio's App Inspector
1. Build → Analyze APK
2. Check for security issues

### Use OWASP Dependency-Check
```bash
./gradlew dependencyCheckAnalyze
```

### Firebase Security Rules Testing
```bash
firebase emulators:start --only firestore
# Run security rules tests
```

## Reporting Security Issues

If you discover a security vulnerability:
1. **DO NOT** open a public GitHub issue
2. Email: [security@travelotef.app] (create dedicated email)
3. Include:
   - Description of vulnerability
   - Steps to reproduce
   - Impact assessment
   - Suggested fix (if any)

## Resources

- [OWASP Mobile Security](https://owasp.org/www-project-mobile-security/)
- [Android Security Best Practices](https://developer.android.com/topic/security/best-practices)
- [Firebase Security Rules](https://firebase.google.com/docs/rules)
- [OWASP Mobile Top 10](https://owasp.org/www-project-mobile-top-10/)

## License
Security guidelines are part of the Travelotef project under MIT License.
