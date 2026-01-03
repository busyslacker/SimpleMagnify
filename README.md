# SimpleMagnify

A camera-based magnifier app designed specifically for seniors. Features giant controls, high contrast UI, OCR text extraction, text-to-speech, and zero ads.

## Features

- **High-Resolution Photo Capture** - Capture sharp images, then zoom up to 15x
- **OCR Text Extraction** - Automatically reads text from captured images
- **Text-to-Speech** - Read extracted text aloud at a comfortable pace
- **Giant Zoom Slider** - Full-width, easy to use (1-3x optical preview)
- **One-Button Freeze** - Capture and examine magnified text
- **Flashlight Toggle** - Built-in light for dark areas
- **Tap-to-Focus** - Touch screen to focus on specific area
- **High Contrast Mode** - Yellow on black for low vision
- **Left/Right Hand Mode** - Configurable button position
- **Copy to Clipboard** - Copy extracted text for use elsewhere
- **No Ads** - Ever. Paid app model.

## Current Status

| Platform | Camera | Freeze | OCR | TTS | Settings |
|----------|--------|--------|-----|-----|----------|
| iOS      | ✅     | ✅     | ✅  | ✅  | ✅       |
| Android  | ✅     | ✅     | ✅  | ✅  | ✅       |

**Note:** Both platforms are functional but may have minor bugs being tracked for the next release.

## Project Structure

```
SimpleMagnify/
├── TECHNICAL_SPEC.md      # Full technical specification
├── README.md              # This file
├── iOS/                   # Swift/SwiftUI implementation
│   └── SimpleMagnify/
│       ├── SimpleMagnify.xcodeproj
│       └── SimpleMagnify/
│           ├── SimpleMagnifyApp.swift
│           ├── ContentView.swift
│           ├── Views/
│           │   ├── CameraView.swift
│           │   ├── CameraPreviewView.swift
│           │   ├── FrozenImageView.swift
│           │   ├── SettingsView.swift
│           │   └── TextResultView.swift
│           ├── Components/
│           │   ├── BigButton.swift
│           │   ├── BigSlider.swift
│           │   └── SettingsToggle.swift
│           ├── Models/
│           │   ├── AppSettings.swift
│           │   ├── CameraManager.swift
│           │   ├── TextRecognitionManager.swift
│           │   └── SpeechManager.swift
│           └── Utilities/
│               └── Constants.swift
└── Android/               # Kotlin/Jetpack Compose implementation
    ├── gradle.properties
    ├── build.gradle.kts
    ├── settings.gradle.kts
    ├── gradle/
    │   └── libs.versions.toml
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
        │   ├── ocr/
        │   │   └── TextRecognitionManager.kt
        │   ├── speech/
        │   │   └── SpeechManager.kt
        │   └── utils/
        │       └── Constants.kt
        ├── res/
        └── AndroidManifest.xml
```

## Tech Stack

### iOS
- **Language:** Swift 5.9+
- **UI:** SwiftUI
- **Camera:** AVFoundation (AVCapturePhotoOutput)
- **OCR:** Vision framework (VNRecognizeTextRequest)
- **TTS:** AVFoundation (AVSpeechSynthesizer)
- **Min iOS:** 15.0

### Android
- **Language:** Kotlin 2.0
- **UI:** Jetpack Compose + Material 3
- **Camera:** CameraX (ImageCapture)
- **OCR:** ML Kit Text Recognition
- **TTS:** Android TextToSpeech
- **Min SDK:** 26 (Android 8.0)
- **Target SDK:** 35

## iOS Setup

### Requirements
- Xcode 15+
- iOS 15.0+ deployment target
- Physical device for camera testing (simulator won't work)

### Steps

1. **Open project**
   - Open `iOS/SimpleMagnify/SimpleMagnify.xcodeproj` in Xcode

2. **Configure signing**
   - Select project in navigator
   - Go to Signing & Capabilities
   - Select your development team

3. **Build and run**
   - Connect physical iOS device
   - Select device as build target
   - Press Cmd+R to build and run

## Android Setup

### Requirements
- Android Studio Ladybug (2024.2.1) or newer
- Kotlin 2.0+
- Java 17+
- Physical device for camera testing

### Steps

1. **Open project**
   - Open `Android/` folder in Android Studio
   - Wait for Gradle sync to complete

2. **Build and run**
   - Connect physical Android device with USB debugging enabled
   - Select device in toolbar
   - Press Shift+F10 to build and run

## User Flow

1. **Camera Screen** - Live preview with zoom slider (1-3x)
2. **Tap Freeze** - Captures high-resolution photo
3. **Frozen Image Screen** - OCR runs automatically
   - If text found → switches to Text view
   - Large scrollable text (28pt)
   - Tap **Read** for text-to-speech
   - Tap **Image** to view/zoom the photo (up to 15x)
   - Copy button to copy text to clipboard
4. **Back to Camera** - Returns to live preview

## Design Guidelines

### Tap Targets
- Minimum 60x60 dp/pt for all interactive elements
- 16dp/pt spacing between elements

### Typography
- Minimum 18pt body text
- 24pt headers
- 28pt for extracted text display
- Medium or Semibold weight (never Light)

### Colors
Standard Mode:
- Background: #FFFFFF
- Text: #1A1A1A
- Primary: #0066CC

High Contrast Mode:
- Background: #1A1A2E
- Text: #FFFFFF
- Primary: #4A90D9

## Testing Checklist

- [x] Camera preview displays correctly
- [x] Zoom slider changes magnification (1x to 3x)
- [x] Pinch-to-zoom works
- [x] Flashlight toggles on/off
- [x] Freeze captures high-resolution photo
- [x] Frozen image can be zoomed/panned (up to 15x)
- [x] OCR extracts text from images
- [x] Text displays in large scrollable view
- [x] Text-to-speech reads text aloud
- [x] Play/Pause/Stop controls work
- [x] Copy to clipboard works
- [x] Settings persist after app restart
- [ ] Test with actual senior users (3-5 people)
- [ ] Fix reported bugs (in progress)

## App Store Submission

### iOS (App Store Connect)
1. Archive in Xcode (Product → Archive)
2. Upload to App Store Connect
3. Fill in metadata:
   - App Name: SimpleMagnify - Large Text Reader
   - Subtitle: OCR, Text-to-Speech, Big Buttons
   - Category: Utilities
   - Price: $2.99

### Android (Google Play Console)
1. Generate signed AAB (Build → Generate Signed Bundle)
2. Upload to Google Play Console
3. Fill in store listing
4. Set price: $2.99

## License

Copyright (c) 2025. All rights reserved.
