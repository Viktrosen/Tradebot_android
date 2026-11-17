package ru.bolotov.feature.strategy.domain.model

/** Parameters edited by the Voting configuration dialog before sending them to the gateway. */
data class VotingWeights private constructor(val values: Map<String, Int>) {
    val unsupportedParticipants: Set<String> get() = values.keys - SUPPORTED_PARTICIPANTS.toSet()
    val validationError: VotingWeightsError?
        get() = when {
            unsupportedParticipants.isNotEmpty() -> VotingWeightsError.UNSUPPORTED_PARTICIPANTS
            values.values.any { it !in MIN_WEIGHT..MAX_WEIGHT } -> VotingWeightsError.INVALID_WEIGHT
            values.size < MIN_PARTICIPANTS -> VotingWeightsError.TOO_FEW_PARTICIPANTS
            else -> null
        }

    val isValid: Boolean get() = validationError == null
    val totalWeight: Int get() = values.values.sum()

    fun withParticipant(participant: String, enabled: Boolean): VotingWeights {
        require(participant in SUPPORTED_PARTICIPANTS)
        return from(if (enabled) values + (participant to (values[participant] ?: MIN_WEIGHT)) else values - participant)
    }

    fun withWeight(participant: String, weight: Int): VotingWeights {
        require(participant in SUPPORTED_PARTICIPANTS && participant in values)
        require(weight in MIN_WEIGHT..MAX_WEIGHT)
        return from(values + (participant to weight))
    }

    /** Never silently drop unsupported keys or invent participants while saving. */
    fun toPayload(): Map<String, Int> {
        check(isValid) { "Invalid Voting configuration: $validationError" }
        return values.toMap()
    }

    companion object {
        val SUPPORTED_PARTICIPANTS = listOf("EMA", "RSI", "MACD", "BB", "SUPER_TREND", "VWAP")
        const val MIN_WEIGHT = 1
        const val MAX_WEIGHT = 10
        const val MIN_PARTICIPANTS = 2

        fun from(weights: Map<String, Int>): VotingWeights = VotingWeights(weights.toMap())
    }
}
