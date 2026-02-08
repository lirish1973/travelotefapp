#!/bin/bash

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "=========================================="
echo "Google Services Configuration Validator"
echo "=========================================="
echo ""

# Check if google-services.json exists
if [ ! -f "app/google-services.json" ]; then
    echo -e "${RED}ERROR: app/google-services.json not found!${NC}"
    echo ""
    echo "To fix this:"
    echo "1. Go to Firebase Console: https://console.firebase.google.com"
    echo "2. Select your project"
    echo "3. Go to Project Settings > General"
    echo "4. Under 'Your apps', find your Android app"
    echo "5. Download google-services.json"
    echo "6. Copy it to app/google-services.json"
    echo ""
    echo "A template is available at: app/google-services.json.template"
    exit 1
fi

echo -e "${GREEN}✓ google-services.json found${NC}"

# Check if it's a valid JSON file
if ! jq empty app/google-services.json 2>/dev/null; then
    # If jq is not installed, try python
    if ! python3 -m json.tool app/google-services.json > /dev/null 2>&1; then
        echo -e "${RED}ERROR: google-services.json is not a valid JSON file${NC}"
        exit 1
    fi
fi

echo -e "${GREEN}✓ Valid JSON format${NC}"

# Check for required package name
EXPECTED_PACKAGE="com.travelotef.app"

# Try with jq first
if command -v jq &> /dev/null; then
    PACKAGE_NAME=$(jq -r '.client[0].client_info.android_client_info.package_name' app/google-services.json 2>/dev/null)
else
    # Fallback to grep/sed
    PACKAGE_NAME=$(grep -o '"package_name"[[:space:]]*:[[:space:]]*"[^"]*"' app/google-services.json | head -1 | sed 's/.*"\([^"]*\)".*/\1/')
fi

if [ -z "$PACKAGE_NAME" ]; then
    echo -e "${RED}ERROR: Could not find package_name in google-services.json${NC}"
    echo "Please ensure your google-services.json has the correct structure."
    exit 1
fi

if [ "$PACKAGE_NAME" != "$EXPECTED_PACKAGE" ]; then
    echo -e "${RED}ERROR: Package name mismatch!${NC}"
    echo "Expected: $EXPECTED_PACKAGE"
    echo "Found:    $PACKAGE_NAME"
    echo ""
    echo "To fix this:"
    echo "1. Go to Firebase Console"
    echo "2. Make sure your Android app is registered with package name: $EXPECTED_PACKAGE"
    echo "3. Download the correct google-services.json file"
    exit 1
fi

echo -e "${GREEN}✓ Package name matches: $EXPECTED_PACKAGE${NC}"

# Check for placeholder values
if grep -q "YOUR_PROJECT_ID\|YOUR_API_KEY\|YOUR_MOBILE_SDK_APP_ID" app/google-services.json; then
    echo -e "${YELLOW}WARNING: Placeholder values detected!${NC}"
    echo "Your google-services.json still contains placeholder values."
    echo "Please download the actual file from Firebase Console."
    exit 1
fi

echo -e "${GREEN}✓ No placeholder values detected${NC}"

# Check for oauth_client (required for Google Sign-In)
if command -v jq &> /dev/null; then
    OAUTH_CLIENTS=$(jq '.client[0].oauth_client | length' app/google-services.json 2>/dev/null)
    if [ "$OAUTH_CLIENTS" = "0" ] || [ "$OAUTH_CLIENTS" = "null" ]; then
        echo -e "${YELLOW}WARNING: No OAuth clients configured${NC}"
        echo "If you're using Google Sign-In, you need to:"
        echo "1. Enable Google Sign-In in Firebase Console"
        echo "2. Add your SHA-1 certificate fingerprint"
        echo "3. Download the updated google-services.json"
    else
        echo -e "${GREEN}✓ OAuth clients configured ($OAUTH_CLIENTS found)${NC}"
    fi
fi

echo ""
echo -e "${GREEN}=========================================="
echo "Configuration validation PASSED!"
echo "==========================================${NC}"
echo ""
echo "Your google-services.json is properly configured."
echo "You can now build the app."
