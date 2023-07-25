package io.github.jeddchoi.model

import kotlinx.datetime.Instant
import kotlinx.datetime.isDistantPast
import kotlin.time.Duration

data class SessionTimer(
    val startTime: Instant = Instant.DISTANT_PAST,
    val endTime: Instant? = null,
    val elapsedTime: Duration = Duration.ZERO,
    val remainingTime: Duration? = null,
    val totalTime: Duration? = null,
) {
    private val isStarted =
        startTime.isDistantPast.not() &&
                elapsedTime != Duration.ZERO
    private val hasEndTime =
        endTime != null &&
                remainingTime?.isFinite() == true &&
                totalTime?.isFinite() == true

    fun progress(remaining: Boolean): Float? {
        return if (hasEndTime && isStarted) {
            if (remaining) remainingTime?.inWholeSeconds?.div(
                totalTime?.inWholeSeconds?.toFloat() ?: 1f
            )?.coerceIn(0f, 1f)
            else elapsedTime.inWholeSeconds.div(totalTime?.inWholeSeconds?.toFloat() ?: 1f)
                .coerceIn(0f, 1f)
        } else {
            null
        }
    }

    override fun toString(): String {
        return "SessionTimer(startTime=$startTime, endTime=$endTime, elapsedTime=${elapsedTime.inWholeSeconds}, remainingTime=${remainingTime?.inWholeSeconds}, totalTime=${totalTime?.inWholeSeconds})"
    }
}

