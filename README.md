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

## 🏗️ ארכיטקטורה

### טכנולוגיות
- **Kotlin** - שפת פיתוח ראשית
- **MVVM Architecture** - ארכיטקטורה נקייה
- **Firebase**
  - Authentication (התחברות)
  - Firestore (מסד נתונים)
  - Storage (אחסון מדיה)
  - Cloud Functions (לוגיקה בצד שרת)
- **Google Maps SDK** - מפות וניווט
- **ExoPlayer** - נגן וידאו
- **Jetpack Compose** - UI מודרני
- **Coroutines** - תכנות אסינכרוני
- **Retrofit** - תקשורת רשת
- **Coil** - טעינת תמונות

### מבנה הפרויקט
```
app/
├── src/
│   ├── main/
│   │   ├── java/com/travelotef/
│   │   │   ├── ui/
│   │   │   │   ├── splash/          # מסך פתיחה
│   │   │   │   ├── auth/            # התחברות והרשמה
│   │   │   │   ├── home/            # מסך בית
│   │   │   │   ├── tours/           # רשימת סיורים
│   │   │   │   ├── tourdetails/     # פרטי סיור
│   │   │   │   ├── mytours/         # הסיורים שלי
│   │   │   │   ├── guide/           # מורה דרך וירטואלי
│   │   │   │   └── profile/         # פרופיל משתמש
│   │   │   ├── data/
│   │   │   │   ├── repository/      # מקור נתונים
│   │   │   │   ├── remote/          # Firebase
│   │   │   │   └── local/           # Room Database
│   │   │   ├── domain/
│   │   │   │   ├── model/           # מודלים
│   │   │   │   └── usecase/         # לוגיקה עסקית
│   │   │   └── di/                  # Dependency Injection
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

### שלב 2 - ליבה (בפיתוח)
- [ ] Tours List
- [ ] Tour Details
- [ ] Purchase Flow
- [ ] My Tours

### שלב 3 - מורה דרך (מתוכנן)
- [ ] Virtual Guide Screen
- [ ] Video Player
- [ ] Interactive Map
- [ ] Photo Gallery
- [ ] Stories View

### שלב 4 - פיצ'רים מתקדמים (עתידי)
- [ ] User Profile
- [ ] Tour History
- [ ] Ratings & Reviews
- [ ] Offline Mode
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
```kotlin
// אפשר Email/Password authentication בקונסול
```

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

## 📦 Dependencies

```kotlin
// Core
implementation("androidx.core:core-ktx:1.12.0")
implementation("androidx.appcompat:appcompat:1.6.1")

// Compose
implementation("androidx.compose.ui:ui:1.5.4")
implementation("androidx.compose.material3:material3:1.1.2")

// Firebase
implementation("com.google.firebase:firebase-auth-ktx:22.3.0")
implementation("com.google.firebase:firebase-firestore-ktx:24.10.0")
implementation("com.google.firebase:firebase-storage-ktx:20.3.0")

// Maps
implementation("com.google.maps.android:maps-compose:4.3.0")

// Video
implementation("androidx.media3:media3-exoplayer:1.2.0")

// Networking
implementation("com.squareup.retrofit2:retrofit:2.9.0")

// Image Loading
implementation("io.coil-kt:coil-compose:2.5.0")

// Navigation
implementation("androidx.navigation:navigation-compose:2.7.6")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
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
Kotlin • MVVM • Firebase • Jetpack Compose • Google Maps • ExoPlayer

### Status
🟢 Active Development - v0.1.0 (Alpha)