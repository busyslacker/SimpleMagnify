import SwiftUI
import Combine

enum ButtonPosition: String, CaseIterable {
    case left
    case right
}

class AppSettings: ObservableObject {
    @Published var defaultZoom: CGFloat {
        didSet {
            UserDefaults.standard.set(defaultZoom, forKey: Constants.StorageKeys.defaultZoom)
        }
    }

    @Published var lightOnStart: Bool {
        didSet {
            UserDefaults.standard.set(lightOnStart, forKey: Constants.StorageKeys.lightOnStart)
        }
    }

    @Published var highContrastMode: Bool {
        didSet {
            UserDefaults.standard.set(highContrastMode, forKey: Constants.StorageKeys.highContrastMode)
        }
    }

    @Published var buttonPosition: ButtonPosition {
        didSet {
            UserDefaults.standard.set(buttonPosition.rawValue, forKey: Constants.StorageKeys.buttonPosition)
        }
    }

    init() {
        // Load saved settings or use defaults
        self.defaultZoom = UserDefaults.standard.object(forKey: Constants.StorageKeys.defaultZoom) as? CGFloat ?? Constants.Zoom.defaultValue
        self.lightOnStart = UserDefaults.standard.bool(forKey: Constants.StorageKeys.lightOnStart)
        self.highContrastMode = UserDefaults.standard.bool(forKey: Constants.StorageKeys.highContrastMode)

        let positionString = UserDefaults.standard.string(forKey: Constants.StorageKeys.buttonPosition) ?? ButtonPosition.right.rawValue
        self.buttonPosition = ButtonPosition(rawValue: positionString) ?? .right
    }

    // MARK: - Computed Colors based on mode
    var backgroundColor: Color {
        highContrastMode ? Constants.Colors.hcBackground : Constants.Colors.background
    }

    var textColor: Color {
        highContrastMode ? Constants.Colors.hcText : Constants.Colors.textPrimary
    }

    var buttonColor: Color {
        highContrastMode ? Constants.Colors.hcButtonPrimary : Constants.Colors.buttonPrimary
    }

    var buttonBackgroundColor: Color {
        highContrastMode ? Constants.Colors.hcButtonBackground : Constants.Colors.buttonBackground
    }
}
