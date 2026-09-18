package com.paisaflow.app.data.voice

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.util.Base64
import java.io.File
import java.util.Locale
import java.util.UUID

/**
 * Plays TTS audio returned by the backend.
 *
 * If backend audio is unavailable, [speakFallback] uses the device's
 * built-in TextToSpeech engine.
 *
 * Call [release] when this class is no longer needed.
 */
class Speaker(
    context: Context,
) {

    companion object {
        private val HINDI_INDIA = Locale("hi", "IN")
    }

    private val appContext = context.applicationContext

    @Volatile
    private var ttsReady = false

    private var tts: TextToSpeech? = null
    private var player: MediaPlayer? = null
    private var currentAudioFile: File? = null

    init {
        tts = TextToSpeech(appContext) { status ->
            if (status != TextToSpeech.SUCCESS) {
                ttsReady = false
                return@TextToSpeech
            }

            val engine = tts ?: return@TextToSpeech
            val availability = engine.isLanguageAvailable(HINDI_INDIA)

            ttsReady = availability != TextToSpeech.LANG_MISSING_DATA &&
                availability != TextToSpeech.LANG_NOT_SUPPORTED

            if (ttsReady) {
                engine.language = HINDI_INDIA
            }
        }
    }

    /**
     * Plays Base64 encoded WAV/audio returned by the backend.
     *
     * Returns true when playback setup was started successfully.
     * Returns false if the Base64/audio data is invalid.
     */
    @Synchronized
    fun playBase64Wav(
        audioBase64: String,
    ): Boolean {
        val cleanAudio = audioBase64
            .substringAfter(
                delimiter = "base64,",
                missingDelimiterValue = audioBase64,
            )
            .trim()

        if (cleanAudio.isEmpty()) {
            return false
        }

        stop()

        val audioBytes = try {
            Base64.decode(
                cleanAudio,
                Base64.DEFAULT,
            )
        } catch (_: IllegalArgumentException) {
            return false
        }

        if (audioBytes.isEmpty()) {
            return false
        }

        val audioFile = try {
            File.createTempFile(
                "pf_tts_",
                ".wav",
                appContext.cacheDir,
            ).apply {
                writeBytes(audioBytes)
            }
        } catch (_: Exception) {
            return false
        }

        currentAudioFile = audioFile

        val newPlayer = MediaPlayer()
        player = newPlayer

        return try {
            newPlayer.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build(),
            )

            newPlayer.setDataSource(audioFile.absolutePath)

            newPlayer.setOnPreparedListener { mediaPlayer ->
                runCatching {
                    mediaPlayer.start()
                }.onFailure {
                    releasePlayer()
                }
            }

            newPlayer.setOnCompletionListener {
                releasePlayer()
            }

            newPlayer.setOnErrorListener { _, _, _ ->
                releasePlayer()
                true
            }

            // Avoid blocking the UI thread while the file is prepared.
            newPlayer.prepareAsync()

            true
        } catch (_: Exception) {
            releasePlayer()
            false
        }
    }

    /**
     * Uses Android's local TextToSpeech engine as a fallback.
     *
     * Returns true if the request was accepted by the TTS engine.
     */
    @Synchronized
    fun speakFallback(
        text: String,
    ): Boolean {
        val cleanText = text.trim()

        if (cleanText.isEmpty() || !ttsReady) {
            return false
        }

        // Stop backend audio before device TTS starts.
        releasePlayer()

        val result = tts?.speak(
            cleanText,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "pf-${UUID.randomUUID()}",
        ) ?: TextToSpeech.ERROR

        return result == TextToSpeech.SUCCESS
    }

    /**
     * Stops current playback and any active TTS utterance.
     */
    @Synchronized
    fun stop() {
        releasePlayer()

        runCatching {
            tts?.stop()
        }
    }

    /**
     * Releases all resources.
     *
     * Call from ViewModel/owner cleanup when Speaker will no longer be used.
     */
    @Synchronized
    fun release() {
        stop()

        runCatching {
            tts?.shutdown()
        }

        tts = null
        ttsReady = false
    }

    /**
     * Releases MediaPlayer and removes the temporary cached audio file.
     */
    @Synchronized
    private fun releasePlayer() {
        val activePlayer = player
        player = null

        if (activePlayer != null) {
            runCatching {
                if (activePlayer.isPlaying) {
                    activePlayer.stop()
                }
            }

            runCatching {
                activePlayer.reset()
            }

            runCatching {
                activePlayer.release()
            }
        }

        currentAudioFile?.let { file ->
            runCatching {
                if (file.exists()) {
                    file.delete()
                }
            }
        }

        currentAudioFile = null
    }
}
