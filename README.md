# 🇮🇱 Travelotef - סיורים בעוטף

<div dir="rtl">

## אודות הפרויקט

**Travelotef** היא אפליקציית Android חדשנית המאפשרת לכל אדם לרכוש סיור מודרך בעוטף עזה ולחוות חוויית טיול אישית ומרגשת.

### 🎯 המטרה
להפוך את הסמארטפון שלך למורה דרך וירטואלי פרטי, עם סרטונים, תמונות, סיפורים וקישורים בכל נקודת עניין.

## ✨ תכונות עיקריות

- 📱 **רכישת סיורים** - מגוון סיורים זמינים לרכישה
- 🎥 **וידאו מודרך** - מדריכים מקצועיים בכל נקודה
- 🗺️ **ניווט חכם** - מפה אינטראקטיבית עם נקודות עניין
- 📸 **גלריית תמונות** - תיעוד חזותי עשיר
- 📖 **סיפורים** - תוכן היסטורי ואישי בכל תחנה
- 🔗 **קישורים** - מידע נוסף ומקורות
- 👤 **פרופיל אישי** - ניהול הסיורים שלי
- 🔄 **סנכרון אוטומטי** - עדכון סיורים מ-TryIt.co.il
- 📴 **מצב לא מקוון** - עבודה ללא חיבור לאינטרנט

## 🏗️ ארכיטקטורה

### טכנולוגיות
- **Kotlin** - שפת פיתוח ראשית
- **MVVM Architecture** - ארכיטקטורה נקייה עם ViewModel, Repository
- **Hilt** - Dependency Injection מודרני
- **Firebase**
  - Authentication (התחברות)
  - Firestore (מסד נתונים)
  - Storage (אחסון מדיה)
  - Cloud Functions (לוגיקה בצד שרת)
- **Room Database** - מסד נתונים מקומי לעבודה לא מקוונת
- **Retrofit** - תקשורת רשת עם TryIt.co.il API
- **WorkManager** - סנכרון רקע אוטומטי
- **Coroutines & Flow** - תכנות אסינכרוני ריאקטיבי
- **Glide** - טעינת תמונות
- **Google Maps SDK** - מפות וניווט (מתוכנן)
- **ExoPlayer** - נגן וידאו (מתוכנן)

### מבנה הפרויקט
```
app/
├── src/
│   ├── main/
│   │   ├── java/com/travelotef/app/
│   │   │   ├── ui/
│   │   │   │   ├── splash/          # מסך פתיחה
│   │   │   │   ├── auth/            # התחברות והרשמה (מתוכנן)
│   │   │   │   ├── home/            # מסך בית + ViewModel
│   │   │   │   ├── tours/           # רשימת סיורים (מתוכנן)
│   │   │   │   ├── tourdetails/     # פרטי סיור (מתוכנן)
│   │   │   │   ├── mytours/         # הסיורים שלי (מתוכנן)
│   │   │   │   ├── guide/           # מורה דרך וירטואלי (מתוכנן)
│   │   │   │   └── profile/         # פרופיל משתמש (מתוכנן)
│   │   │   ├── data/
│   │   │   │   ├── api/             # TryIt.co.il API Service
│   │   │   │   ├── model/           # API Models & Mappers
│   │   │   │   ├── repository/      # TourRepository
│   │   │   │   ├── local/           # Room Database & DAOs
│   │   │   │   └── sync/            # WorkManager Sync
│   │   │   ├── domain/
│   │   │   │   └── model/           # Domain Models (Tour, Location)
│   │   │   ├── di/                  # Hilt Dependency Injection
│   │   │   └── utils/               # Utilities (Resource)
│   │   ├── res/
│   │   └── AndroidManifest.xml
│   └── test/
└── build.gradle.kts
```

## 📱 מסכים

### שלב 1 - יסודות ✅
- [x] Splash Screen
- [x] Login/Register
- [x] Home Screen

### שלב 2 - ליבה (בפיתוח) ✅
- [x] MVVM Architecture with ViewModel & Repository
- [x] Room Database for offline storage
- [x] Retrofit API integration with TryIt.co.il
- [x] Background sync with WorkManager
- [ ] Tours List UI
- [ ] Tour Details UI
- [ ] Purchase Flow
- [ ] My Tours UI

### שלב 3 - מורה דרך (מתוכנן)
- [ ] Virtual Guide Screen
- [ ] Video Player
- [ ] Interactive Map
- [ ] Photo Gallery
- [ ] Stories View

### שלב 4 - פיצ'רים מתקדמים (עתידי)
- [x] Offline Mode (Room Database)
- [ ] User Profile
- [ ] Tour History
- [ ] Ratings & Reviews
- [ ] Social Sharing

## 🚀 התחלת עבודה

### דרישות מוקדמות
- Android Studio Hedgehog | 2023.1.1 ומעלה
- JDK 17
- Android SDK 24 ומעלה (Target: 34)
- חשבון Firebase

### התקנה

1. שכפל את הפרויקט:
```bash
git clone https://github.com/lirish1973/travelotefapp.git
cd travelotefapp
```

2. פתח את הפרויקט ב-Android Studio

3. הגדר Firebase:
   - צור פרויקט חדש ב-[Firebase Console](https://console.firebase.google.com)
   - הורד את קובץ `google-services.json`
   - העתק אותו לתיקייה `app/`

4. Sync Gradle ובנה את הפרויקט

5. הרץ על אמולטור או מכשיר פיזי

## 🔥 הגדרת Firebase

### 1. Authentication

#### הגדרת Email/Password Authentication
1. פתח את [Firebase Console](https://console.firebase.google.com)
2. בחר את הפרויקט שלך
3. עבור אל **Authentication** > **Sign-in method**
4. אפשר את **Email/Password** provider

#### הגדרת Google Sign-In
1. עבור אל **Authentication** > **Sign-in method**
2. אפשר את **Google** provider
3. הגדר את Support Email
4. לחץ על **Save**

**חשוב:** לאחר הגדרת Google Sign-In ב-Firebase Console, הורד מחדש את קובץ `google-services.json` והעתק אותו לתיקיית `app/`. הקובץ המעודכן יכלול את OAuth 2.0 client ID הנדרש ל-Google Sign-In.

**הערה:** אם השדה `default_web_client_id` בקובץ `strings.xml` מכיל `YOUR_WEB_CLIENT_ID_HERE`, עליך:
1. להוריד מחדש את `google-services.json` לאחר הגדרת Google Sign-In ב-Firebase Console
2. לבנות מחדש את הפרויקט - הקובץ `default_web_client_id` ייווצר אוטומטית מתוך `google-services.json`

### 2. Firestore Database
```javascript
// מבנה הנתונים:
tours/
  ├── {tourId}/
  │   ├── title: string
  │   ├── description: string
  │   ├── price: number
  │   ├── duration: string
  │   ├── coverImage: string
  │   ├── locations: array
  │   └── createdAt: timestamp

users/
  ├── {userId}/
  │   ├── email: string
  │   ├── displayName: string
  │   ├── purchasedTours: array
  │   └── createdAt: timestamp

locations/
  ├── {locationId}/
  │   ├── name: string
  │   ├── coordinates: geopoint
  │   ├── videoUrl: string
  │   ├── images: array
  │   ├── story: string
  │   └── links: array
```

### 3. Storage
```
storage/
├── tours/
│   ├── covers/
│   ├── videos/
│   └── images/
```

## 🔄 סנכרון עם TryIt.co.il

האפליקציה כוללת אינטגרציה מלאה עם TryIt.co.il:

### תכונות סנכרון
- **Offline-First**: העבודה מתבצעת ממסד נתונים מקומי (Room)
- **סנכרון אוטומטי**: עדכון רקע כל 24 שעות באמצעות WorkManager
- **Pull to Refresh**: רענון ידני על ידי המשתמש
- **חיפוש חכם**: חיפוש מקומי עם סנכרון מרחוק
- **זיהוי שינויים**: רק נתונים שהשתנו נשלפים מהשרת

### ארכיטקטורת Offline-First
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

### הגדרת API
כדי להשתמש באינטגרציה, עדכן את כתובת ה-API ב-`AppModule.kt`:
```kotlin
private const val BASE_URL = "https://api.tryit.co.il/"
```

למידע מפורט, ראה [TRYIT_API_INTEGRATION.md](TRYIT_API_INTEGRATION.md)

## 📦 Dependencies

```kotlin
// Core Android
implementation("androidx.core:core-ktx:1.15.0")
implementation("androidx.appcompat:appcompat:1.7.0")
implementation("com.google.android.material:material:1.12.0")
implementation("androidx.constraintlayout:constraintlayout:2.2.0")

// Lifecycle
implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

// Navigation
implementation("androidx.navigation:navigation-fragment-ktx:2.8.5")
implementation("androidx.navigation:navigation-ui-ktx:2.8.5")

// Firebase
implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
implementation("com.google.firebase:firebase-analytics-ktx")
implementation("com.google.firebase:firebase-auth-ktx")
implementation("com.google.firebase:firebase-firestore-ktx")
implementation("com.google.firebase:firebase-storage-ktx")

// Hilt for Dependency Injection
implementation("com.google.dagger:hilt-android:2.51.1")
kapt("com.google.dagger:hilt-compiler:2.51.1")

// Room Database
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
ksp("androidx.room:room-compiler:2.6.1")

// Retrofit for API calls
implementation("com.squareup.retrofit2:retrofit:2.11.0")
implementation("com.squareup.retrofit2:converter-gson:2.11.0")
implementation("com.squareup.okhttp3:okhttp:4.12.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.9.0")

// Gson for JSON parsing
implementation("com.google.code.gson:gson:2.11.0")

// WorkManager for background sync
implementation("androidx.work:work-runtime-ktx:2.10.0")

// Image Loading - Glide
implementation("com.github.bumptech.glide:glide:4.16.0")
ksp("com.github.bumptech.glide:compiler:4.16.0")

// SwipeRefreshLayout
implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
```

## 🤝 תרומה לפרויקט

נשמח לקבל תרומות! אנא:
1. Fork את הפרויקט
2. צור branch חדש (`git checkout -b feature/AmazingFeature`)
3. Commit השינויים (`git commit -m 'Add some AmazingFeature'`)
4. Push ל-branch (`git push origin feature/AmazingFeature`)
5. פתח Pull Request

## 📝 רישיון

פרויקט זה הוא קוד פתוח תחת רישיון MIT.

## 📧 יצירת קשר

לשאלות ובעיות: [פתח Issue](https://github.com/lirish1973/travelotefapp/issues)

## 🙏 תודות

תודה מיוחדת לכל המדריכים והמתנדבים שתרמו לתוכן האפליקציה.

---

**פותח עם ❤️ לזכר ולעתיד יישובי העוטף**

</div>

---

## 🌍 English

**Travelotef** is an innovative Android application that allows users to purchase guided tours in the Gaza Envelope region and experience a personal, moving journey with a virtual tour guide through videos, photos, stories and links at every point of interest.

### Tech Stack
Kotlin • MVVM • Hilt • Room • Retrofit • Firebase • WorkManager • Coroutines • Flow

### Status
🟢 Active Development - v0.2.0 (Alpha)

### Recent Updates (v0.2.0)
- ✅ Upgraded to MVVM architecture with Repository pattern
- ✅ Added Room Database for offline-first approach
- ✅ Integrated Retrofit for TryIt.co.il API synchronization
- ✅ Implemented WorkManager for background sync
- ✅ Added Hilt for dependency injection
- ✅ Updated to latest stable dependencies (2024-2026)