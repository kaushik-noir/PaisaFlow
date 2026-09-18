package com.paisaflow.app.model

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
    /**
     * Convenience helper for UI code.
     */
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
    /**
     * Prevents invalid negative/zero ranks from reaching UI badges.
     */
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
    /**
     * Convenience helper for conditional overdue UI.
     */
    val hasOverdueAlert: Boolean
        get() = overdue?.hasOverdueItems == true

    /**
     * Top actions sorted by rank without mutating source data.
     */
    val rankedTopActions: List<TopAction>
        get() = topActions.sortedBy { it.safeRank }
}

/**
 * Hindi Home v2 tile model.
 *
 * Despite the name, this is still a generic UI model; text itself is supplied
 * by demo/i18n content.
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
 * This class stores already-formatted display copy. It should not be used
 * as the source of truth for financial calculations.
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
    /**
     * First primary task, if one exists.
     */
    val primaryTask: HiTask?
        get() = tasks.firstOrNull { it.primary }

    /**
     * Useful for UI layouts that hide prompt chips when there are none.
     */
    val hasQuickPrompts: Boolean
        get() = quickPrompts.isNotEmpty()
}
