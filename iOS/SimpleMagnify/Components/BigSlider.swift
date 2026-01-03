import SwiftUI

struct BigSlider: View {
    @Binding var value: CGFloat
    let range: ClosedRange<CGFloat>
    let step: CGFloat
    let minLabel: String
    let maxLabel: String

    @EnvironmentObject var settings: AppSettings

    init(value: Binding<CGFloat>,
         range: ClosedRange<CGFloat> = 1.0...10.0,
         step: CGFloat = 0.1,
         minLabel: String = "1x",
         maxLabel: String = "10x") {
        self._value = value
        self.range = range
        self.step = step
        self.minLabel = minLabel
        self.maxLabel = maxLabel
    }

    var body: some View {
        VStack(spacing: 8) {
            // Custom slider
            GeometryReader { geometry in
                ZStack(alignment: .leading) {
                    // Track background
                    RoundedRectangle(cornerRadius: 8)
                        .fill(Constants.Colors.sliderTrack)
                        .frame(height: 16)

                    // Filled track
                    RoundedRectangle(cornerRadius: 8)
                        .fill(settings.buttonColor)
                        .frame(width: thumbPosition(in: geometry.size.width), height: 16)

                    // Thumb
                    Circle()
                        .fill(settings.buttonColor)
                        .frame(width: Constants.Dimensions.sliderThumbSize,
                               height: Constants.Dimensions.sliderThumbSize)
                        .shadow(color: .black.opacity(0.2), radius: 4, x: 0, y: 2)
                        .offset(x: thumbPosition(in: geometry.size.width) - Constants.Dimensions.sliderThumbSize / 2)
                        .gesture(
                            DragGesture()
                                .onChanged { gesture in
                                    updateValue(from: gesture.location.x, in: geometry.size.width)
                                }
                        )
                }
                .frame(height: Constants.Dimensions.sliderHeight)
                .contentShape(Rectangle())
                .onTapGesture { location in
                    updateValue(from: location.x, in: geometry.size.width)
                }
            }
            .frame(height: Constants.Dimensions.sliderHeight)
            .accessibilityElement(children: .ignore)
            .accessibilityLabel("Zoom slider")
            .accessibilityValue("\(String(format: "%.1f", value))x zoom")
            .accessibilityAdjustableAction { direction in
                switch direction {
                case .increment:
                    value = min(value + 1, range.upperBound)
                case .decrement:
                    value = max(value - 1, range.lowerBound)
                @unknown default:
                    break
                }
            }

            // Labels
            HStack {
                Text(minLabel)
                    .font(Constants.Fonts.sliderLabel)
                    .foregroundColor(settings.textColor)
                Spacer()
                Text(String(format: "%.1fx", value))
                    .font(Constants.Fonts.sliderLabel)
                    .fontWeight(.bold)
                    .foregroundColor(settings.buttonColor)
                Spacer()
                Text(maxLabel)
                    .font(Constants.Fonts.sliderLabel)
                    .foregroundColor(settings.textColor)
            }
        }
    }

    private func thumbPosition(in width: CGFloat) -> CGFloat {
        let normalizedValue = (value - range.lowerBound) / (range.upperBound - range.lowerBound)
        return normalizedValue * (width - Constants.Dimensions.sliderThumbSize) + Constants.Dimensions.sliderThumbSize / 2
    }

    private func updateValue(from xPosition: CGFloat, in width: CGFloat) {
        let adjustedWidth = width - Constants.Dimensions.sliderThumbSize
        let clampedX = max(0, min(xPosition - Constants.Dimensions.sliderThumbSize / 2, adjustedWidth))
        let normalizedValue = clampedX / adjustedWidth
        let newValue = range.lowerBound + normalizedValue * (range.upperBound - range.lowerBound)

        // Snap to step
        let steppedValue = round(newValue / step) * step
        value = max(range.lowerBound, min(steppedValue, range.upperBound))

        // Haptic feedback at boundaries
        if value == range.lowerBound || value == range.upperBound {
            let impactFeedback = UIImpactFeedbackGenerator(style: .light)
            impactFeedback.impactOccurred()
        }
    }
}

struct BigSlider_Previews: PreviewProvider {
    static var previews: some View {
        BigSlider(value: .constant(3.0))
            .padding()
            .environmentObject(AppSettings())
    }
}
