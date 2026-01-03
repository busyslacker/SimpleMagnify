import SwiftUI

enum Constants {
    // MARK: - Dimensions
    enum Dimensions {
        static let minTapTarget: CGFloat = 60
        static let buttonHeight: CGFloat = 80
        static let buttonSpacing: CGFloat = 16
        static let screenPadding: CGFloat = 20
        static let sliderHeight: CGFloat = 60
        static let sliderThumbSize: CGFloat = 40
        static let headerHeight: CGFloat = 60
        static let cornerRadius: CGFloat = 12
    }

    // MARK: - Zoom
    enum Zoom {
        static let min: CGFloat = 1.0
        static let max: CGFloat = 3.0  // Preview zoom limited to optical range
        static let defaultValue: CGFloat = 1.0
        static let frozenImageMax: CGFloat = 15.0  // High-res image can zoom much more
    }

    // MARK: - Colors (Standard Mode)
    enum Colors {
        static let background = Color.white
        static let textPrimary = Color(hex: "1A1A1A")
        static let buttonPrimary = Color(hex: "0066CC")
        static let buttonDanger = Color(hex: "CC0000")
        static let buttonBackground = Color(hex: "F0F0F0")
        static let sliderTrack = Color(hex: "E0E0E0")
        static let sliderFill = Color(hex: "0066CC")

        // High Contrast Mode
        static let hcBackground = Color.black
        static let hcText = Color.yellow
        static let hcButtonPrimary = Color.yellow
        static let hcButtonBackground = Color(hex: "333333")
    }

    // MARK: - Fonts
    enum Fonts {
        static let header = Font.system(size: 24, weight: .semibold)
        static let button = Font.system(size: 20, weight: .medium)
        static let label = Font.system(size: 18, weight: .regular)
        static let sliderLabel = Font.system(size: 16, weight: .medium)
    }

    // MARK: - Strings
    enum Strings {
        static let appName = "SimpleMagnify"
        static let light = "LIGHT"
        static let freeze = "FREEZE"
        static let backToCamera = "BACK TO CAMERA"
        static let settings = "Settings"
        static let defaultZoom = "Default Zoom"
        static let lightOnStart = "Light on at start"
        static let highContrastMode = "High contrast mode"
        static let buttonPosition = "Button Position"
        static let left = "LEFT"
        static let right = "RIGHT"
    }

    // MARK: - UserDefaults Keys
    enum StorageKeys {
        static let defaultZoom = "default_zoom"
        static let lightOnStart = "light_on_start"
        static let highContrastMode = "high_contrast"
        static let buttonPosition = "button_position"
    }
}

// MARK: - Color Extension for Hex
extension Color {
    init(hex: String) {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.count {
        case 3: // RGB (12-bit)
            (a, r, g, b) = (255, (int >> 8) * 17, (int >> 4 & 0xF) * 17, (int & 0xF) * 17)
        case 6: // RGB (24-bit)
            (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        case 8: // ARGB (32-bit)
            (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
        default:
            (a, r, g, b) = (1, 1, 1, 0)
        }
        self.init(
            .sRGB,
            red: Double(r) / 255,
            green: Double(g) / 255,
            blue: Double(b) / 255,
            opacity: Double(a) / 255
        )
    }
}
