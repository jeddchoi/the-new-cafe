package io.github.jeddchoi.mypage.session

import kotlinx.datetime.Instant
import kotlin.time.Duration

data class SessionTimer(
    val startTime: Instant = Instant.DISTANT_PAST,
    val endTime: Instant = Instant.DISTANT_FUTURE,
    val elapsedTime: Duration = Duration.ZERO,
    val remainingTime: Duration = Duration.INFINITE,
    val totalTime: Duration = Duration.INFINITE,
)