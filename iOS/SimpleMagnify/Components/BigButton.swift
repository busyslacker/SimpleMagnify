import SwiftUI

struct BigButton: View {
    let title: String
    let icon: String
    let action: () -> Void
    let isActive: Bool

    @EnvironmentObject var settings: AppSettings

    init(title: String, icon: String, isActive: Bool = false, action: @escaping () -> Void) {
        self.title = title
        self.icon = icon
        self.isActive = isActive
        self.action = action
    }

    var body: some View {
        Button(action: {
            // Haptic feedback
            let impactFeedback = UIImpactFeedbackGenerator(style: .medium)
            impactFeedback.impactOccurred()
            action()
        }) {
            HStack(spacing: 12) {
                Image(systemName: icon)
                    .font(.system(size: 28, weight: .medium))
                Text(title)
                    .font(Constants.Fonts.button)
            }
            .foregroundColor(isActive ? settings.backgroundColor : settings.buttonColor)
            .frame(maxWidth: .infinity)
            .frame(height: Constants.Dimensions.buttonHeight)
            .background(isActive ? settings.buttonColor : settings.buttonBackgroundColor)
            .cornerRadius(Constants.Dimensions.cornerRadius)
        }
        .accessibilityLabel("\(title) button")
        .accessibilityHint(isActive ? "Currently active. Double tap to toggle." : "Double tap to activate.")
    }
}

struct BigButton_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: 20) {
            BigButton(title: "LIGHT", icon: "flashlight.on.fill", isActive: false) {}
            BigButton(title: "LIGHT", icon: "flashlight.on.fill", isActive: true) {}
            BigButton(title: "FREEZE", icon: "camera.fill") {}
        }
        .padding()
        .environmentObject(AppSettings())
    }
}
