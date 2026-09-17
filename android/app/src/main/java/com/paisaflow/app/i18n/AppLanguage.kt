package com.paisaflow.app.i18n

import androidx.compose.runtime.compositionLocalOf

/**
 * Languages available in the PaisaFlow UI.
 *
 * @property code      Internal/UI language code persisted by the app.
 * @property native    Language name shown to the user.
 * @property short     Short label suitable for compact UI elements.
 * @property bhashini  Language code sent to the speech backend/Bhashini.
 *
 * Hinglish uses Hindi speech recognition / TTS, so its Bhashini code is "hi".
 */
enum class AppLanguage(
    val code: String,
    val native: String,
    val short: String,
    val bhashini: String,
) {
    HI(
        code = "hi",
        native = "हिंदी",
        short = "हिं",
        bhashini = "hi",
    ),

    HINGLISH(
        code = "hinglish",
        native = "Hinglish",
        short = "Hin",
        bhashini = "hi",
    ),

    EN(
        code = "en",
        native = "English",
        short = "EN",
        bhashini = "en",
    ),

    BHO(
        code = "bho",
        native = "भोजपुरी",
        short = "भो",
        bhashini = "bho",
    ),

    MAG(
        code = "mag",
        native = "मगही",
        short = "मग",
        bhashini = "mag",
    ),

    MAI(
        code = "mai",
        native = "मैथिली",
        short = "मै",
        bhashini = "mai",
    );

    /**
     * Returns the next language in the selector cycle.
     */
    fun next(): AppLanguage {
        val nextIndex = (ordinal + 1) % entries.size
        return entries[nextIndex]
    }

    companion object {

        private val byUiCode: Map<String, AppLanguage> =
            entries.associateBy { it.code.lowercase() }

        /**
         * Converts a stored/UI language code into [AppLanguage].
         *
         * Exact UI codes are checked first. This matters because both
         * Hindi and Hinglish use "hi" for speech/Bhashini.
         *
         * Unknown or blank values safely fall back to Hindi.
         */
        fun fromCode(
            code: String?,
        ): AppLanguage {
            val normalized = code
                ?.trim()
                ?.lowercase()
                .orEmpty()

            if (normalized.isEmpty()) {
                return HI
            }

            // Prefer the app's own persisted UI code.
            byUiCode[normalized]?.let {
                return it
            }

            // Accept speech/backend language codes as a fallback.
            return entries.firstOrNull {
                it.bhashini.equals(
                    normalized,
                    ignoreCase = true,
                )
            } ?: HI
        }

        /**
         * Same as [fromCode], but returns null when the value is unknown.
         *
         * Useful when callers need to distinguish malformed input
         * from the default Hindi fallback.
         */
        fun fromCodeOrNull(
            code: String?,
        ): AppLanguage? {
            val normalized = code
                ?.trim()
                ?.lowercase()
                .orEmpty()

            if (normalized.isEmpty()) {
                return null
            }

            return byUiCode[normalized]
                ?: entries.firstOrNull {
                    it.bhashini.equals(
                        normalized,
                        ignoreCase = true,
                    )
                }
        }
    }
}

/**
 * Current UI language supplied from the Compose app root.
 *
 * Hindi is the safe default when no provider is present.
 */
val LocalAppLanguage = compositionLocalOf {
    AppLanguage.HI
}
