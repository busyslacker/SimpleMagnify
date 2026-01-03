# SimpleMagnify

A camera-based magnifier app designed specifically for seniors. Features giant controls, high contrast UI, and zero ads.

## Features

- **Giant Zoom Slider** - Full-width, easy to use
- **One-Button Freeze** - Capture and examine magnified text
- **Flashlight Toggle** - Built-in light for dark areas
- **High Contrast Mode** - Yellow on black for low vision
- **Left/Right Hand Mode** - Configurable button position
- **No Ads** - Ever. Paid app model.

## Project Structure

```
SimpleMagnify/
├── TECHNICAL_SPEC.md      # Full technical specification
├── README.md              # This file
├── iOS/                   # Swift/SwiftUI implementation
│   └── SimpleMagnify/
│       ├── SimpleMagnifyApp.swift
│       ├── ContentView.swift
│       ├── Views/
│       │   ├── CameraView.swift
│       │   ├── CameraPreviewView.swift
│       │   ├── FrozenImageView.swift
│       │   └── SettingsView.swift
│       ├── Components/
│       │   ├── BigButton.swift
│       │   ├── BigSlider.swift
│       │   └── SettingsToggle.swift
│       ├── Models/
│       │   ├── AppSettings.swift
│       │   └── CameraManager.swift
│       └── Utilities/
│           └── Constants.swift
└── Android/               # Kotlin/Jetpack Compose implementation
    └── app/src/main/
        ├── java/com/simplemagnify/
        │   ├── MainActivity.kt
        │   ├── ui/
        │   │   ├── CameraScreen.kt
        │   │   ├── FrozenImageScreen.kt
        │   │   ├── SettingsScreen.kt
        │   │   ├── Navigation.kt
        │   │   └── theme/Theme.kt
        │   ├── components/
        │   │   ├── BigButton.kt
        │   │   ├── BigSlider.kt
        │   │   └── SettingsToggle.kt
        │   ├── models/
        │   │   └── AppSettings.kt
        │   ├── camera/
        │   │   └── CameraManager.kt
        │   └── utils/
        │       └── Constants.kt
        ├── res/
        └── AndroidManifest.xml
```

## iOS Setup

### Requirements
- Xcode 15+
- iOS 15.0+ deployment target
- Physical device for camera testing (simulator won't work)

### Steps

1. **Create new Xcode project**
   - Open Xcode → File → New → Project
   - Select "App" under iOS
   - Product Name: `SimpleMagnify`
   - Interface: SwiftUI
   - Language: Swift

2. **Copy source files**
   - Copy all `.swift` files from `iOS/SimpleMagnify/` into your Xcode project
   - Maintain the folder structure (Views, Components, Models, Utilities)

3. **Configure Info.plist**
   Add camera usage description:
   ```xml
   <key>NSCameraUsageDescription</key>
   <string>SimpleMagnify needs camera access to magnify text and objects for easier reading.</string>
   ```

4. **Set deployment target**
   - Select project in navigator
   - Set iOS Deployment Target to 15.0

5. **Build and run**
   - Connect physical iOS device
   - Select device as build target
   - Press Cmd+R to build and run

### Creating App Icon
1. Design 1024x1024 icon (or use AI tools)
2. Use https://appicon.co to generate all sizes
3. Drag into Assets.xcassets → AppIcon

## Android Setup

### Requirements
- Android Studio Hedgehog (2023.1.1) or newer
- Kotlin 1.9+
- minSdk 26 (Android 8.0)
- Physical device for camera testing

### Steps

1. **Create new Android Studio project**
   - File → New → New Project
   - Select "Empty Activity"
   - Name: `SimpleMagnify`
   - Package name: `com.simplemagnify`
   - Language: Kotlin
   - Minimum SDK: API 26

2. **Copy configuration files**
   - Replace `app/build.gradle.kts` with provided file
   - Replace `build.gradle.kts` (project level) with provided file
   - Replace `settings.gradle.kts` with provided file
   - Copy `gradle/libs.versions.toml` to your gradle folder

3. **Copy source files**
   - Copy all `.kt` files from `Android/app/src/main/java/com/simplemagnify/`
   - Maintain package structure

4. **Copy resources**
   - Copy `AndroidManifest.xml`
   - Copy `res/values/strings.xml`
   - Copy `res/values/themes.xml`

5. **Sync and build**
   - Click "Sync Project with Gradle Files"
   - Connect physical Android device
   - Press Shift+F10 to build and run

### Creating App Icon
1. Right-click `res` folder → New → Image Asset
2. Select your 1024x1024 icon
3. Android Studio generates all required sizes

## Design Guidelines

### Tap Targets
- Minimum 60x60 dp/pt for all interactive elements
- 16dp/pt spacing between elements

### Typography
- Minimum 18pt body text
- 24pt headers
- Medium or Semibold weight (never Light)

### Colors
Standard Mode:
- Background: #FFFFFF
- Text: #1A1A1A
- Primary: #0066CC

High Contrast Mode:
- Background: #000000
- Text: #FFFF00

### Accessibility
- All elements have accessibility labels
- Support VoiceOver (iOS) and TalkBack (Android)
- Works with system font scaling up to 200%

## Testing Checklist

- [ ] Camera preview displays correctly
- [ ] Zoom slider changes magnification (1x to 10x)
- [ ] Pinch-to-zoom works
- [ ] Flashlight toggles on/off
- [ ] Freeze captures current frame
- [ ] Frozen image can be zoomed/panned
- [ ] Settings persist after app restart
- [ ] Test with actual senior users (3-5 people)

## App Store Submission

### iOS (App Store Connect)
1. Archive in Xcode (Product → Archive)
2. Upload to App Store Connect
3. Fill in metadata:
   - App Name: SimpleMagnify - Large Text Reader
   - Subtitle: Big Buttons, No Ads, Easy to Use
   - Category: Utilities
   - Price: $2.99

### Android (Google Play Console)
1. Generate signed APK/AAB
2. Upload to Google Play Console
3. Fill in store listing
4. Set price: $2.99

## License

Copyright (c) 2024. All rights reserved.
