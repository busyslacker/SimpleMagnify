import Vision
import UIKit

class TextRecognitionManager {

    /// Performs OCR on the given image and returns extracted text
    static func recognizeText(in image: UIImage, completion: @escaping (String?) -> Void) {
        guard let cgImage = image.cgImage else {
            completion(nil)
            return
        }

        // Create a request for text recognition
        let request = VNRecognizeTextRequest { request, error in
            if let error = error {
                print("Text recognition error: \(error)")
                completion(nil)
                return
            }

            // Extract text from results
            guard let observations = request.results as? [VNRecognizedTextObservation] else {
                completion(nil)
                return
            }

            // Combine all recognized text
            let recognizedText = observations.compactMap { observation in
                observation.topCandidates(1).first?.string
            }.joined(separator: "\n")

            DispatchQueue.main.async {
                completion(recognizedText.isEmpty ? nil : recognizedText)
            }
        }

        // Configure for accurate recognition
        request.recognitionLevel = .accurate
        request.usesLanguageCorrection = true

        // Perform the request
        let handler = VNImageRequestHandler(cgImage: cgImage, options: [:])

        DispatchQueue.global(qos: .userInitiated).async {
            do {
                try handler.perform([request])
            } catch {
                print("Failed to perform text recognition: \(error)")
                DispatchQueue.main.async {
                    completion(nil)
                }
            }
        }
    }
}
