# Troubleshooting: Google Services Configuration

## Error: "No matching client found for package name"

### Full Error Message
```
Execution failed for task ':app:processDebugGoogleServices'.
> No matching client found for package name 'com.travelotef.app' in google-services.json
```

### What This Error Means
This error occurs when the `google-services.json` file doesn't contain a configuration for the package name used by your Android app. The Google Services plugin looks for a client entry with `package_name: "com.travelotef.app"` but can't find it.

### Root Causes
1. **Missing google-services.json file** - The file doesn't exist in the `app/` directory
2. **Wrong package name in Firebase** - Your Firebase project is configured with a different package name
3. **Placeholder values** - You're using the template file instead of a real Firebase configuration
4. **Corrupted file** - The JSON file is malformed or incomplete
5. **Multiple Firebase projects** - You downloaded the config from the wrong Firebase project

## Solution Steps

### Step 1: Verify File Exists
```bash
# Check if the file exists
ls -la app/google-services.json

# If it doesn't exist, you'll see:
# ls: cannot access 'app/google-services.json': No such file or directory
```

**If file doesn't exist:**
- Download it from Firebase Console (see Step 2)

### Step 2: Download from Firebase Console

1. **Go to Firebase Console:**
   - Visit: https://console.firebase.google.com
   - Sign in with your Google account

2. **Select Your Project:**
   - Click on your project (or create a new one)
   - Project name should be something like "Travelotef" or "travelotef-app"

3. **Navigate to Project Settings:**
   - Click the gear icon ⚙️ next to "Project Overview"
   - Select "Project settings"

4. **Find Your Android App:**
   - Scroll down to "Your apps" section
   - Look for an Android app with package name: `com.travelotef.app`

5. **If App Doesn't Exist - Register It:**
   - Click "Add app" → Select Android icon
   - Enter package name: `com.travelotef.app`
   - App nickname (optional): "Travelotef Android"
   - Debug signing certificate SHA-1 (optional for now)
   - Click "Register app"

6. **Download Configuration:**
   - Click "Download google-services.json"
   - Save the file to your downloads folder

7. **Copy to Project:**
   ```bash
   # Linux/Mac
   cp ~/Downloads/google-services.json app/google-services.json
   
   # Windows
   copy %USERPROFILE%\Downloads\google-services.json app\google-services.json
   ```

### Step 3: Verify Package Name

Run the validation script:
```bash
# Make script executable (first time only)
chmod +x scripts/validate-google-services.sh

# Run validation
./scripts/validate-google-services.sh
```

Or manually check:
```bash
# Using grep
grep -A 2 '"package_name"' app/google-services.json

# Should show:
# "package_name": "com.travelotef.app"
```

**Expected output:**
```json
"android_client_info": {
  "package_name": "com.travelotef.app"
}
```

### Step 4: Verify JSON Structure

Check if it's valid JSON:
```bash
# Using Python
python3 -m json.tool app/google-services.json

# Using jq (if installed)
jq '.' app/google-services.json
```

### Step 5: Clean and Rebuild

After fixing the configuration:
```bash
# Clean the project
./gradlew clean

# Build again
./gradlew build
```

## Common Mistakes

### Mistake 1: Using the Template File
❌ **Wrong:** Using `google-services.json.template` directly
✅ **Correct:** Download actual file from Firebase Console

### Mistake 2: Wrong Package Name in Firebase
❌ **Wrong:** Firebase configured with `com.example.app` or `com.travelotef`
✅ **Correct:** Firebase configured with `com.travelotef.app`

### Mistake 3: File in Wrong Location
❌ **Wrong:** `google-services.json` in project root or `src/`
✅ **Correct:** `google-services.json` in `app/` directory

### Mistake 4: Multiple Client Entries
If your `google-services.json` has multiple clients, make sure one has the correct package name:
```json
{
  "client": [
    {
      "android_client_info": {
        "package_name": "com.wrong.package"  // ❌ Wrong
      }
    },
    {
      "android_client_info": {
        "package_name": "com.travelotef.app"  // ✅ Correct
      }
    }
  ]
}
```

## Quick Validation Checklist

- [ ] File exists at `app/google-services.json`
- [ ] File is valid JSON (no syntax errors)
- [ ] Package name is `com.travelotef.app`
- [ ] No placeholder values (YOUR_API_KEY, etc.)
- [ ] File was downloaded from correct Firebase project
- [ ] File is not the `.template` file

## Advanced Troubleshooting

### Issue: Multiple Firebase Projects
If you have multiple Firebase projects:

1. **Identify the correct project:**
   - Check `project_id` in your `google-services.json`
   - Verify it matches your Firebase Console

2. **Download from correct project:**
   - Make sure you're in the right project in Firebase Console
   - Look for the project name in the top-left corner

### Issue: File Permissions
If you get permission errors:
```bash
# Fix file permissions
chmod 644 app/google-services.json

# Verify
ls -la app/google-services.json
```

### Issue: Git Conflicts
The `google-services.json` file should NOT be in Git:
```bash
# Check if it's tracked
git ls-files app/google-services.json

# If it shows output, remove it from Git:
git rm --cached app/google-services.json
git commit -m "Remove google-services.json from version control"

# Verify it's ignored
git check-ignore -v app/google-services.json
```

## Building Without Firebase

If you want to build without Firebase temporarily:

1. **Comment out the plugin** in `app/build.gradle.kts`:
```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // id("com.google.gms.google-services")  // Commented out
    // ... other plugins
}
```

2. **Remove Firebase dependencies:**
```kotlin
dependencies {
    // Comment out Firebase dependencies
    // implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    // implementation("com.google.firebase:firebase-analytics-ktx")
    // ... other firebase deps
}
```

**Note:** This is only for testing build issues. The app won't work without Firebase.

## Getting Help

If you still have issues:

1. **Check the validation script output:**
   ```bash
   ./scripts/validate-google-services.sh
   ```

2. **Check Gradle output:**
   ```bash
   ./gradlew build --info
   ```

3. **Verify build.gradle.kts:**
   - Package name in `defaultConfig.applicationId`
   - Should be: `"com.travelotef.app"`

4. **Create an issue:**
   - Include error message
   - Include validation script output
   - Include first 10 lines of google-services.json (with sensitive data removed)

## Related Documentation

- [Firebase Console](https://console.firebase.google.com)
- [Firebase Android Setup](https://firebase.google.com/docs/android/setup)
- [Google Services Plugin Documentation](https://developers.google.com/android/guides/google-services-plugin)
- [BUILD_GUIDE.md](BUILD_GUIDE.md) - Complete build instructions
- [GOOGLE_SERVICES_FIX.md](GOOGLE_SERVICES_FIX.md) - Previous related fixes

## Summary

The key points to remember:

1. ✅ `google-services.json` must exist in the `app/` directory
2. ✅ It must contain a client with package name `com.travelotef.app`
3. ✅ Download it from Firebase Console, don't use the template
4. ✅ Use the validation script to verify: `./scripts/validate-google-services.sh`
5. ✅ Clean and rebuild after fixing: `./gradlew clean build`

If you follow these steps, the "No matching client found" error should be resolved.
