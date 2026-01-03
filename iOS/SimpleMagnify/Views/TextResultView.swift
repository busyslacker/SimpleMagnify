import SwiftUI

struct TextResultView: View {
    let text: String
    @Binding var isPresented: Bool
    @EnvironmentObject var settings: AppSettings

    @State private var fontSize: CGFloat = 28

    private let minFontSize: CGFloat = 18
    private let maxFontSize: CGFloat = 48

    var body: some View {
        ZStack {
            settings.backgroundColor.ignoresSafeArea()

            VStack(spacing: 0) {
                // Header
                headerView

                // Text content
                textContentView

                // Font size controls
                fontSizeControls

                // Back button
                backButtonView
            }
        }
    }

    // MARK: - Header
    private var headerView: some View {
        HStack {
            Button(action: {
                let impactFeedback = UIImpactFeedbackGenerator(style: .light)
                impactFeedback.impactOccurred()
                isPresented = false
            }) {
                HStack(spacing: 8) {
                    Image(systemName: "chevron.left")
                        .font(.system(size: 24, weight: .medium))
                    Text("Back")
                        .font(Constants.Fonts.button)
                }
                .foregroundColor(settings.buttonColor)
                .frame(height: Constants.Dimensions.minTapTarget)
            }
            .accessibilityLabel("Go back to camera")

            Spacer()

            // Copy button
            Button(action: {
                UIPasteboard.general.string = text
                let impactFeedback = UIImpactFeedbackGenerator(style: .medium)
                impactFeedback.impactOccurred()
            }) {
                Image(systemName: "doc.on.doc")
                    .font(.system(size: 24))
                    .foregroundColor(settings.buttonColor)
                    .frame(width: Constants.Dimensions.minTapTarget,
                           height: Constants.Dimensions.minTapTarget)
            }
            .accessibilityLabel("Copy text to clipboard")
        }
        .padding(.horizontal, Constants.Dimensions.screenPadding)
        .frame(height: Constants.Dimensions.headerHeight)
    }

    // MARK: - Text Content
    private var textContentView: some View {
        ScrollView {
            Text(text)
                .font(.system(size: fontSize, weight: .medium))
                .foregroundColor(settings.textColor)
                .lineSpacing(8)
                .padding(Constants.Dimensions.screenPadding)
                .frame(maxWidth: .infinity, alignment: .leading)
        }
        .background(Color.black.opacity(0.3))
        .accessibilityLabel("Extracted text")
    }

    // MARK: - Font Size Controls
    private var fontSizeControls: some View {
        HStack(spacing: 20) {
            // Decrease font size
            Button(action: {
                let impactFeedback = UIImpactFeedbackGenerator(style: .light)
                impactFeedback.impactOccurred()
                fontSize = max(minFontSize, fontSize - 4)
            }) {
                Image(systemName: "textformat.size.smaller")
                    .font(.system(size: 28))
                    .foregroundColor(fontSize <= minFontSize ? settings.textColor.opacity(0.3) : settings.buttonColor)
                    .frame(width: Constants.Dimensions.minTapTarget,
                           height: Constants.Dimensions.minTapTarget)
            }
            .disabled(fontSize <= minFontSize)
            .accessibilityLabel("Decrease font size")

            Text("\(Int(fontSize))pt")
                .font(Constants.Fonts.label)
                .foregroundColor(settings.textColor)
                .frame(width: 60)

            // Increase font size
            Button(action: {
                let impactFeedback = UIImpactFeedbackGenerator(style: .light)
                impactFeedback.impactOccurred()
                fontSize = min(maxFontSize, fontSize + 4)
            }) {
                Image(systemName: "textformat.size.larger")
                    .font(.system(size: 28))
                    .foregroundColor(fontSize >= maxFontSize ? settings.textColor.opacity(0.3) : settings.buttonColor)
                    .frame(width: Constants.Dimensions.minTapTarget,
                           height: Constants.Dimensions.minTapTarget)
            }
            .disabled(fontSize >= maxFontSize)
            .accessibilityLabel("Increase font size")
        }
        .padding(.vertical, 12)
        .background(settings.backgroundColor)
    }

    // MARK: - Back Button
    private var backButtonView: some View {
        VStack {
            BigButton(
                title: Constants.Strings.backToCamera,
                icon: "arrow.uturn.backward"
            ) {
                isPresented = false
            }
        }
        .padding(Constants.Dimensions.screenPadding)
        .background(settings.backgroundColor)
    }
}

struct TextResultView_Previews: PreviewProvider {
    static var previews: some View {
        TextResultView(
            text: "This is sample text that would be extracted from an image using OCR. It can be quite long and should scroll nicely.",
            isPresented: .constant(true)
        )
        .environmentObject(AppSettings())
    }
}
