package com.paisaflow.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import com.paisaflow.app.ui.screens.auth.AuthResult
import com.paisaflow.app.ui.screens.auth.BiometricAuth
import com.paisaflow.app.ui.screens.auth.LockScreen
import com.paisaflow.app.ui.theme.PaisaFlowTheme

/**
 * Main entry point for PaisaFlow.
 *
 * FragmentActivity is used because the biometric implementation relies on
 * AndroidX BiometricPrompt with a FragmentActivity host.
 */
class MainActivity : FragmentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?,
    ) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            PaisaFlowTheme {

                /**
                 * Do not use rememberSaveable for the unlocked state.
                 *
                 * An authentication result is security-sensitive transient
                 * state and should not automatically survive Activity/process
                 * recreation through saved-instance state.
                 */
                var isUnlocked by remember {
                    mutableStateOf(false)
                }

                var authError by remember {
                    mutableStateOf<String?>(null)
                }

                var promptInProgress by remember {
                    mutableStateOf(false)
                }

                val biometricAuth = remember {
                    BiometricAuth(this@MainActivity)
                }

                val biometricsAvailable = remember(biometricAuth) {
                    biometricAuth.canAuthenticate()
                }

                /**
                 * Starts biometric authentication while preventing multiple
                 * overlapping prompts caused by rapid taps/recomposition.
                 */
                fun requestUnlock() {
                    if (
                        isUnlocked ||
                        promptInProgress ||
                        !biometricsAvailable
                    ) {
                        return
                    }

                    authError = null
                    promptInProgress = true

                    biometricAuth.prompt { result ->
                        promptInProgress = false

                        when (result) {
                            AuthResult.Success -> {
                                isUnlocked = true
                                authError = null
                            }

                            is AuthResult.Failed -> {
                                isUnlocked = false
                                authError = result.message
                            }

                            AuthResult.Unavailable -> {
                                isUnlocked = false
                                authError = null
                            }
                        }
                    }
                }

                /**
                 * Automatically show the biometric prompt once when the
                 * activity starts and authentication is available.
                 */
                LaunchedEffect(
                    biometricsAvailable,
                ) {
                    if (
                        biometricsAvailable &&
                        !isUnlocked
                    ) {
                        requestUnlock()
                    }
                }

                if (isUnlocked) {
                    PaisaFlowApp()
                } else {
                    LockScreen(
                        biometricsAvailable = biometricsAvailable,
                        errorMessage = authError,
                        onUnlock = {
                            requestUnlock()
                        },

                        /**
                         * Keep this only if PaisaFlow intentionally allows
                         * users to bypass biometric lock.
                         *
                         * For a strict app-lock mode, remove this option from
                         * LockScreen instead of exposing a bypass here.
                         */
                        onContinueWithoutLock = {
                            authError = null
                            isUnlocked = true
                        },
                    )
                }
            }
        }
    }
}
