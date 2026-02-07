# Quick Reference Card

## 🚀 Getting Started (5 Minutes)

### 1. Open Project
```bash
# Clone if not already cloned
git clone https://github.com/lirish1973/travelotefapp.git
cd travelotefapp

# Open in Android Studio
# File → Open → Select travelotefapp folder
```

### 2. Add Firebase Configuration
```bash
# Download google-services.json from Firebase Console
# Place it in: app/google-services.json
```

### 3. Configure TryIt API
```kotlin
// File: app/src/main/java/com/travelotef/app/di/AppModule.kt
// Line 26: Update this URL
private const val BASE_URL = "https://api.tryit.co.il/"
```

### 4. Build & Run
```bash
# In Android Studio, click Run ▶️ (Shift+F10)
# Or from terminal:
./gradlew installDebug
```

---

## 📁 Project Structure

```
app/src/main/java/com/travelotef/app/
├── TravelotefApp.kt           # Application class (Hilt entry)
├── MainActivity.kt             # Main activity
│
├── ui/
│   ├── splash/                 # Splash screen
│   └── home/                   # Home screen
│       ├── HomeFragment.kt     # UI
│       └── HomeViewModel.kt    # ViewModel (LiveData)
│
├── domain/
│   └── model/                  # Business models
│       └── Tour.kt             # Tour, Location, Link
│
├── data/
│   ├── api/                    # REST API
│   │   └── TryItApiService.kt  # Retrofit interface
│   ├── model/                  # Data models
│   │   ├── ApiModels.kt        # API responses
│   │   └── Mappers.kt          # Conversions
│   ├── local/                  # Database
│   │   ├── TravelotefDatabase.kt
│   │   ├── TravelotefDao.kt    # Data access
│   │   └── DatabaseEntities.kt # Room entities
│   ├── repository/             # Data source
│   │   └── TourRepository.kt   # Offline-first logic
│   └── sync/
│       └── TourSyncWorker.kt   # Background sync
│
├── di/
│   └── AppModule.kt            # Hilt dependencies
│
└── utils/
    └── Resource.kt             # Result wrapper
```

---

## 🔧 Key Files to Edit

### For UI Development
- `HomeFragment.kt` - Add UI components
- `HomeViewModel.kt` - Already has LiveData
- `layout/fragment_home.xml` - Add views

### For API Integration
- `AppModule.kt` - Update BASE_URL
- `TryItApiService.kt` - Verify endpoints
- `ApiModels.kt` - Verify data structure

### For Business Logic
- `TourRepository.kt` - Data fetching logic
- `Tour.kt` - Business models
- `Mappers.kt` - Data conversions

---

## 💡 Common Tasks

### Load Tours in Fragment
```kotlin
// Already implemented in HomeFragment.kt
viewModel.tours.observe(viewLifecycleOwner) { resource ->
    when (resource) {
        is Resource.Loading -> showLoading()
        is Resource.Success -> showTours(resource.data)
        is Resource.Error -> showError(resource.message)
    }
}
```

### Trigger Sync
```kotlin
// Manual sync
viewModel.syncTours()

// Or pull-to-refresh
swipeRefreshLayout.setOnRefreshListener {
    viewModel.refreshTours()
}
```

### Search Tours
```kotlin
searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
    override fun onQueryTextChange(query: String): Boolean {
        viewModel.searchTours(query)
        return true
    }
})
```

### Navigate to Tour Details
```kotlin
// In RecyclerView adapter
holder.itemView.setOnClickListener {
    findNavController().navigate(
        R.id.action_home_to_tourDetails,
        bundleOf("tourId" to tour.id)
    )
}
```

---

## 🔍 Debugging

### Check Database
```bash
# In Android Studio
View → Tool Windows → App Inspection → Database Inspector
```

### View Logs
```bash
# Filter by tag
adb logcat | grep "TravelotefSync"

# Or in Android Studio
View → Tool Windows → Logcat
```

### Test API Connection
```kotlin
// Add in HomeFragment onCreate
lifecycleScope.launch {
    val result = viewModel.testConnection()
    Log.d("API", "Connection: $result")
}
```

### Check Sync Status
```kotlin
// View last sync time
val lastSync = syncMetadataDao.getMetadata("last_sync_time")
Log.d("Sync", "Last sync: ${lastSync?.value}")
```

---

## 📚 Quick Links

### Documentation
- [UPGRADE_SUMMARY.md](UPGRADE_SUMMARY.md) - What was done
- [BUILD_GUIDE.md](BUILD_GUIDE.md) - Build instructions
- [TRYIT_API_INTEGRATION.md](TRYIT_API_INTEGRATION.md) - API guide
- [SECURITY.md](SECURITY.md) - Security guidelines
- [README.md](README.md) - Project overview

### External Resources
- [Android Architecture](https://developer.android.com/topic/architecture)
- [Hilt Guide](https://developer.android.com/training/dependency-injection/hilt-android)
- [Room Guide](https://developer.android.com/training/data-storage/room)
- [Retrofit](https://square.github.io/retrofit/)

---

## 🐛 Troubleshooting

### "google-services.json not found"
```bash
# Download from Firebase Console
# Place in app/google-services.json
```

### "SDK location not found"
```bash
# Create local.properties
echo "sdk.dir=/path/to/Android/Sdk" > local.properties
```

### "Cannot resolve symbol"
```bash
# In Android Studio
File → Invalidate Caches / Restart
Build → Clean Project
Build → Rebuild Project
```

### "Sync failed"
```bash
# Check internet connection
# Verify API URL in AppModule.kt
# Check WorkManager logs
adb shell dumpsys jobscheduler | grep travelotef
```

---

## ✅ Quick Checklist

Before committing:
- [ ] Code builds successfully
- [ ] No lint errors
- [ ] Tests pass (if written)
- [ ] Documentation updated
- [ ] No hardcoded secrets

Before deploying:
- [ ] Firebase configured
- [ ] API endpoint set
- [ ] ProGuard rules checked
- [ ] Security checklist reviewed
- [ ] APK signed

---

## 🚑 Emergency Contacts

### If Build Breaks
1. Check [BUILD_GUIDE.md](BUILD_GUIDE.md#troubleshooting)
2. Clean and rebuild
3. Check Gradle sync

### If App Crashes
1. Check LogCat for stack trace
2. Review recent code changes
3. Test on different device

### If Sync Fails
1. Check API endpoint
2. Verify internet connection
3. Check WorkManager constraints

---

## 📊 Architecture at a Glance

```
┌─────────────────┐
│  UI (Fragment)  │ ← User interacts
└────────┬────────┘
         │
┌────────▼────────┐
│   ViewModel     │ ← LiveData
└────────┬────────┘
         │
┌────────▼────────┐
│   Repository    │ ← Offline-first
└───┬─────────┬───┘
    │         │
┌───▼───┐ ┌──▼────┐
│ Room  │ │  API  │
│  DB   │ │(TryIt)│
└───────┘ └───────┘
```

---

## 💻 Development Workflow

1. **Pull latest code**
   ```bash
   git pull origin main
   ```

2. **Create feature branch**
   ```bash
   git checkout -b feature/tour-details
   ```

3. **Make changes**
   - Edit code
   - Test locally
   - Write tests

4. **Commit & push**
   ```bash
   git add .
   git commit -m "Add tour details screen"
   git push origin feature/tour-details
   ```

5. **Create PR**
   - Go to GitHub
   - Create Pull Request
   - Request review

---

## 🎯 Performance Tips

### Database
- Use indexes on foreign keys ✅ (Already done)
- Batch inserts for bulk data ✅ (Already done)
- Use Flow for reactive queries ✅ (Already done)

### Network
- Cache images with Glide ✅ (Already configured)
- Use pagination for large lists (TODO)
- Implement retry logic ✅ (Already done)

### UI
- Use RecyclerView (not ListView)
- Implement ViewHolder pattern
- Use DiffUtil for list updates

---

## 🔐 Security Reminders

❌ **NEVER commit**:
- API keys
- Passwords
- google-services.json (already in .gitignore)
- keystore files

✅ **ALWAYS**:
- Use HTTPS
- Validate user input
- Encrypt sensitive data
- Follow security checklist

---

*Quick Reference v1.0 - February 2026*
