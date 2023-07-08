package io.github.jeddchoi.model

import kotlinx.datetime.Instant

sealed class UserSession {
    abstract val currentState: UserStateType

    object None : UserSession() {
        override val currentState: UserStateType = UserStateType.None
    }

    data class UsingSeat(
        val sessionId: String = "",
        val hasFailure: Boolean = false,
        val startSessionTime: Instant = Instant.DISTANT_PAST,
        val endSessionTime: Instant? = null,
        val startTime: Instant = Instant.DISTANT_PAST,
        val endTime: Instant? = null,
        val seatPosition: SeatPosition? = null,
        override val currentState: UserStateType = UserStateType.Reserved,
        val requestTypeAfterCurrentState: SeatFinderRequestType? = null,
    ) : UserSession()
}
