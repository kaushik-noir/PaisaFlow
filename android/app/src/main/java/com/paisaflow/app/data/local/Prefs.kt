package com.paisaflow.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.paisaflow.app.i18n.AppLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Stores small, non-sensitive application settings.
 *
 * Example:
 * - Selected language
 * - First-run / onboarding status
 *
 * Do not store passwords, tokens, API keys, or other secrets here.
 */
private val Context.dataStore by preferencesDataStore(
    name = "app_settings"
)

class AppPreferences(
    private val context: Context
) {

    companion object {
        private val LANGUAGE_KEY =
            stringPreferencesKey("app_language")

        private val FIRST_RUN_KEY =
            booleanPreferencesKey("is_first_run")
    }

    /**
     * Currently selected application language.
     */
    val language: Flow<AppLanguage> =
        context.dataStore.data.map { preferences ->
            val savedLanguage = preferences[LANGUAGE_KEY]

            savedLanguage
                ?.let { value ->
                    AppLanguage.entries.find {
                        it.name == value
                    }
                }
                ?: AppLanguage.ENGLISH
        }

    /**
     * True when the user hasn't completed the initial app setup.
     */
    val isFirstRun: Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[FIRST_RUN_KEY] ?: true
        }

    /**
     * Save the user's selected language.
     */
    suspend fun setLanguage(
        language: AppLanguage
    ) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language.name
        }
    }

    /**
     * Mark the first-run flow as completed.
     */
    suspend fun completeFirstRun() {
        context.dataStore.edit { preferences ->
            preferences[FIRST_RUN_KEY] = false
        }
    }

    /**
     * Reset first-run state.
     *
     * Useful for debugging or if onboarding must be shown again.
     */
    suspend fun resetFirstRun() {
        context.dataStore.edit { preferences ->
            preferences[FIRST_RUN_KEY] = true
        }
    }
}
