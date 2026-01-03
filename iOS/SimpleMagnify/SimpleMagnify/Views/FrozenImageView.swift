import SwiftUI

enum ViewMode {
    case image
    case text
}

struct FrozenImageView: View {
    let image: UIImage
    @Binding var isPresented: Bool
    @EnvironmentObject var settings: AppSettings

    @State private var scale: CGFloat = 1.0
    @State private var lastScale: CGFloat = 1.0
    @State private var offset: CGSize = .zero
    @State private var lastOffset: CGSize = .zero

    @State private var viewMode: ViewMode = .image
    @State private var recognizedText: String?
    @State private var isProcessingOCR = false

    @StateObject private var speechManager = SpeechManager()

    private let maxScale: CGFloat = Constants.Zoom.frozenImageMax

    var body: some View {
        ZStack {
            settings.backgroundColor.ignoresSafeArea()

            VStack(spacing: 0) {
                // Header
                headerView

                // Content (Image or Text)
                if viewMode == .image {
                    imageView
                } else {
                    textView
                }

                // Mode toggle and controls
                controlsView
            }

            // Processing overlay
            if isProcessingOCR {
                processingOverlay
            }
        }
        .onAppear {
            performOCR()
        }
        .onDisappear {
            speechManager.stop()
        }
    }

    // MARK: - Header
    private var headerView: some View {
        HStack {
            Button(action: {
                let impactFeedback = UIImpactFeedbackGenerator(style: .light)
                impactFeedback.impactOccurred()
                speechManager.stop()
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

            // Copy button (only visible in text mode)
            if viewMode == .text, let text = recognizedText, !text.isEmpty {
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
        }
        .padding(.horizontal, Constants.Dimensions.screenPadding)
        .frame(height: Constants.Dimensions.headerHeight)
    }

    // MARK: - Image View
    private var imageView: some View {
        GeometryReader { geometry in
            Image(uiImage: image)
                .resizable()
                .aspectRatio(contentMode: .fit)
                .scaleEffect(scale)
                .offset(offset)
                .gesture(
                    MagnificationGesture()
                        .onChanged { value in
                            let delta = value / lastScale
                            lastScale = value
                            scale = min(max(scale * delta, 1), maxScale)
                        }
                        .onEnded { _ in
                            lastScale = 1.0
                            if scale < 1 {
                                withAnimation {
                                    scale = 1
                                    offset = .zero
                                }
                            }
                        }
                )
                .simultaneousGesture(
                    DragGesture()
                        .onChanged { value in
                            if scale > 1 {
                                offset = CGSize(
                                    width: lastOffset.width + value.translation.width,
                                    height: lastOffset.height + value.translation.height
                                )
                            }
                        }
                        .onEnded { _ in
                            lastOffset = offset
                        }
                )
                .gesture(
                    TapGesture(count: 2)
                        .onEnded {
                            withAnimation {
                                if scale > 1 {
                                    scale = 1
                                    offset = .zero
                                    lastOffset = .zero
                                } else {
                                    scale = 3
                                }
                            }
                        }
                )
                .frame(width: geometry.size.width, height: geometry.size.height)
                .clipped()
        }
        .background(Color.black)
        .accessibilityLabel("Frozen magnified image")
        .accessibilityHint("Pinch to zoom, drag to pan, double tap to reset zoom")
    }

    // MARK: - Text View
    private var textView: some View {
        ScrollView {
            if let text = recognizedText, !text.isEmpty {
                Text(text)
                    .font(.system(size: 28, weight: .medium))
                    .foregroundColor(settings.textColor)
                    .lineSpacing(8)
                    .padding(Constants.Dimensions.screenPadding)
                    .frame(maxWidth: .infinity, alignment: .leading)
            } else {
                VStack(spacing: 16) {
                    Image(systemName: "text.magnifyingglass")
                        .font(.system(size: 60))
                        .foregroundColor(settings.textColor.opacity(0.5))

                    Text("No text found")
                        .font(Constants.Fonts.header)
                        .foregroundColor(settings.textColor.opacity(0.5))

                    Text("Try capturing an image with clearer text")
                        .font(Constants.Fonts.label)
                        .foregroundColor(settings.textColor.opacity(0.5))
                        .multilineTextAlignment(.center)
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .padding(40)
            }
        }
        .background(Color.black.opacity(0.3))
        .accessibilityLabel("Extracted text from image")
    }

    // MARK: - Controls
    private var controlsView: some View {
        VStack(spacing: Constants.Dimensions.buttonSpacing) {
            // Mode toggle buttons
            HStack(spacing: Constants.Dimensions.buttonSpacing) {
                BigButton(
                    title: "Image",
                    icon: "photo",
                    isActive: viewMode == .image
                ) {
                    viewMode = .image
                }

                BigButton(
                    title: "Text",
                    icon: "doc.text",
                    isActive: viewMode == .text
                ) {
                    viewMode = .text
                }
                .disabled(isProcessingOCR)
                .opacity(isProcessingOCR ? 0.6 : 1.0)
            }
            .padding(.horizontal, Constants.Dimensions.screenPadding)

            // Speech controls (only in text mode with recognized text)
            if viewMode == .text, let text = recognizedText, !text.isEmpty {
                speechControlsView(text: text)
                    .padding(.horizontal, Constants.Dimensions.screenPadding)
            }

            // Back to camera button
            BigButton(
                title: Constants.Strings.backToCamera,
                icon: "arrow.uturn.backward"
            ) {
                speechManager.stop()
                isPresented = false
            }
            .padding(.horizontal, Constants.Dimensions.screenPadding)
            .padding(.bottom, Constants.Dimensions.screenPadding)
        }
        .padding(.top, Constants.Dimensions.buttonSpacing)
        .background(settings.backgroundColor)
    }

    // MARK: - Speech Controls
    private func speechControlsView(text: String) -> some View {
        HStack(spacing: Constants.Dimensions.buttonSpacing) {
            // Play/Pause button
            BigButton(
                title: speechManager.isSpeaking ? (speechManager.isPaused ? "Resume" : "Pause") : "Read",
                icon: speechManager.isSpeaking ? (speechManager.isPaused ? "play.fill" : "pause.fill") : "speaker.wave.2.fill",
                isActive: speechManager.isSpeaking && !speechManager.isPaused
            ) {
                speechManager.togglePlayPause(text)
            }

            // Stop button (only show when speaking)
            if speechManager.isSpeaking {
                BigButton(
                    title: "Stop",
                    icon: "stop.fill"
                ) {
                    speechManager.stop()
                }
            }
        }
    }

    // MARK: - Processing Overlay
    private var processingOverlay: some View {
        ZStack {
            Color.black.opacity(0.6)
                .ignoresSafeArea()

            VStack(spacing: 16) {
                ProgressView()
                    .scaleEffect(2)
                    .tint(settings.buttonColor)

                Text("Reading text...")
                    .font(Constants.Fonts.label)
                    .foregroundColor(.white)
            }
            .padding(40)
            .background(
                RoundedRectangle(cornerRadius: Constants.Dimensions.cornerRadius)
                    .fill(settings.backgroundColor)
            )
        }
    }

    // MARK: - OCR
    private func performOCR() {
        isProcessingOCR = true

        TextRecognitionManager.recognizeText(in: image) { text in
            self.recognizedText = text
            self.isProcessingOCR = false

            // Auto-switch to text view if text was found
            if text != nil && !text!.isEmpty {
                withAnimation {
                    self.viewMode = .text
                }
            }
        }
    }
}

struct FrozenImageView_Previews: PreviewProvider {
    static var previews: some View {
        FrozenImageView(
            image: UIImage(systemName: "photo")!,
            isPresented: .constant(true)
        )
        .environmentObject(AppSettings())
    }
}
