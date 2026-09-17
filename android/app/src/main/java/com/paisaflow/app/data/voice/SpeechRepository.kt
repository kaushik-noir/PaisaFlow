package com.paisaflow.app.data.voice

import android.util.Base64
import com.paisaflow.app.data.network.ApiClient
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Request sent to the backend speech-to-text endpoint.
 */
@Serializable
data class TranscribeRequest(
    @SerialName("audio_base64")
    val audioBase64: String,

    val language: String = DEFAULT_LANGUAGE,

    @SerialName("sampling_rate")
    val samplingRate: Int = DEFAULT_SAMPLE_RATE,
)

/**
 * Speech-to-text response.
 */
@Serializable
data class TranscribeResponse(
    val text: String,

    @SerialName("language_used")
    val languageUsed: String,

    @SerialName("is_sample_data")
    val isSampleData: Boolean = false,
)

/**
 * Request sent to the backend text-to-speech endpoint.
 */
@Serializable
data class SynthesiseRequest(
    val text: String,
    val language: String = DEFAULT_LANGUAGE,
    val gender: String = DEFAULT_TTS_GENDER,
)

/**
 * Text-to-speech response.
 */
@Serializable
data class SynthesiseResponse(
    @SerialName("audio_base64")
    val audioBase64: String? = null,

    @SerialName("language_used")
    val languageUsed: String,

    @SerialName("is_sample_data")
    val isSampleData: Boolean = false,

    val note: String? = null,
)

/**
 * Translation request.
 */
@Serializable
data class TranslateRequest(
    val text: String,
    val source: String = DEFAULT_LANGUAGE,
    val target: String = "en",
)

/**
 * Translation response.
 */
@Serializable
data class TranslateResponse(
    val text: String,

    @SerialName("language_used")
    val languageUsed: String,

    @SerialName("is_sample_data")
    val isSampleData: Boolean = false,
)

/**
 * PaisaFlow speech/language API.
 *
 * The Android app talks only to the PaisaFlow backend.
 * Provider-specific credentials must remain on the server.
 */
interface SpeechApi {

    @POST("api/v1/speech/transcribe")
    suspend fun transcribe(
        @Body body: TranscribeRequest,
    ): TranscribeResponse

    @POST("api/v1/speech/synthesise")
    suspend fun synthesise(
        @Body body: SynthesiseRequest,
    ): SynthesiseResponse

    @POST("api/v1/speech/translate")
    suspend fun translate(
        @Body body: TranslateRequest,
    ): TranslateResponse
}

/**
 * Speech + language repository backed by the PaisaFlow API.
 *
 * Responsibilities:
 * - Convert WAV bytes to Base64 before upload.
 * - Validate basic user input before network calls.
 * - Preserve coroutine cancellation.
 * - Return ordinary network/API failures as Result.failure.
 */
object SpeechRepository {

    const val DEFAULT_LANGUAGE = "hi"
    const val DEFAULT_SAMPLE_RATE = 16_000
    const val DEFAULT_TTS_GENDER = "female"

    /**
     * UI language codes currently supported by the PaisaFlow speech flow.
     *
     * Kept as Pair<String, String> so existing Compose code using
     * .first / .second continues to work.
     */
    val languages: List<Pair<String, String>> = listOf(
        "hi" to "हिंदी",
        "bho" to "भोजपुरी",
        "mag" to "मगही",
        "mai" to "मैथिली",
        "en" to "English",
    )

    private val supportedLanguageCodes: Set<String> =
        languages.mapTo(mutableSetOf()) { it.first }

    private val api: SpeechApi by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        ApiClient.retrofit.create(SpeechApi::class.java)
    }

    /**
     * Sends a WAV recording for speech recognition.
     */
    suspend fun transcribe(
        wav: ByteArray,
        language: String = DEFAULT_LANGUAGE,
        samplingRate: Int = DEFAULT_SAMPLE_RATE,
    ): Result<TranscribeResponse> {
        if (wav.isEmpty()) {
            return Result.failure(
                IllegalArgumentException("Audio recording is empty."),
            )
        }

        if (samplingRate <= 0) {
            return Result.failure(
                IllegalArgumentException("Sampling rate must be greater than 0."),
            )
        }

        val languageCode = normaliseLanguage(language)

        return safeApiCall {
            val encodedAudio = Base64.encodeToString(
                wav,
                Base64.NO_WRAP,
            )

            api.transcribe(
                TranscribeRequest(
                    audioBase64 = encodedAudio,
                    language = languageCode,
                    samplingRate = samplingRate,
                ),
            )
        }
    }

    /**
     * Converts text into speech through the backend.
     */
    suspend fun synthesise(
        text: String,
        language: String = DEFAULT_LANGUAGE,
        gender: String = DEFAULT_TTS_GENDER,
    ): Result<SynthesiseResponse> {
        val cleanText = text.trim()

        if (cleanText.isEmpty()) {
            return Result.failure(
                IllegalArgumentException("Text to synthesise cannot be empty."),
            )
        }

        val cleanGender = gender.trim().ifBlank {
            DEFAULT_TTS_GENDER
        }

        return safeApiCall {
            api.synthesise(
                SynthesiseRequest(
                    text = cleanText,
                    language = normaliseLanguage(language),
                    gender = cleanGender,
                ),
            )
        }
    }

    /**
     * Translates text between supported languages.
     */
    suspend fun translate(
        text: String,
        source: String,
        target: String,
    ): Result<TranslateResponse> {
        val cleanText = text.trim()

        if (cleanText.isEmpty()) {
            return Result.failure(
                IllegalArgumentException("Text to translate cannot be empty."),
            )
        }

        val sourceLanguage = normaliseLanguage(source)
        val targetLanguage = normaliseLanguage(target)

        // No network request is needed when both languages are the same.
        if (sourceLanguage == targetLanguage) {
            return Result.success(
                TranslateResponse(
                    text = cleanText,
                    languageUsed = targetLanguage,
                    isSampleData = false,
                ),
            )
        }

        return safeApiCall {
            api.translate(
                TranslateRequest(
                    text = cleanText,
                    source = sourceLanguage,
                    target = targetLanguage,
                ),
            )
        }
    }

    /**
     * Returns true if the code is part of the language list exposed to the UI.
     */
    fun isLanguageSupported(
        language: String,
    ): Boolean {
        return language
            .trim()
            .lowercase() in supportedLanguageCodes
    }

    /**
     * Cleans and validates a language code before sending it to the backend.
     */
    private fun normaliseLanguage(
        language: String,
    ): String {
        val code = language
            .trim()
            .lowercase()
            .ifBlank { DEFAULT_LANGUAGE }

        require(code in supportedLanguageCodes) {
            "Unsupported language code: $code"
        }

        return code
    }

    /**
     * Converts ordinary failures to Result.failure while allowing coroutine
     * cancellation to propagate normally.
     */
    private suspend inline fun <T> safeApiCall(
        crossinline block: suspend () -> T,
    ): Result<T> {
        return try {
            Result.success(block())
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            Result.failure(error)
        }
    }
}

/*
 * File-level defaults are required by @Serializable request classes.
 * They intentionally mirror SpeechRepository's public constants.
 */
private const val DEFAULT_LANGUAGE = "hi"
private const val DEFAULT_SAMPLE_RATE = 16_000
private const val DEFAULT_TTS_GENDER = "female"
