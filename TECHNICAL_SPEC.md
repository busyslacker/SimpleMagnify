# SimpleMagnify - Technical Specification

## Overview

SimpleMagnify is a camera-based magnifier app designed specifically for seniors. It prioritizes large controls, high contrast, and extreme simplicity over feature richness.

**Platforms:** iOS (Swift/SwiftUI) + Android (Kotlin/Jetpack Compose)
**Target OS:** iOS 15+ / Android API 26+ (Android 8.0+)
**Architecture:** MVVM

---

## Core Features

| Feature | Priority | Complexity |
|---------|----------|------------|
| Camera preview with zoom | P0 | Medium |
| Giant zoom slider | P0 | Low |
| Freeze/capture image | P0 | Low |
| Flashlight toggle | P0 | Low |
| Pinch-to-zoom | P1 | Low |
| Settings persistence | P1 | Low |
| High contrast UI mode | P2 | Low |
| Left/right hand mode | P2 | Low |

---

## Screen Flow

```
┌─────────────────┐
│                 │
│  Camera Screen  │◄──── Main/Launch Screen
│                 │
└────────┬────────┘
         │
    ┌────┴────┐
    │         │
    ▼         ▼
┌────────┐  ┌──────────┐
│ Frozen │  │ Settings │
│ Image  │  │  Screen  │
└────────┘  └──────────┘
```

---

## Screen Specifications

### Screen 1: Camera View (Main)

**Layout:**
```
┌─────────────────────────────────────┐
│ SimpleMagnify                   ⚙️  │ ← Header (60pt height)
├─────────────────────────────────────┤
│                                     │
│                                     │
│         CAMERA PREVIEW              │ ← Takes remaining space
│         (with zoom applied)         │
│                                     │
│                                     │
├─────────────────────────────────────┤
│  1x ━━━━━━━━━●━━━━━━━━━━━━━━━ 10x  │ ← Zoom slider (80pt height)
├─────────────────────────────────────┤
│                                     │
│  ┌───────────┐    ┌───────────┐    │ ← Button row (100pt height)
│  │  💡 LIGHT │    │ 📷 FREEZE │    │
│  └───────────┘    └───────────┘    │
│                                     │
└─────────────────────────────────────┘
```

**Interactions:**
- Slider drag → Adjust zoom (1x to 10x)
- Pinch on preview → Adjust zoom
- Tap "Light" → Toggle flashlight
- Tap "Freeze" → Capture and navigate to frozen view
- Tap ⚙️ → Navigate to settings

**State:**
- `zoomLevel: Float` (1.0 to 10.0)
- `isFlashlightOn: Boolean`
- `isCameraReady: Boolean`

---

### Screen 2: Frozen Image View

**Layout:**
```
┌─────────────────────────────────────┐
│ ← Back                              │ ← Header with back button
├─────────────────────────────────────┤
│                                     │
│                                     │
│         FROZEN IMAGE                │ ← Full screen, pinch to zoom
│         (captured frame)            │
│                                     │
│                                     │
│                                     │
├─────────────────────────────────────┤
│                                     │
│  ┌─────────────────────────────┐   │ ← Single button (100pt height)
│  │      🔙 BACK TO CAMERA      │   │
│  └─────────────────────────────┘   │
│                                     │
└─────────────────────────────────────┘
```

**Interactions:**
- Pinch → Zoom in/out on frozen image
- Pan → Move around zoomed image
- Tap "Back to Camera" or back arrow → Return to camera

**State:**
- `capturedImage: Image`
- `imageZoom: Float`
- `imageOffset: CGPoint/Offset`

---

### Screen 3: Settings

**Layout:**
```
┌─────────────────────────────────────┐
│ ← Back              Settings        │
├─────────────────────────────────────┤
│                                     │
│  Default Zoom                       │
│  ┌─────────────────────────────┐   │
│  │ 1x ━━━━●━━━━━━━━━━━━━━ 10x │   │
│  └─────────────────────────────┘   │
│                                     │
│  ─────────────────────────────────  │
│                                     │
│  ┌─────────────────────────────┐   │
│  │ Light on at start      [○] │   │ ← Toggle
│  └─────────────────────────────┘   │
│                                     │
│  ─────────────────────────────────  │
│                                     │
│  ┌─────────────────────────────┐   │
│  │ High contrast mode     [●] │   │ ← Toggle
│  └─────────────────────────────┘   │
│                                     │
│  ─────────────────────────────────  │
│                                     │
│  Button Position                    │
│  ┌─────────┐      ┌─────────┐      │
│  │  LEFT   │      │  RIGHT  │      │ ← Segmented control
│  └─────────┘      └─────────┘      │
│                                     │
└─────────────────────────────────────┘
```

**Settings to Persist:**
| Setting | Type | Default | Key |
|---------|------|---------|-----|
| defaultZoom | Float | 2.0 | `default_zoom` |
| lightOnStart | Bool | false | `light_on_start` |
| highContrastMode | Bool | false | `high_contrast` |
| buttonPosition | Enum | .right | `button_position` |

**Storage:**
- iOS: `UserDefaults`
- Android: `SharedPreferences` / `DataStore`

---

## Design Tokens

### Colors

```swift
// Standard Mode
static let background = Color.white           // #FFFFFF
static let textPrimary = Color(hex: "1A1A1A") // Near black
static let buttonPrimary = Color(hex: "0066CC") // Accessible blue
static let buttonDanger = Color(hex: "CC0000")  // Red
static let buttonBackground = Color(hex: "F5F5F5") // Light gray

// High Contrast Mode
static let hcBackground = Color.black
static let hcText = Color.yellow
static let hcButtonPrimary = Color.yellow
static let hcButtonBackground = Color(hex: "333333")
```

### Typography

```swift
// iOS
static let headerFont = Font.system(size: 24, weight: .semibold)
static let buttonFont = Font.system(size: 20, weight: .medium)
static let labelFont = Font.system(size: 18, weight: .regular)
static let sliderLabelFont = Font.system(size: 16, weight: .medium)
```

```kotlin
// Android
val headerTextSize = 24.sp
val buttonTextSize = 20.sp
val labelTextSize = 18.sp
val sliderLabelTextSize = 16.sp
val fontWeightMedium = FontWeight.Medium
```

### Dimensions

```
Minimum tap target: 60x60 dp/pt
Button height: 80 dp/pt
Button spacing: 16 dp/pt
Screen padding: 20 dp/pt
Slider height: 60 dp/pt (thumb: 40x40)
Header height: 60 dp/pt
Corner radius: 12 dp/pt
```

---

## Technical Implementation

### iOS - Camera Setup (AVFoundation)

```swift
// Required imports
import AVFoundation
import SwiftUI

// Camera permission check
AVCaptureDevice.requestAccess(for: .video) { granted in
    // Handle permission
}

// Camera session setup
let session = AVCaptureSession()
session.sessionPreset = .photo

// Get back camera
guard let device = AVCaptureDevice.default(.builtInWideAngleCamera,
                                            for: .video,
                                            position: .back) else { return }

// Zoom implementation
try? device.lockForConfiguration()
device.videoZoomFactor = clamp(zoomLevel, 1.0, device.activeFormat.videoMaxZoomFactor)
device.unlockForConfiguration()

// Flashlight
try? device.lockForConfiguration()
device.torchMode = isOn ? .on : .off
device.unlockForConfiguration()
```

### Android - Camera Setup (CameraX)

```kotlin
// Required dependencies
implementation("androidx.camera:camera-camera2:1.3.0")
implementation("androidx.camera:camera-lifecycle:1.3.0")
implementation("androidx.camera:camera-view:1.3.0")

// Camera permission
Manifest.permission.CAMERA

// CameraX setup
val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
cameraProviderFuture.addListener({
    val cameraProvider = cameraProviderFuture.get()
    val preview = Preview.Builder().build()
    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    cameraProvider.bindToLifecycle(
        lifecycleOwner,
        cameraSelector,
        preview
    )
}, ContextCompat.getMainExecutor(context))

// Zoom
camera.cameraControl.setZoomRatio(zoomLevel)

// Flashlight
camera.cameraControl.enableTorch(isOn)
```

---

## File Structure

### iOS Project

```
SimpleMagnify/
├── SimpleMagnifyApp.swift          # App entry point
├── ContentView.swift               # Root navigation
├── Views/
│   ├── CameraView.swift            # Main camera screen
│   ├── FrozenImageView.swift       # Frozen image screen
│   └── SettingsView.swift          # Settings screen
├── Components/
│   ├── BigButton.swift             # Reusable large button
│   ├── BigSlider.swift             # Custom large slider
│   └── SettingsToggle.swift        # Large toggle row
├── Models/
│   ├── AppSettings.swift           # Settings model
│   └── CameraManager.swift         # Camera handling
├── Utilities/
│   ├── Constants.swift             # Design tokens
│   └── Extensions.swift            # Helper extensions
├── Resources/
│   └── Assets.xcassets/            # App icons, colors
└── Info.plist                      # Permissions
```

### Android Project

```
app/src/main/
├── java/com/simplemagnify/
│   ├── MainActivity.kt             # App entry point
│   ├── ui/
│   │   ├── CameraScreen.kt         # Main camera composable
│   │   ├── FrozenImageScreen.kt    # Frozen image composable
│   │   ├── SettingsScreen.kt       # Settings composable
│   │   └── Navigation.kt           # Nav graph
│   ├── components/
│   │   ├── BigButton.kt            # Reusable large button
│   │   ├── BigSlider.kt            # Custom large slider
│   │   └── SettingsToggle.kt       # Large toggle row
│   ├── models/
│   │   └── AppSettings.kt          # Settings data class
│   ├── camera/
│   │   └── CameraManager.kt        # CameraX handling
│   └── utils/
│       ├── Constants.kt            # Design tokens
│       └── Extensions.kt           # Helper extensions
├── res/
│   ├── values/
│   │   ├── strings.xml
│   │   ├── colors.xml
│   │   └── themes.xml
│   └── mipmap-*/                   # App icons
└── AndroidManifest.xml             # Permissions
```

---

## Permissions

### iOS (Info.plist)

```xml
<key>NSCameraUsageDescription</key>
<string>SimpleMagnify needs camera access to magnify text and objects for easier reading.</string>
```

### Android (AndroidManifest.xml)

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera" android:required="true" />
<uses-feature android:name="android.hardware.camera.autofocus" android:required="false" />
```

---

## Build & Release Checklist

### iOS
- [ ] Set deployment target to iOS 15.0
- [ ] Add camera usage description to Info.plist
- [ ] Configure app icons (all sizes)
- [ ] Set bundle identifier: `com.yourname.simplemagnify`
- [ ] Configure signing with your Apple Developer account
- [ ] Archive and upload to App Store Connect

### Android
- [ ] Set minSdk to 26, targetSdk to 34
- [ ] Add camera permission to AndroidManifest.xml
- [ ] Configure app icons (all densities)
- [ ] Set applicationId: `com.yourname.simplemagnify`
- [ ] Generate signed APK/AAB
- [ ] Upload to Google Play Console

---

## Testing Checklist

### Functional Tests
- [ ] Camera preview displays correctly
- [ ] Zoom slider changes magnification (1x to 10x)
- [ ] Pinch-to-zoom works on camera preview
- [ ] Flashlight toggles on/off
- [ ] Freeze captures current frame
- [ ] Frozen image can be zoomed/panned
- [ ] Back navigation works from all screens
- [ ] Settings persist after app restart

### Accessibility Tests
- [ ] VoiceOver/TalkBack reads all elements
- [ ] All buttons have accessibility labels
- [ ] Minimum 60x60pt/dp tap targets
- [ ] Works with system font scaling (up to 200%)
- [ ] High contrast mode is visually distinct

### Device Tests
- [ ] iPhone SE (smallest modern iPhone)
- [ ] iPhone 15 Pro Max (largest iPhone)
- [ ] Android phone with small screen (5.5")
- [ ] Android phone with large screen (6.7"+)
- [ ] Test with actual senior users (3-5 people)

---

## Performance Targets

| Metric | Target |
|--------|--------|
| App launch to camera ready | < 2 seconds |
| Zoom response latency | < 100ms |
| Freeze capture time | < 200ms |
| App size | < 15 MB |
| Memory usage | < 150 MB |
| Battery (30 min use) | < 10% drain |

---

## Future Enhancements (v1.1+)

| Feature | Priority | Notes |
|---------|----------|-------|
| Image saving to Photos | Medium | Let users save frozen images |
| Color filters | Low | Invert, grayscale for low vision |
| Widget | Low | Quick launch from home screen |
| Apple Watch companion | Low | Quick flashlight toggle |
| Voice commands | Low | "Zoom in", "Freeze", "Light on" |

---

## Dependencies

### iOS
- SwiftUI (built-in)
- AVFoundation (built-in)
- No third-party dependencies required

### Android
```kotlin
// build.gradle.kts (app level)
dependencies {
    // CameraX
    val cameraxVersion = "1.3.0"
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")

    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2024.01.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.navigation:navigation-compose:2.7.6")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // DataStore for settings
    implementation("androidx.datastore:datastore-preferences:1.0.0")
}
```
