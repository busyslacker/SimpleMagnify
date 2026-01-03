import SwiftUI

struct SettingsView: View {
    @EnvironmentObject var settings: AppSettings
    @Environment(\.dismiss) var dismiss

    var body: some View {
        NavigationView {
            ZStack {
                settings.backgroundColor.ignoresSafeArea()

                ScrollView {
                    VStack(spacing: 24) {
                        // Default Zoom
                        defaultZoomSection

                        Divider()
                            .background(settings.textColor.opacity(0.3))

                        // Light on Start
                        SettingsToggle(
                            title: Constants.Strings.lightOnStart,
                            isOn: $settings.lightOnStart
                        )

                        Divider()
                            .background(settings.textColor.opacity(0.3))

                        // High Contrast Mode
                        SettingsToggle(
                            title: Constants.Strings.highContrastMode,
                            isOn: $settings.highContrastMode
                        )

                        Divider()
                            .background(settings.textColor.opacity(0.3))

                        // Button Position
                        SettingsSegmentedControl(
                            title: Constants.Strings.buttonPosition,
                            selection: $settings.buttonPosition
                        )

                        Spacer(minLength: 40)

                        // App Info
                        appInfoSection
                    }
                    .padding(Constants.Dimensions.screenPadding)
                }
            }
            .navigationTitle(Constants.Strings.settings)
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button(action: {
                        let impactFeedback = UIImpactFeedbackGenerator(style: .light)
                        impactFeedback.impactOccurred()
                        dismiss()
                    }) {
                        HStack(spacing: 8) {
                            Image(systemName: "chevron.left")
                            Text("Back")
                        }
                        .font(Constants.Fonts.button)
                        .foregroundColor(settings.buttonColor)
                    }
                    .accessibilityLabel("Close settings")
                }
            }
        }
    }

    // MARK: - Default Zoom Section
    private var defaultZoomSection: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text(Constants.Strings.defaultZoom)
                .font(Constants.Fonts.label)
                .foregroundColor(settings.textColor)

            BigSlider(value: $settings.defaultZoom)
        }
    }

    // MARK: - App Info Section
    private var appInfoSection: some View {
        VStack(spacing: 8) {
            Text(Constants.Strings.appName)
                .font(Constants.Fonts.label)
                .foregroundColor(settings.textColor)

            Text("Version 1.0.0")
                .font(.system(size: 14))
                .foregroundColor(settings.textColor.opacity(0.6))

            Text("Made with care for seniors")
                .font(.system(size: 14))
                .foregroundColor(settings.textColor.opacity(0.6))
                .padding(.top, 4)
        }
        .padding(.vertical, 20)
    }
}

struct SettingsView_Previews: PreviewProvider {
    static var previews: some View {
        SettingsView()
            .environmentObject(AppSettings())
    }
}
