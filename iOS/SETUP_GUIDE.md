# SimpleMagnify iOS Setup Guide

## Prerequisites

- **Xcode 15.0+** (download from Mac App Store)
- **macOS Sonoma 14.0+** (or Ventura 13.0+ with Xcode 15)
- **Physical iPhone** for testing (camera won't work in simulator)
- **Apple Developer Account** (free for device testing, $99/year for App Store)

## Step-by-Step Setup

### Step 1: Create New Xcode Project

1. Open Xcode
2. Click **"Create New Project"** (or File → New → Project)
3. Select **iOS** tab at the top
4. Choose **"App"** and click **Next**
5. Fill in the options:
   - **Product Name:** `SimpleMagnify`
   - **Team:** Select your Apple ID or team
   - **Organization Identifier:** `com.yourname` (e.g., `com.johndoe`)
   - **Interface:** `SwiftUI`
   - **Language:** `Swift`
   - **Storage:** `None`
   - Leave checkboxes unchecked (no tests needed for now)
6. Click **Next**
7. Choose location to save (anywhere except the SimpleMagnify folder we created)
8. Click **Create**

### Step 2: Configure Project Settings

1. Click on the **project name** in the left sidebar (blue icon)
2. Select the **SimpleMagnify target** (under TARGETS)
3. In the **General** tab:
   - **Display Name:** `SimpleMagnify`
   - **Bundle Identifier:** `com.yourname.simplemagnify`
   - **Version:** `1.0.0`
   - **Build:** `1`
   - **Minimum Deployments:** iOS `15.0`
   - **Device Orientation:** Check only `Portrait`
4. In the **Signing & Capabilities** tab:
   - Select your Team
   - Ensure "Automatically manage signing" is checked

### Step 3: Delete Default Files

In the Project Navigator (left sidebar), delete these default files:
- `ContentView.swift` (we have our own)

Right-click → Delete → Move to Trash

### Step 4: Add Source Files

1. In Finder, navigate to:
   ```
   /Users/jungoo/Projects/GitHub/SimpleMagnify/iOS/SimpleMagnify/
   ```

2. In Xcode, right-click on the **SimpleMagnify** folder (yellow folder icon) in the navigator

3. Select **"Add Files to SimpleMagnify..."**

4. Navigate to the source folder above

5. Select ALL items:
   - `SimpleMagnifyApp.swift`
   - `ContentView.swift`
   - `Info.plist`
   - `Views` folder
   - `Components` folder
   - `Models` folder
   - `Utilities` folder

6. Ensure these options are set:
   - ☑️ **Copy items if needed**
   - ☑️ **Create groups** (not folder references)
   - ☑️ **Add to targets:** SimpleMagnify

7. Click **Add**

### Step 5: Configure Info.plist

The Info.plist should be automatically added, but verify:

1. Click on **Info.plist** in the navigator
2. Confirm it contains `NSCameraUsageDescription` with the camera permission message
3. If Xcode created a separate Info.plist, you may need to:
   - Delete the duplicate
   - In Build Settings, search for "Info.plist File"
   - Set the path to: `SimpleMagnify/Info.plist`

### Step 6: Fix Any Import Errors

If you see red error indicators:

1. **Clean the build folder:** Product → Clean Build Folder (⇧⌘K)
2. **Close and reopen Xcode**
3. **Check that all files are added to the target:**
   - Select each .swift file
   - In the right panel, under "Target Membership"
   - Ensure "SimpleMagnify" is checked

### Step 7: Connect Your iPhone

1. Connect iPhone via USB cable
2. On iPhone: Trust the computer if prompted
3. In Xcode toolbar, click the device dropdown (next to the Play button)
4. Select your iPhone

**First time setup:**
- Go to iPhone → Settings → General → VPN & Device Management
- Trust your developer certificate

### Step 8: Build and Run

1. Press **⌘R** (or click the Play button)
2. Wait for the build to complete
3. On first launch, the app will request camera permission
4. Tap **Allow** to enable camera access

## Troubleshooting

### "Signing requires a development team"
- Select your Apple ID team in Signing & Capabilities
- If you don't have one, sign in via Xcode → Settings → Accounts → Add Apple ID

### "Could not find module..."
- Clean build folder (⇧⌘K)
- Restart Xcode

### Camera shows black screen
- Ensure you're testing on a **physical device**, not simulator
- Check camera permission in Settings → SimpleMagnify → Camera

### "App installation failed"
- Check iPhone is unlocked
- Trust the developer certificate on the device
- Try different USB cable/port

### Files not compiling
- Verify each .swift file has "SimpleMagnify" checked in Target Membership
- Check file is added to the project (should have proper indentation in navigator)

## Project Structure After Setup

Your Xcode navigator should look like this:

```
SimpleMagnify
├── SimpleMagnifyApp.swift
├── ContentView.swift
├── Views
│   ├── CameraView.swift
│   ├── CameraPreviewView.swift
│   ├── FrozenImageView.swift
│   └── SettingsView.swift
├── Components
│   ├── BigButton.swift
│   ├── BigSlider.swift
│   └── SettingsToggle.swift
├── Models
│   ├── AppSettings.swift
│   └── CameraManager.swift
├── Utilities
│   └── Constants.swift
├── Info.plist
└── Assets.xcassets
```

## Adding App Icon (Optional but Recommended)

1. Find or create a 1024x1024 PNG icon
2. Go to https://appicon.co
3. Upload your image
4. Download the generated icons
5. In Xcode, click **Assets.xcassets**
6. Click **AppIcon**
7. Drag the appropriate sizes into each slot

## Next Steps

Once the app runs successfully:

1. **Test all features:**
   - Camera preview with zoom
   - Freeze functionality
   - Flashlight toggle
   - Settings persistence

2. **Test with seniors:**
   - Watch them use it without instructions
   - Note any confusion points
   - Iterate based on feedback

3. **Prepare for App Store:**
   - Take screenshots on multiple devices
   - Write app description
   - Set up App Store Connect listing
