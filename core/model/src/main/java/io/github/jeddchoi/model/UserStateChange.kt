package io.github.jeddchoi.model

import kotlinx.datetime.Instant

data class UserStateChange(
    val requestType: SeatFinderRequestType = SeatFinderRequestType.Quit,
    val success: Boolean = false,
    val resultSeatState: SeatStateType = SeatStateType.Empty,
    val resultUserState: UserStateType = UserStateType.None,
    val reason: UserStateChangeReason = UserStateChangeReason.UserAction,
    val timestamp: Instant = Instant.DISTANT_FUTURE,
)
