package io.github.jeddchoi.model

import kotlinx.datetime.Instant

data class UserSession(
    val sessionId: String = "",
    val hasFailure: Boolean = false,
    val startSessionTime: Instant = Instant.DISTANT_PAST,
    val endSessionTime: Instant? = null,
    val startTime: Instant = Instant.DISTANT_PAST,
    val endTime: Instant? = null,
    val currentState: UserStateType = UserStateType.None,
    val requestTypeAfterCurrentState: SeatFinderRequestType? = null,
)