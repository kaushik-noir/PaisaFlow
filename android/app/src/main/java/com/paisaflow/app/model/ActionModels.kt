package com.paisaflow.app.model

/**
 * UI model for the collection / payment-recovery action card.
 *
 * The actual language-specific copy lives in the i18n layer.
 */
data class CollectionAction(
    val title: String,
    val place: String,
    val amount: String,
    val context: String,
    val why: String,
    val customer: String,
    val relationship: String,
    val phone: String,
    val draft: String,
)

/**
 * UI model for the inventory / stock re-order action card.
 *
 * Numeric stock values remain typed so the UI can safely calculate
 * progress, thresholds, and warnings without parsing strings.
 */
data class ReorderAction(
    val title: String,
    val supplier: String,
    val amount: String,
    val saving: String,

    val stockPct: Int,
    val stockDays: Double,
    val availableBags: Int,
    val minBags: Int,

    val product: String,
    val priceNote: String,
    val insight: String,
    val phone: String,
) {

    /**
     * Stock percentage clamped to a valid UI progress range.
     */
    val safeStockPct: Int
        get() = stockPct.coerceIn(0, 100)

    /**
     * Useful for warning-state UI.
     */
    val isBelowMinimumStock: Boolean
        get() = availableBags < minBags
}

/**
 * UI model for the finance decision action card.
 *
 * Values are intentionally strings here because this model represents
 * already-formatted, localised UI content rather than finance-engine data.
 */
data class FinanceAction(
    val title: String,
    val scheme: String,

    val unsafeLabel: String,
    val unsafeEmi: String,
    val unsafeNote: String,

    val safeLabel: String,
    val safeEmi: String,
    val safeNote: String,
)

/**
 * Complete content required by the Action Layer screen.
 */
data class ActionsData(
    val pendingCount: Int,
    val audioDuration: String,
    val collection: CollectionAction,
    val reorder: ReorderAction,
    val finance: FinanceAction,
) {

    /**
     * Safe value for badges/counters even if bad demo/API data slips through.
     */
    val safePendingCount: Int
        get() = pendingCount.coerceAtLeast(0)
}
