import SwiftUI

struct SettingsToggle: View {
    let title: String
    @Binding var isOn: Bool

    @EnvironmentObject var settings: AppSettings

    var body: some View {
        HStack {
            Text(title)
                .font(Constants.Fonts.label)
                .foregroundColor(settings.textColor)

            Spacer()

            Toggle("", isOn: $isOn)
                .labelsHidden()
                .toggleStyle(BigToggleStyle(settings: settings))
        }
        .padding(.vertical, 16)
        .padding(.horizontal, 20)
        .background(settings.buttonBackgroundColor)
        .cornerRadius(Constants.Dimensions.cornerRadius)
        .accessibilityElement(children: .combine)
        .accessibilityLabel(title)
        .accessibilityValue(isOn ? "On" : "Off")
        .accessibilityHint("Double tap to toggle")
    }
}

struct BigToggleStyle: ToggleStyle {
    let settings: AppSettings

    func makeBody(configuration: Configuration) -> some View {
        Button(action: {
            let impactFeedback = UIImpactFeedbackGenerator(style: .light)
            impactFeedback.impactOccurred()
            configuration.isOn.toggle()
        }) {
            RoundedRectangle(cornerRadius: 20)
                .fill(configuration.isOn ? settings.buttonColor : Constants.Colors.sliderTrack)
                .frame(width: 70, height: 40)
                .overlay(
                    Circle()
                        .fill(Color.white)
                        .frame(width: 32, height: 32)
                        .shadow(color: .black.opacity(0.15), radius: 2, x: 0, y: 1)
                        .offset(x: configuration.isOn ? 14 : -14)
                        .animation(.easeInOut(duration: 0.2), value: configuration.isOn)
                )
        }
    }
}

struct SettingsSegmentedControl: View {
    let title: String
    @Binding var selection: ButtonPosition

    @EnvironmentObject var settings: AppSettings

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(title)
                .font(Constants.Fonts.label)
                .foregroundColor(settings.textColor)

            HStack(spacing: Constants.Dimensions.buttonSpacing) {
                ForEach(ButtonPosition.allCases, id: \.self) { position in
                    Button(action: {
                        let impactFeedback = UIImpactFeedbackGenerator(style: .light)
                        impactFeedback.impactOccurred()
                        selection = position
                    }) {
                        Text(position == .left ? Constants.Strings.left : Constants.Strings.right)
                            .font(Constants.Fonts.button)
                            .foregroundColor(selection == position ? settings.backgroundColor : settings.textColor)
                            .frame(maxWidth: .infinity)
                            .frame(height: 60)
                            .background(selection == position ? settings.buttonColor : settings.buttonBackgroundColor)
                            .cornerRadius(Constants.Dimensions.cornerRadius)
                    }
                    .accessibilityLabel("\(position == .left ? "Left" : "Right") hand mode")
                    .accessibilityAddTraits(selection == position ? .isSelected : [])
                }
            }
        }
    }
}

struct SettingsToggle_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: 20) {
            SettingsToggle(title: "Light on at start", isOn: .constant(false))
            SettingsToggle(title: "High contrast mode", isOn: .constant(true))
            SettingsSegmentedControl(title: "Button Position", selection: .constant(.right))
        }
        .padding()
        .environmentObject(AppSettings())
    }
}
