package com.paisaflow.app.model

/**
 * One segment of the Data Provenance bar.
 *
 * @param label Human-readable label shown in the UI.
 * @param percent Share of this source in the provenance breakdown.
 * @param evidence Evidence classification for the source.
 */
data class ProvenanceShare(
    val label: String,
    val percent: Int,
    val evidence: EvidenceLabel,
) {
    /**
     * Safe percentage for progress bars and charts.
     */
    val safePercent: Int
        get() = percent.coerceIn(0, 100)
}

/**
 * Simple key/value row used inside evidence cards.
 */
data class KeyValue(
    val key: String,
    val value: String,
)

/**
 * A source card shown on the Evidence Mode screen.
 *
 * Examples:
 * - FACT
 * - OBSERVATION
 * - ESTIMATE
 */
data class EvidenceSource(
    val title: String,
    val evidence: EvidenceLabel,
    val percent: Int,
    val description: String,
    val rows: List<KeyValue>,
    val checks: List<String>,
    val footer: String?,
    val iconKind: SourceIcon,
) {
    /**
     * Safe percentage for progress indicators.
     */
    val safePercent: Int
        get() = percent.coerceIn(0, 100)

    /**
     * Convenience helper for UI sections that only need to know
     * whether optional footer text exists.
     */
    val hasFooter: Boolean
        get() = !footer.isNullOrBlank()
}

/**
 * Icon type used by evidence-source cards.
 */
enum class SourceIcon {
    MIC,
    STOREFRONT,
    TERMINAL,
}

/**
 * One portion of the simple "bucket" metaphor.
 */
data class BucketShare(
    val buckets: Int,
    val label: String,
    val tone: MetricTone,
) {
    /**
     * Prevents negative bucket counts from reaching the UI.
     */
    val safeBuckets: Int
        get() = buckets.coerceAtLeast(0)
}

/**
 * Plain-language explanation shown below evidence details.
 */
data class SimpleExplanation(
    val question: String,
    val metaphorTitle: String,
    val metaphorSubtitle: String,
    val shares: List<BucketShare>,
    val plainTalk: List<String>,
) {
    /**
     * Total number of buckets represented by the metaphor.
     */
    val totalBuckets: Int
        get() = shares.sumOf { it.safeBuckets }
}

/**
 * Short field story / testimonial shown as supporting context.
 *
 * This model only stores display copy. The caller is responsible for
 * clearly marking fictional/demo stories when they are not real records.
 */
data class FieldStory(
    val person: String,
    val quote: String,
)

/**
 * Complete model required by the Evidence Mode screen.
 */
data class EvidenceData(
    val ownerInitials: String,
    val ownerName: String,
    val ownerMeta: String,
    val verifiedScore: String,
    val provenance: List<ProvenanceShare>,
    val sources: List<EvidenceSource>,
    val explanation: SimpleExplanation,
    val story: FieldStory,
) {
    /**
     * Sum of the clamped provenance percentages.
     *
     * Useful for debug/UI validation. A well-formed provenance bar
     * will normally total 100.
     */
    val provenanceTotal: Int
        get() = provenance.sumOf { it.safePercent }

    /**
     * Indicates whether provenance percentages add up to 100.
     */
    val hasCompleteProvenance: Boolean
        get() = provenanceTotal == 100
}
