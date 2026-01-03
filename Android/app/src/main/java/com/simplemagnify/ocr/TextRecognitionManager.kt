package com.simplemagnify.ocr

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

object TextRecognitionManager {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    /**
     * Performs OCR on the given bitmap and returns extracted text
     */
    fun recognizeText(
        bitmap: Bitmap,
        onSuccess: (String?) -> Unit,
        onError: (Exception) -> Unit = {}
    ) {
        val image = InputImage.fromBitmap(bitmap, 0)

        recognizer.process(image)
            .addOnSuccessListener { result ->
                val text = result.text.takeIf { it.isNotBlank() }
                onSuccess(text)
            }
            .addOnFailureListener { e ->
                onError(e)
                onSuccess(null)
            }
    }

    /**
     * Performs OCR and returns text with line-by-line structure preserved
     */
    fun recognizeTextWithLines(
        bitmap: Bitmap,
        onSuccess: (String?) -> Unit,
        onError: (Exception) -> Unit = {}
    ) {
        val image = InputImage.fromBitmap(bitmap, 0)

        recognizer.process(image)
            .addOnSuccessListener { result ->
                val lines = result.textBlocks.flatMap { block ->
                    block.lines.map { line -> line.text }
                }
                val text = lines.joinToString("\n").takeIf { it.isNotBlank() }
                onSuccess(text)
            }
            .addOnFailureListener { e ->
                onError(e)
                onSuccess(null)
            }
    }
}
