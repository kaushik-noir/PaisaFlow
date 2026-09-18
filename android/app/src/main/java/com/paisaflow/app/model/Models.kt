package com.paisaflow.app.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Visual tone used by metric tiles across the Home / Kosh UI.
 *
 * The model stays independent from Compose colors and theme classes.
 */
enum class MetricTone {
    PRIMARY,
    NEUTRAL,
    WARNING,
    SECONDARY,
}

/**
 * Logical icon identifiers resolved by the UI layer.
 *
 * Keeping icon names in the model avoids coupling data models
 * to Compose ImageVector or Android drawable types.
 */
enum class MetricIcon {
    PAYMENTS,
    TRENDING_UP,
    INVENTORY,
    PENDING,
    CHECK_CIRCLE,
    STOREFRONT,
    ALARM,
    GROUP,
}

/**
 * Supported action types for Home-screen tasks.
 */
enum class ActionKind {
    SEND,
    CHECK,
    CALL,
}

/**
 * Evidence classification attached to important values.
 */
@Serializable
enum class EvidenceLabel(
    val display: String,
) {
    @SerialName("FACT")
    FACT("FACT"),

    @SerialName("OBSERVATION")
    OBSERVATION("OBSERVATION"),

    @SerialName("ESTIMATE")
    ESTIMATE("ESTIMATE"),

    @SerialName("ASSUMPTION")
    ASSUMPTION("ASSUMPTION"),

    @SerialName("AI_INFERENCE")
    AI_INFERENCE("AI INFERENCE"),
}

/**
 * One Kosh Snapshot tile shown on the Home screen.
 */
data class KoshMetric(
    val title: String,
    val value: String,
    val valueSuffix: String?,
    val footer: String,
    val evidence: EvidenceLabel,
    val tone: MetricTone,
    val icon: MetricIcon,
    val footerIcon: MetricIcon,
) {
    val hasValueSuffix: Boolean
        get() = !valueSuffix.isNullOrBlank()
}

/**
 * One item from "Aaj ke Top 3 Kaam".
 */
data class TopAction(
    val rank: Int,
    val title: String,
    val subtitle: String,
    val kind: ActionKind,
) {
    val safeRank: Int
        get() = rank.coerceAtLeast(1)
}

/**
 * Payment-overdue alert shown on Home.
 */
data class OverdueAlert(
    val count: Int,
    val daysLate: Int,
    val counterparty: String,
    val amount: String,
) {
    val safeCount: Int
        get() = count.coerceAtLeast(0)

    val safeDaysLate: Int
        get() = daysLate.coerceAtLeast(0)

    val hasOverdueItems: Boolean
        get() = safeCount > 0
}

/**
 * Current Digital Twin status shown on the Home screen.
 */
data class TwinStatus(
    val name: String,
    val status: String,
    val isLive: Boolean,
)

/**
 * Complete model required by the primary Home / Kosh screen.
 */
data class HomeData(
    val ownerFirstName: String,
    val businessLabel: String,
    val todayLabel: String,
    val voiceExample: String,
    val overdue: OverdueAlert?,
    val metrics: List<KoshMetric>,
    val topActions: List<TopAction>,
    val twin: TwinStatus,
) {
    val hasOverdueAlert: Boolean
        get() = overdue?.hasOverdueItems == true

    val rankedTopActions: List<TopAction>
        get() = topActions.sortedBy { it.safeRank }
}

/**
 * Hindi Home v2 tile model.
 */
data class HiTile(
    val label: String,
    val value: String,
    val footer: String,
    val evidence: EvidenceLabel,
    val tone: MetricTone,
    val icon: MetricIcon,
)

/**
 * Hindi Home v2 task model.
 */
data class HiTask(
    val title: String,
    val subtitle: String,
    val buttonLabel: String,
    val kind: ActionKind,
    val primary: Boolean,
)

/**
 * Complete content model for the Hindi Home v2 screen.
 *
 * This stores formatted display content and should not be used as
 * the source of truth for financial calculations.
 */
data class HiHomeData(
    val businessName: String,
    val blockChip: String,
    val liveLine: String,
    val greeting: String,
    val dateLine: String,
    val question: String,

    val heroTitle: String,
    val heroSubtitle: String,
    val readyLine: String,
    val quickPrompts: List<String>,

    val alertDays: String,
    val alertName: String,
    val alertLine: String,

    val tiles: List<HiTile>,

    val twinName: String,
    val healthLine: String,
    val runwayText: String,

    val seasonTitle: String,
    val seasonBody: String,

    val modelAccuracy: String,

    val tasks: List<HiTask>,
    val lastVoiceEntry: String,
) {
    val primaryTask: HiTask?
        get() = tasks.firstOrNull { it.primary }

    val hasQuickPrompts: Boolean
        get() = quickPrompts.isNotEmpty()
}

/**
 * One entity extracted by the Brain and displayed on the
 * "Maine samjha" confirmation card.
 */
@Serializable
data class ExtractedFact(
    val label: String,
    val value: String,
    val subtitle: String? = null,
    val evidence: EvidenceLabel,

    @SerialName("is_highlight")
    val isHighlight: Boolean = false,
)

/**
 * One selectable answer for a clarification question.
 */
@Serializable
data class ClarificationOption(
    val value: Int,
    val label: String,
    val hint: String? = null,
)

/**
 * One follow-up question shown at a time during onboarding.
 */
@Serializable
data class ClarificationQuestion(
    val step: Int,

    @SerialName("total_steps")
    val totalSteps: Int,

    val question: String,
    val translation: String,

    @SerialName("unit_label")
    val unitLabel: String,

    val preselected: Int? = null,
    val options: List<ClarificationOption>,
) {
    val safeStep: Int
        get() = step.coerceAtLeast(1)

    val safeTotalSteps: Int
        get() = totalSteps.coerceAtLeast(safeStep)

    val progress: Float
        get() = if (safeTotalSteps == 0) {
            0f
        } else {
            safeStep.toFloat() / safeTotalSteps.toFloat()
        }
}

/**
 * Contract returned by:
 * POST /api/v1/conversation/message
 */
@Serializable
data class BrainUnderstanding(
    val transcript: String,
    val facts: List<ExtractedFact>,

    @SerialName("read_back")
    val readBack: String,

    @SerialName("next_question")
    val nextQuestion: ClarificationQuestion,

    @SerialName("onboarding_step")
    val onboardingStep: Int,

    @SerialName("onboarding_total")
    val onboardingTotal: Int,

    @SerialName("is_sample_data")
    val isSampleData: Boolean = false,
) {
    val safeOnboardingStep: Int
        get() = onboardingStep.coerceAtLeast(1)

    val safeOnboardingTotal: Int
        get() = onboardingTotal.coerceAtLeast(safeOnboardingStep)
}

/**
 * Request body sent to:
 * POST /api/v1/conversation/message
 */
@Serializable
data class MessageRequest(
    val text: String,
    val language: String = "hinglish",

    @SerialName("conversation_id")
    val conversationId: String? = null,
)

/**
 * Voice interaction states.
 *
 * These states should remain visible in the UI so the user always knows
 * whether the app is listening, processing, or responding.
 */
enum class VoiceState(
    val bannerText: String,
) {
    IDLE(
        "Tap karke boliye",
    ),

    LISTENING(
        "Sun rahe hain... (Listening)",
    ),

    PROCESSING(
        "Aapki baat samajh raha hoon...",
    ),

    UNDERSTANDING(
        "Business data check kar raha hoon...",
    ),

    RESPONDING(
        "Result ko simple bana raha hoon...",
    ),

    ERROR(
        "Kuch problem aa gayi. Dobara try karein.",
    );

    val showsWaveform: Boolean
        get() = this == LISTENING

    val isBusy: Boolean
        get() = this == PROCESSING ||
            this == UNDERSTANDING ||
            this == RESPONDING
}
