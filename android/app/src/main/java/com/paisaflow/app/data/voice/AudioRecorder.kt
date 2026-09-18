package com.paisaflow.app.data.voice

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Records 16 kHz, mono, 16-bit PCM audio and returns a complete WAV byte array.
 *
 * Caller must hold android.permission.RECORD_AUDIO before calling start().
 *
 * This class is intended for short voice commands / ASR input.
 * Do not use it for long continuous recordings because audio is kept in memory.
 */
class AudioRecorder(
    private val sampleRate: Int = DEFAULT_SAMPLE_RATE,
) {

    companion object {
        private const val DEFAULT_SAMPLE_RATE = 16_000
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT

        private const val CHANNEL_COUNT = 1
        private const val BITS_PER_SAMPLE = 16
        private const val WAV_HEADER_SIZE = 44
    }

    private val recording = AtomicBoolean(false)

    @Volatile
    private var recorder: AudioRecord? = null

    @Volatile
    private var recordingThread: Thread? = null

    private var audioBuffer = ByteArrayOutputStream()

    val isRecording: Boolean
        get() = recording.get()

    init {
        require(sampleRate > 0) {
            "Sample rate must be greater than 0."
        }
    }

    /**
     * Starts microphone recording.
     *
     * Caller must already have RECORD_AUDIO permission.
     */
    @SuppressLint("MissingPermission")
    @Synchronized
    fun start() {
        if (recording.get()) {
            return
        }

        val minBufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
        )

        require(minBufferSize > 0) {
            "Unable to determine a valid AudioRecord buffer size."
        }

        // Give AudioRecord some headroom over the platform minimum.
        val recordBufferSize = minBufferSize * 2

        val newRecorder = AudioRecord.Builder()
            .setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION)
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AUDIO_FORMAT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(CHANNEL_CONFIG)
                    .build(),
            )
            .setBufferSizeInBytes(recordBufferSize)
            .build()

        if (newRecorder.state != AudioRecord.STATE_INITIALIZED) {
            newRecorder.release()
            throw IllegalStateException("AudioRecord could not be initialized.")
        }

        audioBuffer.close()
        audioBuffer = ByteArrayOutputStream()

        try {
            newRecorder.startRecording()
        } catch (error: Exception) {
            newRecorder.release()
            throw error
        }

        recorder = newRecorder
        recording.set(true)

        recordingThread = Thread(
            {
                recordLoop(
                    audioRecord = newRecorder,
                    chunkSize = minBufferSize,
                )
            },
            "PaisaFlow-AudioRecorder",
        ).also { thread ->
            thread.start()
        }
    }

    /**
     * Stops recording and returns a complete WAV file:
     * 44-byte WAV header + PCM audio bytes.
     *
     * If recording has not started, an empty WAV file is returned.
     */
    @Synchronized
    fun stop(): ByteArray {
        if (!recording.get() && recorder == null) {
            return wrapWav(
                pcm = ByteArray(0),
                rate = sampleRate,
            )
        }

        recording.set(false)

        val activeRecorder = recorder

        // stop() may throw if the recorder changed state unexpectedly.
        try {
            if (activeRecorder?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                activeRecorder.stop()
            }
        } catch (_: IllegalStateException) {
            // Continue cleanup. The captured PCM, if any, can still be returned.
        }

        recordingThread?.let { thread ->
            try {
                thread.join(1_500)
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }

        recordingThread = null

        try {
            activeRecorder?.release()
        } finally {
            recorder = null
        }

        val pcm = audioBuffer.toByteArray()

        return wrapWav(
            pcm = pcm,
            rate = sampleRate,
        )
    }

    /**
     * Releases microphone resources without using the recorded audio.
     */
    @Synchronized
    fun cancel() {
        recording.set(false)

        val activeRecorder = recorder

        try {
            if (activeRecorder?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                activeRecorder.stop()
            }
        } catch (_: IllegalStateException) {
            // Ignore and continue cleanup.
        }

        recordingThread?.let { thread ->
            try {
                thread.join(1_500)
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }

        recordingThread = null

        try {
            activeRecorder?.release()
        } finally {
            recorder = null
            audioBuffer.reset()
        }
    }

    private fun recordLoop(
        audioRecord: AudioRecord,
        chunkSize: Int,
    ) {
        val chunk = ByteArray(chunkSize)

        try {
            while (recording.get()) {
                val bytesRead = audioRecord.read(
                    chunk,
                    0,
                    chunk.size,
                    AudioRecord.READ_BLOCKING,
                )

                when {
                    bytesRead > 0 -> {
                        audioBuffer.write(
                            chunk,
                            0,
                            bytesRead,
                        )
                    }

                    bytesRead == AudioRecord.ERROR_DEAD_OBJECT -> {
                        recording.set(false)
                    }

                    bytesRead == AudioRecord.ERROR_BAD_VALUE ||
                        bytesRead == AudioRecord.ERROR_INVALID_OPERATION -> {
                        recording.set(false)
                    }
                }
            }
        } catch (_: Exception) {
            recording.set(false)
        }
    }

    /**
     * Converts raw little-endian PCM into a standard PCM WAV file.
     */
    private fun wrapWav(
        pcm: ByteArray,
        rate: Int,
    ): ByteArray {
        val bytesPerSample = BITS_PER_SAMPLE / 8
        val blockAlign = CHANNEL_COUNT * bytesPerSample
        val byteRate = rate * blockAlign

        val dataSize = pcm.size
        val riffChunkSize = 36 + dataSize

        val header = ByteBuffer
            .allocate(WAV_HEADER_SIZE)
            .order(ByteOrder.LITTLE_ENDIAN)

        header.putAscii("RIFF")
        header.putInt(riffChunkSize)
        header.putAscii("WAVE")

        header.putAscii("fmt ")
        header.putInt(16) // PCM fmt chunk size.
        header.putShort(1.toShort()) // PCM audio format.
        header.putShort(CHANNEL_COUNT.toShort())
        header.putInt(rate)
        header.putInt(byteRate)
        header.putShort(blockAlign.toShort())
        header.putShort(BITS_PER_SAMPLE.toShort())

        header.putAscii("data")
        header.putInt(dataSize)

        return ByteArray(WAV_HEADER_SIZE + dataSize).also { wav ->
            System.arraycopy(
                header.array(),
                0,
                wav,
                0,
                WAV_HEADER_SIZE,
            )

            System.arraycopy(
                pcm,
                0,
                wav,
                WAV_HEADER_SIZE,
                dataSize,
            )
        }
    }

    private fun ByteBuffer.putAscii(value: String) {
        put(value.toByteArray(Charsets.US_ASCII))
    }
}
