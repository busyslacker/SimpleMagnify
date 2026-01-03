package com.simplemagnify.speech

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Locale

class SpeechManager(context: Context) : TextToSpeech.OnInitListener {

    var isSpeaking by mutableStateOf(false)
        private set

    var isPaused by mutableStateOf(false)
        private set

    var isInitialized by mutableStateOf(false)
        private set

    private val tts: TextToSpeech = TextToSpeech(context, this)
    private var currentText: String = ""
    private var currentPosition: Int = 0

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.getDefault())
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                isInitialized = true

                // Set speech rate slightly slower for seniors
                tts.setSpeechRate(0.9f)

                // Set up progress listener
                tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        isSpeaking = true
                        isPaused = false
                    }

                    override fun onDone(utteranceId: String?) {
                        isSpeaking = false
                        isPaused = false
                        currentPosition = 0
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        isSpeaking = false
                        isPaused = false
                    }

                    override fun onError(utteranceId: String?, errorCode: Int) {
                        isSpeaking = false
                        isPaused = false
                    }
                })
            }
        }
    }

    /**
     * Speaks the given text
     */
    fun speak(text: String) {
        if (!isInitialized) return

        stop()
        currentText = text
        currentPosition = 0

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "SimpleMagnify_TTS")
    }

    /**
     * Pauses speech (Android TTS doesn't support true pause, so we stop and track position)
     */
    fun pause() {
        if (isSpeaking && !isPaused) {
            tts.stop()
            isPaused = true
            isSpeaking = false
        }
    }

    /**
     * Resumes paused speech (restarts from beginning due to TTS limitations)
     */
    fun resume() {
        if (isPaused && currentText.isNotEmpty()) {
            tts.speak(currentText, TextToSpeech.QUEUE_FLUSH, null, "SimpleMagnify_TTS")
            isPaused = false
        }
    }

    /**
     * Stops speech completely
     */
    fun stop() {
        tts.stop()
        isSpeaking = false
        isPaused = false
        currentPosition = 0
    }

    /**
     * Toggles between play/pause
     */
    fun togglePlayPause(text: String) {
        when {
            isSpeaking -> pause()
            isPaused -> resume()
            else -> speak(text)
        }
    }

    /**
     * Releases TTS resources
     */
    fun shutdown() {
        tts.stop()
        tts.shutdown()
    }
}
