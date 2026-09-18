package com.paisaflow.app

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.paisaflow.app.data.local.Prefs
import com.paisaflow.app.data.mock.DemoHome
import com.paisaflow.app.i18n.AppLanguage
import com.paisaflow.app.i18n.LocalAppLanguage
import com.paisaflow.app.ui.components.AppBottomNav
import com.paisaflow.app.ui.components.AppTab
import com.paisaflow.app.ui.components.AppTopBar
import com.paisaflow.app.ui.screens.actions.ActionsScreen
import com.paisaflow.app.ui.screens.alerts.AlertsScreen
import com.paisaflow.app.ui.screens.evidence.EvidenceModeScreen
import com.paisaflow.app.ui.screens.home.HomeHindiScreen
import com.paisaflow.app.ui.screens.home.HomeScreen
import com.paisaflow.app.ui.screens.khata.VoiceKhataScreen
import com.paisaflow.app.ui.screens.memory.MemoryScreen
import com.paisaflow.app.ui.screens.onboarding.LanguageScreen
import com.paisaflow.app.ui.screens.onboarding.OnboardingScreen
import com.paisaflow.app.ui.screens.onboarding.SplashScreen
import com.paisaflow.app.ui.screens.profile.ProfileScreen
import com.paisaflow.app.ui.screens.simulate.SimulateScreen
import com.paisaflow.app.ui.screens.twin.TwinScreen
import com.paisaflow.app.ui.screens.voice.VoiceConversationScreen
import com.paisaflow.app.ui.theme.PfColors
import kotlinx.coroutines.launch

/**
 * Secondary full-screen destinations shown above the main tab shell.
 */
enum class Route {
    NONE,
    EVIDENCE,
    MEMORY,
    KHATA,
    ALERTS,
    PROFILE,
}

/**
 * Root PaisaFlow UI flow:
 *
 * Splash
 *   ↓
 * Language selection (first run only)
 *   ↓
 * Onboarding (first run only)
 *   ↓
 * Main tab shell
 *   ↓
 * Secondary full-screen routes
 *
 * The selected app language is provided to the whole UI through
 * [LocalAppLanguage].
 */
@Composable
fun PaisaFlowApp() {
    val context = LocalContext.current

    val prefs = remember(context) {
        Prefs(context.applicationContext)
    }

    var language by rememberSaveable {
        mutableStateOf(prefs.language)
    }

    var splashDone by rememberSaveable {
        mutableStateOf(false)
    }

    var onboarded by rememberSaveable {
        mutableStateOf(prefs.onboarded)
    }

    var languageChosen by rememberSaveable {
        mutableStateOf(prefs.onboarded)
    }

    var currentTab by rememberSaveable {
        mutableStateOf(AppTab.HOME)
    }

    var currentRoute by rememberSaveable {
        mutableStateOf(Route.NONE)
    }

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val coroutineScope = rememberCoroutineScope()

    /**
     * App-wide lightweight user feedback.
     *
     * The previous snackbar is dismissed before showing the next one so
     * repeated actions do not build up a long queue.
     */
    val showMessage: (String) -> Unit = remember(
        snackbarHostState,
        coroutineScope,
    ) {
        { message ->
            val cleanMessage = message.trim()

            if (cleanMessage.isNotEmpty()) {
                coroutineScope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()

                    snackbarHostState.showSnackbar(
                        message = cleanMessage,
                        duration = SnackbarDuration.Short,
                    )
                }
            }
        }
    }

    /**
     * Changes and persists the selected language.
     */
    fun changeLanguage(
        newLanguage: AppLanguage,
        showConfirmation: Boolean = false,
    ) {
        language = newLanguage
        prefs.language = newLanguage

        if (showConfirmation) {
            showMessage(newLanguage.native)
        }
    }

    /**
     * Cycles to the next configured app language.
     *
     * Important: calculate the next language before changing state so the
     * snackbar displays the new language, not the previous one.
     */
    fun selectNextLanguage() {
        val nextLanguage = language.next()

        changeLanguage(
            newLanguage = nextLanguage,
            showConfirmation = true,
        )
    }

    /**
     * Completes onboarding and moves directly to the conversation tab.
     */
    fun completeOnboarding() {
        onboarded = true
        prefs.onboarded = true
        currentRoute = Route.NONE
        currentTab = AppTab.TALK
    }

    /**
     * Opens a secondary full-screen route.
     */
    fun openRoute(
        route: Route,
    ) {
        currentRoute = route
    }

    /**
     * Closes any currently open secondary route.
     */
    fun closeRoute() {
        currentRoute = Route.NONE
    }

    /**
     * Changes the bottom-nav tab and closes any overlay route.
     */
    fun selectTab(
        tab: AppTab,
    ) {
        currentRoute = Route.NONE
        currentTab = tab
    }

    CompositionLocalProvider(
        LocalAppLanguage provides language,
    ) {

        /**
         * Startup flow.
         */
        when {
            !splashDone -> {
                SplashScreen {
                    splashDone = true
                }

                return@CompositionLocalProvider
            }

            !languageChosen -> {
                LanguageScreen(
                    language,
                    { selectedLanguage ->
                        changeLanguage(selectedLanguage)
                    },
                ) {
                    languageChosen = true
                }

                return@CompositionLocalProvider
            }

            !onboarded -> {
                OnboardingScreen(
                    onSpeak = {
                        completeOnboarding()
                    },
                    onSubmitText = { text ->
                        completeOnboarding()

                        if (text.isNotBlank()) {
                            showMessage(text)
                        }
                    },
                )

                return@CompositionLocalProvider
            }
        }

        /**
         * Secondary full-screen routes.
         */
        if (currentRoute != Route.NONE) {
            BackHandler(
                enabled = true,
            ) {
                closeRoute()
            }

            when (currentRoute) {
                Route.EVIDENCE -> {
                    EvidenceModeScreen(
                        onBack = {
                            closeRoute()
                        },
                        onSpeak = {
                            closeRoute()
                            selectTab(AppTab.TALK)
                        },
                        onToast = showMessage,
                    )
                }

                Route.MEMORY -> {
                    MemoryScreen(
                        onBack = {
                            closeRoute()
                        },
                        onCorrect = {
                            showMessage("Badalna hai — correction event")
                        },
                    )
                }

                Route.KHATA -> {
                    VoiceKhataScreen(
                        onBack = {
                            closeRoute()
                        },
                        onSpeak = {
                            showMessage("🎙️ Boliye…")
                        },
                        transcript = "Ramesh ko ₹850 ka maal diya",
                        onToast = showMessage,
                    )
                }

                Route.ALERTS -> {
                    AlertsScreen(
                        onBack = {
                            closeRoute()
                        },
                        onView = {
                            closeRoute()
                            selectTab(AppTab.KHATA)
                        },
                        onToast = showMessage,
                    )
                }

                Route.PROFILE -> {
                    ProfileScreen(
                        language,
                        onBack = {
                            closeRoute()
                        },
                        onOpenMemory = {
                            openRoute(Route.MEMORY)
                        },
                        onOpenAlerts = {
                            openRoute(Route.ALERTS)
                        },
                        onLanguage = {
                            selectNextLanguage()
                        },
                        onToast = showMessage,
                    )
                }

                Route.NONE -> Unit
            }

            return@CompositionLocalProvider
        }

        /**
         * Hindi Home v2 has its own brand bar.
         * Other languages use the shared app top bar.
         */
        val useHindiHome =
            currentTab == AppTab.HOME &&
                language == AppLanguage.HI

        Scaffold(
            containerColor = PfColors.Surface,

            topBar = {
                if (!useHindiHome) {
                    AppTopBar(
                        businessLabel = DemoHome.data.businessLabel,
                        language = language,
                        onLanguageClick = {
                            selectNextLanguage()
                        },
                        onSpeakClick = {
                            showMessage(
                                "Screen ko padh kar sunaa raha hoon…",
                            )
                        },
                        onProfileClick = {
                            openRoute(Route.PROFILE)
                        },
                    )
                }
            },

            bottomBar = {
                AppBottomNav(
                    current = currentTab,
                    onSelect = { selectedTab ->
                        selectTab(selectedTab)
                    },
                )
            },

            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                )
            },
        ) { innerPadding ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {

                when (currentTab) {
                    AppTab.HOME -> {
                        if (useHindiHome) {
                            HomeHindiScreen(
                                onSpeak = {
                                    selectTab(AppTab.TALK)
                                },
                                onOpenSimulation = {
                                    selectTab(AppTab.WHAT_IF)
                                },
                                onOpenActions = {
                                    selectTab(AppTab.KHATA)
                                },
                                onToggleLanguage = {
                                    selectNextLanguage()
                                },
                                onToast = showMessage,
                            )
                        } else {
                            HomeScreen(
                                onSpeak = {
                                    selectTab(AppTab.TALK)
                                },
                                onOpenActions = {
                                    selectTab(AppTab.KHATA)
                                },
                                onOpenTwin = {
                                    selectTab(AppTab.TWIN)
                                },
                                onToast = showMessage,
                            )
                        }
                    }

                    AppTab.TALK -> {
                        VoiceConversationScreen(
                            onToast = showMessage,
                            onExplain = {
                                openRoute(Route.EVIDENCE)
                            },
                            language = language,
                            onLanguage = { selectedLanguage ->
                                changeLanguage(selectedLanguage)
                            },
                        )
                    }

                    AppTab.TWIN -> {
                        TwinScreen(
                            onOpenMemory = {
                                openRoute(Route.MEMORY)
                            },
                            onRunWhatIf = {
                                selectTab(AppTab.WHAT_IF)
                            },
                        )
                    }

                    AppTab.WHAT_IF -> {
                        SimulateScreen(
                            onSpeak = {
                                selectTab(AppTab.TALK)
                            },
                            onExplain = {
                                openRoute(Route.EVIDENCE)
                            },
                            onToast = showMessage,
                        )
                    }

                    AppTab.KHATA -> {
                        ActionsScreen(
                            onSpeak = {
                                openRoute(Route.KHATA)
                            },
                            onOpenWhatIf = {
                                selectTab(AppTab.WHAT_IF)
                            },
                            onToast = showMessage,
                        )
                    }
                }
            }
        }
    }
}
