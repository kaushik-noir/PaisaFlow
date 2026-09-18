package com.paisaflow.app.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request body for:
 * POST /api/v1/simulation/loan
 *
 * Defaults represent the fictional demo scenario only.
 * Production flows should explicitly supply the user's confirmed values.
 */
@Serializable
data class LoanSimRequest(
    @SerialName("cash_on_hand")
    val cashOnHand: Int = 100_000,

    @SerialName("monthly_revenue")
    val monthlyRevenue: Int = 78_000,

    @SerialName("monthly_costs")
    val monthlyCosts: Int = 46_000,

    @SerialName("fixed_costs")
    val fixedCosts: Int = 30_000,

    val loan: Int = 900_000,

    @SerialName("annual_rate_pct")
    val annualRatePct: Double = 11.0,

    @SerialName("tenure_months")
    val tenureMonths: Int = 60,

    @SerialName("new_units")
    val newUnits: Int = 6,

    @SerialName("capex_per_unit")
    val capexPerUnit: Int = 60_000,

    @SerialName("winter_drop_pct")
    val winterDropPct: Double = 28.0,

    @SerialName("sales_drop_pct")
    val salesDropPct: Double = 20.0,

    @SerialName("cost_rise_pct")
    val costRisePct: Double = 15.0,

    val language: String = "hi",
) {

    /**
     * Basic client-side validation.
     *
     * The backend must still perform its own authoritative validation.
     */
    val validationErrors: List<String>
        get() = buildList {
            if (cashOnHand < 0) {
                add("Cash on hand cannot be negative.")
            }

            if (monthlyRevenue < 0) {
                add("Monthly revenue cannot be negative.")
            }

            if (monthlyCosts < 0) {
                add("Monthly costs cannot be negative.")
            }

            if (fixedCosts < 0) {
                add("Fixed costs cannot be negative.")
            }

            if (fixedCosts > monthlyCosts) {
                add("Fixed costs cannot exceed total monthly costs.")
            }

            if (loan <= 0) {
                add("Loan amount must be greater than zero.")
            }

            if (annualRatePct < 0.0 || annualRatePct > 100.0) {
                add("Annual interest rate must be between 0 and 100.")
            }

            if (tenureMonths <= 0) {
                add("Loan tenure must be greater than zero.")
            }

            if (newUnits < 0) {
                add("New units cannot be negative.")
            }

            if (capexPerUnit < 0) {
                add("CAPEX per unit cannot be negative.")
            }

            if (winterDropPct !in 0.0..100.0) {
                add("Winter drop percentage must be between 0 and 100.")
            }

            if (salesDropPct !in 0.0..100.0) {
                add("Sales drop percentage must be between 0 and 100.")
            }

            if (costRisePct < 0.0) {
                add("Cost rise percentage cannot be negative.")
            }

            if (language.isBlank()) {
                add("Language code cannot be blank.")
            }
        }

    val isValid: Boolean
        get() = validationErrors.isEmpty()

    /**
     * Throws when the request contains obviously invalid client-side values.
     *
     * Useful immediately before making a network request.
     */
    fun requireValid(): LoanSimRequest {
        require(isValid) {
            validationErrors.joinToString(separator = " ")
        }

        return this
    }
}

/**
 * Known scenario states returned by the finance engine.
 *
 * The wire model keeps [ScenarioOut.status] as String so unexpected/new
 * backend values do not break deserialization.
 */
enum class ScenarioStatus {
    SAFE,
    TIGHT,
    DEFICIT,
    UNKNOWN;

    companion object {
        fun from(
            value: String,
        ): ScenarioStatus {
            return entries.firstOrNull {
                it != UNKNOWN && it.name.equals(
                    value.trim(),
                    ignoreCase = true,
                )
            } ?: UNKNOWN
        }
    }
}

/**
 * One stress-test scenario returned by the loan simulation.
 */
@Serializable
data class ScenarioOut(
    val key: String,
    val title: String,
    val subtitle: String? = null,

    /**
     * Expected values currently include SAFE, TIGHT, and DEFICIT.
     */
    val status: String,

    val revenue: Int,
    val costs: Int,
    val surplus: Int,
    val emi: Int,

    @SerialName("surplus_after_emi")
    val surplusAfterEmi: Int,

    @SerialName("repayment_burden_pct")
    val repaymentBurdenPct: Int,

    val note: String? = null,
) {

    val statusType: ScenarioStatus
        get() = ScenarioStatus.from(status)

    val isDeficit: Boolean
        get() = statusType == ScenarioStatus.DEFICIT ||
            surplusAfterEmi < 0

    /**
     * Safe value for progress/gauge components.
     *
     * The original backend value remains available through
     * [repaymentBurdenPct].
     */
    val repaymentBurdenForGauge: Int
        get() = repaymentBurdenPct.coerceIn(0, 100)

    val hasSubtitle: Boolean
        get() = !subtitle.isNullOrBlank()

    val hasNote: Boolean
        get() = !note.isNullOrBlank()
}

/**
 * Safer alternative plan calculated by the finance engine.
 */
@Serializable
data class SafePlanOut(
    val loan: Int,
    val units: Int,
    val emi: Int,

    @SerialName("winter_surplus_after_emi")
    val winterSurplusAfterEmi: Int,

    @SerialName("buffer_to_keep")
    val bufferToKeep: Int,

    val advice: String,
) {
    val survivesWinterScenario: Boolean
        get() = winterSurplusAfterEmi >= 0
}

/**
 * Response returned by:
 * POST /api/v1/simulation/loan
 */
@Serializable
data class LoanSimResponse(
    val question: String,
    val emi: Int,

    @SerialName("total_interest")
    val totalInterest: Int,

    @SerialName("safe_limit")
    val safeLimit: Int,

    @SerialName("base_surplus_after_emi")
    val baseSurplusAfterEmi: Int,

    @SerialName("winter_surplus_after_emi")
    val winterSurplusAfterEmi: Int,

    @SerialName("winter_deficit_3_months")
    val winterDeficit3Months: Int,

    val scenarios: List<ScenarioOut>,

    @SerialName("safe_plan")
    val safePlan: SafePlanOut? = null,

    val evidence: String = "ESTIMATE",
    val note: String = "",
) {

    val hasSafePlan: Boolean
        get() = safePlan != null

    val baseScenarioHasDeficit: Boolean
        get() = baseSurplusAfterEmi < 0

    val winterScenarioHasDeficit: Boolean
        get() = winterSurplusAfterEmi < 0

    /**
     * Finds a scenario without exposing case-sensitivity concerns to UI code.
     */
    fun scenario(
        key: String,
    ): ScenarioOut? {
        return scenarios.firstOrNull {
            it.key.equals(
                key.trim(),
                ignoreCase = true,
            )
        }
    }
}

/**
 * Indian rupee grouping.
 *
 * Examples:
 * 900000    -> ₹9,00,000
 * -9408     -> -₹9,408
 * 10000000  -> ₹1,00,00,000
 *
 * Uses the number's string representation rather than abs(), so edge values
 * such as Int.MIN_VALUE are handled safely.
 */
fun Int.inr(): String = formatIndianRupees(toString())

/**
 * Long overload for future larger financial values.
 */
fun Long.inr(): String = formatIndianRupees(toString())

private fun formatIndianRupees(
    rawNumber: String,
): String {
    val isNegative = rawNumber.startsWith("-")
    val digits = rawNumber.removePrefix("-")

    if (digits.isEmpty()) {
        return if (isNegative) "-₹0" else "₹0"
    }

    val grouped = if (digits.length <= 3) {
        digits
    } else {
        val lastThree = digits.takeLast(3)
        var remaining = digits.dropLast(3)
        val groups = mutableListOf<String>()

        while (remaining.length > 2) {
            groups.add(
                index = 0,
                element = remaining.takeLast(2),
            )
            remaining = remaining.dropLast(2)
        }

        if (remaining.isNotEmpty()) {
            groups.add(
                index = 0,
                element = remaining,
            )
        }

        groups.joinToString(separator = ",") + "," + lastThree
    }

    return buildString {
        if (isNegative) {
            append("-")
        }

        append("₹")
        append(grouped)
    }
}
