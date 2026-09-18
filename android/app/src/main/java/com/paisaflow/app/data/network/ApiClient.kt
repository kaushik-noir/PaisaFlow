package com.paisaflow.app.data.network

import com.paisaflow.app.BuildConfig
import com.paisaflow.app.data.mock.DemoData
import com.paisaflow.app.model.BrainUnderstanding
import com.paisaflow.app.model.LoanSimRequest
import com.paisaflow.app.model.LoanSimResponse
import com.paisaflow.app.model.MessageRequest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

/**
 * PaisaFlow versioned backend API.
 *
 * Keep all endpoints versioned so future API changes do not
 * unexpectedly break older versions of the Android app.
 */
interface PaisaFlowApi {

    @POST("api/v1/conversation/message")
    suspend fun sendMessage(
        @Body body: MessageRequest,
    ): BrainUnderstanding

    @POST("api/v1/simulation/loan")
    suspend fun simulateLoan(
        @Body body: LoanSimRequest,
    ): LoanSimResponse
}

/**
 * Central Retrofit / OkHttp client for PaisaFlow.
 *
 * Security notes:
 * - BODY logging is intentionally disabled.
 * - Network logs are enabled only in debug builds.
 * - Demo fallback is allowed only in debug builds.
 * - Production failures are propagated instead of silently showing fake data.
 */
object ApiClient {

    private const val CONNECT_TIMEOUT_SECONDS = 5L
    private const val READ_TIMEOUT_SECONDS = 40L
    private const val WRITE_TIMEOUT_SECONDS = 20L
    private const val CALL_TIMEOUT_SECONDS = 45L

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        explicitNulls = false
    }

    /**
     * BASIC logs only request/response lines and metadata.
     * Never use BODY logging here because requests may contain
     * financial amounts, transcripts, phone numbers, or other private data.
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BASIC
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .callTimeout(CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    private val baseUrl: String
        get() {
            val configuredUrl = BuildConfig.API_BASE_URL.trim()

            require(configuredUrl.isNotBlank()) {
                "API_BASE_URL must not be blank."
            }

            return if (configuredUrl.endsWith("/")) {
                configuredUrl
            } else {
                "$configuredUrl/"
            }
        }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(
                json.asConverterFactory(
                    "application/json".toMediaType(),
                ),
            )
            .build()
    }

    val api: PaisaFlowApi by lazy {
        retrofit.create(PaisaFlowApi::class.java)
    }

    /**
     * Sends a user utterance to the backend.
     *
     * In DEBUG:
     * If the local backend is unavailable, demo data is returned so
     * UI development can continue.
     *
     * In RELEASE:
     * The real exception is propagated. This prevents the production
     * app from accidentally presenting fictional demo data as real data.
     */
    suspend fun sendUtterance(
        text: String,
        language: String = "hinglish",
    ): BrainUnderstanding {
        val cleanText = text.trim()

        require(cleanText.isNotEmpty()) {
            "Message cannot be empty."
        }

        return try {
            api.sendMessage(
                MessageRequest(
                    text = cleanText,
                    language = language.trim().ifBlank { "hinglish" },
                ),
            )
        } catch (error: Exception) {
            if (BuildConfig.DEBUG) {
                DemoData.dairyExpansion
            } else {
                throw error
            }
        }
    }

    /**
     * Runs the loan simulation against the backend.
     *
     * No automatic demo fallback is used here because financial
     * simulation results should never silently switch to sample numbers.
     */
    suspend fun simulateLoan(
        request: LoanSimRequest,
    ): LoanSimResponse {
        return api.simulateLoan(request)
    }
}
