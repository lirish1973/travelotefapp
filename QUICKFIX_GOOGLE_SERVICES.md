# Quick Fix: Google Services Configuration Error

## 🔴 Error You're Seeing

```
Execution failed for task ':app:processDebugGoogleServices'.
> No matching client found for package name 'com.travelotef.app'
```

## ✅ Quick Solution (2 Minutes)

### Option 1: Use Existing Configuration (Fastest)
The project now includes a working `google-services.json` file with sample credentials:

```bash
# 1. Verify the file exists
ls app/google-services.json

# 2. Run validation
./scripts/validate-google-services.sh

# 3. Build the project
./gradlew clean build
```

✅ **Done!** You can now build and run the app.

### Option 2: Use Your Own Firebase Project
If you want to use your own Firebase configuration:

```bash
# 1. Download from Firebase Console
# Go to: https://console.firebase.google.com
# Project Settings > General > Your apps > Download google-services.json

# 2. Copy to app directory
cp ~/Downloads/google-services.json app/google-services.json

# 3. Validate
./scripts/validate-google-services.sh

# 4. Build
./gradlew clean build
```

## 📋 Checklist

Make sure:
- [ ] File `app/google-services.json` exists
- [ ] Package name is `com.travelotef.app`
- [ ] File is valid JSON
- [ ] No placeholder values (YOUR_API_KEY, etc.)

## 🆘 Still Not Working?

Run the validation script for detailed diagnostics:
```bash
chmod +x scripts/validate-google-services.sh
./scripts/validate-google-services.sh
```

See full troubleshooting guide: [TROUBLESHOOTING_GOOGLE_SERVICES.md](TROUBLESHOOTING_GOOGLE_SERVICES.md)

## 📚 More Information

- **Complete Fix Documentation**: [GOOGLE_SERVICES_CLIENT_FIX.md](GOOGLE_SERVICES_CLIENT_FIX.md)
- **Build Guide**: [BUILD_GUIDE.md](BUILD_GUIDE.md)
- **Firebase Setup**: [app/README-google-services.md](app/README-google-services.md)

## 🔐 Security Note

The included `google-services.json` contains sample/development credentials only. For production, use your own Firebase project configuration. The file is gitignored and won't be committed to version control.
