package io.github.jeddchoi.mypage.session

import kotlinx.datetime.Instant
import kotlinx.datetime.isDistantFuture
import kotlinx.datetime.isDistantPast
import kotlin.time.Duration

data class SessionTimer(
    val startTime: Instant = Instant.DISTANT_PAST,
    val endTime: Instant? = null,
    val elapsedTime: Duration = Duration.ZERO,
    val remainingTime: Duration? = null,
    val totalTime: Duration? = null,
) {
    val isStarted =
        startTime.isDistantPast.not() &&
                elapsedTime == Duration.ZERO
    val hasEndTime =
        endTime?.isDistantFuture == false &&
                remainingTime?.isFinite() == true &&
                totalTime?.isFinite() == true


    override fun toString(): String {
        return "SessionTimer(startTime=$startTime, endTime=$endTime, elapsedTime=${elapsedTime.inWholeSeconds}, remainingTime=${remainingTime?.inWholeSeconds}, totalTime=${totalTime?.inWholeSeconds})"
    }
}