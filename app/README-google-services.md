# Google Services Configuration Template

## Overview
This template provides guidance for setting up your Firebase `google-services.json` file.

## Setup Instructions

1. **Go to Firebase Console**: https://console.firebase.google.com
2. **Select your project** (or create a new one)
3. **Go to Project Settings** → General tab
4. **Under "Your apps"**, find your Android app or add a new one
5. **Click "Download google-services.json"**
6. **Copy the downloaded file** to this directory as `google-services.json`
7. The file will be **automatically ignored** by Git (see `.gitignore`)

## Important Notes

- **Never commit** `google-services.json` to Git
- The file contains sensitive Firebase configuration
- Each developer/environment can have different configurations
- The `.gitignore` will prevent accidental commits

## Package Names

Ensure your Firebase app is configured with the correct package name:
- For production: `com.travelotef.app`
- For development: Use the same package name in Firebase Console

## File Structure

Your downloaded `google-services.json` should have this structure:

```json
{
  "project_info": {
    "project_number": "YOUR_PROJECT_NUMBER",
    "project_id": "YOUR_PROJECT_ID",
    "storage_bucket": "YOUR_PROJECT_ID.firebasestorage.app"
  },
  "client": [
    {
      "client_info": {
        "mobilesdk_app_id": "YOUR_MOBILE_SDK_APP_ID",
        "android_client_info": {
          "package_name": "com.travelotef.app"
        }
      },
      "oauth_client": [],
      "api_key": [
        {
          "current_key": "YOUR_API_KEY"
        }
      ],
      "services": {
        "appinvite_service": {
          "other_platform_oauth_client": []
        }
      }
    }
  ],
  "configuration_version": "1"
}
```

## Troubleshooting

### "google-services.json not found" error
- Download the file from Firebase Console
- Place it in the `app/` directory (same location as this README)
- Ensure the filename is exactly `google-services.json`

### "Package name mismatch" error
- Verify the package name in Firebase Console matches `com.travelotef.app`
- Re-download the file after updating the package name

### "No matching client found" error
- See [TROUBLESHOOTING_GOOGLE_SERVICES.md](../TROUBLESHOOTING_GOOGLE_SERVICES.md) for detailed solutions
- Run validation script: `./scripts/validate-google-services.sh`

### Need help?
See the main project README.md or BUILD_GUIDE.md for detailed setup instructions.
