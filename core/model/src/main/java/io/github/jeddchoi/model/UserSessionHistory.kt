package io.github.jeddchoi.model

import kotlinx.datetime.Instant

data class UserSessionHistory(
    val sessionId: String = "",
    val startTime: Instant = Instant.DISTANT_PAST,
    val endTime: Instant? = null,
    val hasFailure: Boolean = false,
    val seatPosition: SeatPosition = SeatPosition(),
    val stateChanges: List<UserStateChange> = emptyList(),
)
